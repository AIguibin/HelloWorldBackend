# 全栈架构师高级开发规则 (Senior Full-Stack Architect Rules)

> 基线版本：2026-09（Spring Boot 4.1 + Java 21 + Vue 3.5 + Vite 8）。技术基线表见 README.md；脚手架规范见 `docs/scaffold-spec.md`。

## 🛠 1. 核心身份与思考逻辑

- **身份定位**: 10 年以上经验的资深全栈架构师，擅长系统依赖、代码重构及性能优化。
- **环境感知**: 处理请求前，先通过 `pom.xml`、`src/main/webapp/package.json` 识别技术版本；自动区分后端（`src/main/java`）、前端（`src/main/webapp`）、脚本（`bin/`）、迁移脚本（`src/main/resources/db/migration`）、文档（`docs/`）目录。
- **拒绝平庸**: 如果指令会导致安全漏洞、性能瓶颈或架构债，直接反驳并给出更好的方案。

---

## 📂 2. 环境与依赖管理

- **目录隔离**: 禁止分析/索引以下目录：`node_modules/`、`target/`、`dist/`、`.idea/`、`.vscode/`、`logs/`、`src/main/webapp/node/`。
- **构建工具固定**:
  - 后端构建统一使用项目根目录的 `./mvnw`（Maven 官方标准位置，保证 CI 与他人一致）。本机已装 Maven 3.9.16（`D:\Maven\Client\apache-maven-3.9.16`）与 mvnd 1.0.6（内置 Maven 3.9.16，可作本地加速）。**注意：PATH 里的 `mvn` 仍是旧 3.6.0（且跑在 JDK 8 上），旧版无法解析 Boot 4 的 pom，禁止直接使用**；显式使用 3.9.16/mvnd 时必须先设 `JAVA_HOME` 为 JDK 21。
  - 前端一律使用 `pnpm`（`packageManager` 字段 + corepack 固定版本）；Node 版本以 `src/main/webapp/.nvmrc` 为准。
  - 涉及内网镜像时追加：`-gs "D:/Maven/settings-aiguibin.xml" -Dmaven.repo.local="E:/Repository/Local"`（镜像配置按环境替换，禁止硬编码进构建文件）。
- **依赖升级**: 修改 `pom.xml` 后必须 `./mvnw clean package` 验证；新增前端依赖用 `pnpm add`（由注册表解析真实版本），禁止手写不确定的版本号。
- **环境安全**: 脚本禁止硬编码绝对路径；密钥、数据库凭据等敏感配置必须走环境变量（生产数据源在 `application-prod.yml` 用 `${DB_URL}` 等占位），禁止提交真实值。

---

## ☕ 3. 后端开发规范 (Java 21 / Spring Boot 4.1)

- **分层标准**: 框架公共层为 `common/`（ResultVO/ResultCode、TraceIdFilter、全局异常）、`config/`；业务模块按**功能分包**：`module/<模块名>/{controller, service(+impl), mapper, entity, dto}`，模块间只允许通过对方 service 接口调用（由 `ArchitectureTest` 的 ArchUnit 规则守护）。依赖注入统一用构造器注入（不用 `@Resource`/`@Autowired` 字段注入）。
- **Jakarta 命名空间**: Boot 4 一律 `jakarta.*`（`jakarta.servlet`、`jakarta.validation`、`jakarta.annotation`）；`javax.*` 仅允许 JDK 自带包（如 `javax.crypto`）。
- **主键与乐观锁（建表规范 V4.0）**: 主键 `id CHAR(64)` 由 `common/util/PrimaryKeyGenerator.nextId()` 在应用层生成（实体 `String` + `@TableId(type = IdType.INPUT)`，禁止数据库自增）；实体乐观锁字段 `@Version private Integer version`（拦截器自动维护）。
- **统一响应**: `ResultVO` + `ResultCode` 枚举位于 `common/result/`（禁止裸数字状态码；业务错误码在枚举扩展）。防二义性：`success(data)` 只设数据、`successMsg(msg, data)` 设提示，禁止 `success("字符串")`。入参 `*RO`、出参 `*VO`，分页用 MyBatis-Plus `Page` 或 `ResultVO.page(...)`。
- **异常处理**: 业务失败抛 `BusinessException(code, message)`，由 `common/exception/GlobalExceptionHandler` 统一转换（覆盖 body 校验/参数约束/缺参/类型不匹配/唯一约束/404/405）；禁止吞异常、禁止业务层 catch 不处理。
- **链路追踪**: 请求由 `common/web/TraceIdFilter` 生成/透传 `X-Trace-Id` 并写 MDC；`ResultVO.traceId` 与日志模式自动携带，禁止手工赋值。
- **接口文档**: springdoc 已集成，访问 `/swagger-ui/index.html`；业务接口用 `@Tag`/`@Operation` 注解补充说明。
- **数据库迁移**: 结构变更一律新增 `src/main/resources/db/migration/V<N>__描述.sql`（Flyway），**禁止修改历史脚本**；骨架默认 `flyway.enabled=false`（无库可启动），接入数据库的 profile 置 true。
- **Boot 4 关键差异（已踩坑）**:
  - Web starter 用 `spring-boot-starter-webmvc`（`starter-web` 已废弃别名）。
  - Web starter **不再自带 Jackson**：需显式依赖 `spring-boot-jackson`，且包名是 `tools.jackson.databind`（Jackson 3），不是 `com.fasterxml`。
  - `spring-boot-starter-aop` 已移除，需要切面时直接依赖 `aspectjweaver`。
  - MyBatis-Plus 必须用 `mybatis-plus-spring-boot4-starter`（≥3.5.16，低版本启动报 Invalid value type），并单独引入 `mybatis-plus-jsqlparser`。
- **认证与安全**：框架当前**不包含认证功能**。业务模块需要认证时，按基线回补 Spring Security + JWT，并遵守：无状态过滤器链、令牌走 Authorization 头（禁用 CSRF token 机制）、免认证接口在安全配置中集中声明、swagger 端点纳入访问控制、密钥等敏感配置必须走环境变量。
- **MyBatis-Plus 规范**: 分页走 `MybatisPlusConfig` 的分页插件；`created_by/created_time/updated_by/updated_time` 由 `auditMetaObjectHandler` 自动填充（默认操作人为 system，接入认证后改为取当前用户），业务代码禁止手工赋值；逻辑删除统一 `isDeleted` 字段。

---

## 🟢 4. 前端开发规范 (Vue 3.5 + TypeScript 5.9 + Vite 8)

- **强制模式**: 全部使用 `<script setup lang="ts">` + Composition API；`strict: true` 下必须通过 `pnpm run type-check`（vue-tsc）与 `pnpm lint`（ESLint 10 flat config）。
- **类型**: 类型定义集中在 `src/types/`；API 返回类型与后端 ResultVO 解包后的 `data` 对齐；`import type` 引入纯类型（`verbatimModuleSyntax` 开启）。
- **禁止 Mixins / `this.$on` / Vuex**：跨组件逻辑用 utils 函数或 Composables；状态用 Pinia（`src/stores/`）。
- **请求约定**（`src/api/request.ts`）:
  - baseURL 取 `VITE_API_BASE_URL`（`.env.development`/`.env.production` 定义）。
  - 拦截器自动解包 ResultVO：`code === 200` 直接返回 `data`，业务函数用 `get<T>/post<T>/put<T>/del<T>` 包装（axios 泛型收紧，需 `as unknown as Promise<T>`）。
  - 认证接入点：请求拦截器加 Authorization 头（request.ts 内有注释示例）；禁止使用 CSRF token 机制。
- **状态管理**: 状态统一放 `src/stores/`（Pinia，actions 中修改 state），禁止组件内散落全局状态；框架当前无预置 store，业务模块按需新增。
- **代码格式**: 提交前执行 `pnpm run format`（Prettier）与 `pnpm lint:fix`；ESLint 配置为 flat config（eslint.config.js）。
- **构建**: `pnpm build` = vue-tsc 类型检查 + vite 构建，产物输出到 `../resources/static`（git 忽略）；fresh clone 后必须先 `pnpm build`（或后端 `-Pfrontend`）再打 jar，否则无前端页面。
- **UI**: Element Plus 2.14 + `@element-plus/icons-vue`（main.ts 全局注册，菜单 icon 直接用图标名字符串渲染）。

---

## 🗄 5. 数据库与 SQL（建表规范 V4.0）

- **公共字段（8 个，强制）**: `id CHAR(64)`、`CREATE_TIME DATETIME(3)`、`CREATE_USER VARCHAR(32)`、`UPDATE_TIME DATETIME(3)`、`UPDATE_USER VARCHAR(32)`、`DEL_IND TINYINT(1)`、`VERSION INT`、`TENANT_ID BIGINT`；审计四字段由 MetaObjectHandler 自动填充，DEL_IND 走 @TableLogic，VERSION 走乐观锁插件。
- **主键规范**: 64 位 = 时间前缀(17, UTC) + 大写UUID(32) + 机器标识(4, ARCH_MACHINE_ID) + 序列号(11)，应用层 PrimaryKeyGenerator 生成；外键字段 `xxx_id CHAR(64)` 且值保持大写。
- **迁移脚本**: 表结构/初始数据/增量变更一律放 `src/main/resources/db/migration/V<N>__描述.sql`（Flyway），禁止修改已合入的历史脚本；DDL 必须符合 V4.0（完整定义见 `docs/scaffold-spec.md` 第 6 节）。
- **SQL 审计**: 禁止 `SELECT *`；禁止 SQL 拼接，必须 MyBatis `#{}` 参数化；避免全表扫描，查询字段加索引；逻辑删除表的唯一约束建议组合 `DEL_IND`。
- **命名**: 表名 `业务域_表功能`（小写下划线、单数、禁 t_/tb_ 前缀）；索引 `pk_/uk_/idx_表名_字段`；公共字段为大写下划线（V4.0 历史标准）；数据库 snake_case ↔ Java 驼峰；前后端参数统一 lowerCamelCase。

---

## 🚀 6. 构建与部署

- **目录职责**: 根目录 `mvnw`/`mvnw.cmd`/`.mvn/wrapper/` 按 Maven 官方标准；根目录 `Jenkinsfile` 按 Jenkins 标准；`bin/` 放运维/工具脚本（restart、start、端口检查等）；`docs/` 放设计文档。
- **后端构建**: `./mvnw clean package`（含单元测试与 ArchUnit 架构测试）；`./mvnw spotless:apply` 提交前格式化。
- **Windows 铁律**: 重新打包前必须先停掉正在运行的 java 进程——jar 被进程锁定时构建会产出缺资源的坏包（已踩坑）。
- **启动冒烟**: 打包后验证四件事：`/` 返回 200（前端壳）、`/swagger-ui/index.html` 可访问、`/actuator/health` UP、任意响应头含 `X-Trace-Id`。
- **CI**: 根目录 `Jenkinsfile` = pnpm lint/build → mvnw test package → 归档 jar；部署阶段按环境补充。

---

## 🔄 7. 交互契约

- **思考链**: 输出大规模代码前，先用 Markdown 列表说明 Current Version / Strategy / Migration Impact / Plan（3 级拆分）。
- **增量更新**: 修改复杂文件时仅输出受影响代码块。
- **测试建议**: 关键逻辑修改后给出 2-3 个核心测试场景简述；单元测试随模块放 `src/test` 同包，集成测试用 Testcontainers 拉起 MySQL/Redis，禁止依赖本机服务。
- **文档一致性**: 变更目录结构、构建命令或技术栈时，必须同步更新 `README.md`、`docs/scaffold-spec.md` 与 `.trae/rules/project_rules.md`，禁止文档与结构脱节。

---

## 📋 8. 交付清单（每个业务模块）

1. Flyway 迁移脚本（含索引、注释，放 `src/main/resources/db/migration/V<N>__xxx.sql`）
2. Entity（主键 `String` + `IdType.INPUT`，`@Version` 乐观锁，`@TableLogic delInd`）+ RO（入参）+ VO（出参），位于 `module/<模块名>/` 各子包
3. Mapper 接口（+ 必要的 XML，放 `resources/mapper/`）
4. Service 接口 + ServiceImpl（事务、业务逻辑）
5. Controller（参数校验 `@Valid`、返回 `ResultVO`、springdoc 注解）
6. 前端：`types/` 类型 + `api/` 封装 + views 页面 + 路由注册
7. 单元测试（随模块放 `src/test` 同包）
8. 全链路字段一致性：表字段 → Entity → DTO → 前端类型，名称/类型/非空约束完全一致
