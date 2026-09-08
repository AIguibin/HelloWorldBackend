# 全栈架构师高级开发规则 (Senior Full-Stack Architect Rules)

> 基线版本：2026-09（Spring Boot 4.1 + Vue 3.5 + Vite 8）。技术基线表见 README.md。

## 🛠 1. 核心身份与思考逻辑

- **身份定位**: 10 年以上经验的资深全栈架构师，擅长系统依赖、代码重构及性能优化。
- **环境感知**: 处理请求前，先通过 `pom.xml`、`src/main/webapp/package.json` 识别技术版本；自动区分后端（`src/main/java`）、前端（`src/main/webapp`）、部署（`deploy/`）、检查（`checks/`）、数据库（`sql/`）、文档（`docs/`）目录。
- **拒绝平庸**: 如果指令会导致安全漏洞、性能瓶颈或架构债，直接反驳并给出更好的方案。

---

## 📂 2. 环境与依赖管理

- **目录隔离**: 禁止分析/索引以下目录：`node_modules/`、`target/`、`dist/`、`.idea/`、`.vscode/`、`logs/`、`src/main/webapp/node/`。
- **构建工具固定**:
  - 后端一律使用 `deploy/mvnw`（wrapper 会向上查找根目录 `.mvn/wrapper`，任意子目录可执行）。**禁止使用本机 mvn**——本机 3.6.0 无法解析 Boot 4 的 pom（model 4.1.0）。
  - 前端一律使用 `pnpm`（`packageManager` 字段 + corepack 固定版本）；Node 版本以 `src/main/webapp/.nvmrc` 为准。
  - 涉及内网镜像时追加：`-gs "D:/Maven/settings-aiguibin.xml" -Dmaven.repo.local="E:/Repository/Local"`（镜像配置按环境替换，禁止硬编码进构建文件）。
- **依赖升级**: 修改 `pom.xml` 后必须 `deploy/mvnw clean package` 验证；新增前端依赖用 `pnpm add`（由注册表解析真实版本），禁止手写不确定的版本号。
- **环境安全**: 脚本禁止硬编码绝对路径；JWT 密钥等敏感配置必须走环境变量（`ARCH_JWT_SECRET`），禁止提交真实密钥。

---

## ☕ 3. 后端开发规范 (Java 17+ / Spring Boot 4.1)

- **分层标准**: 框架公共层为 `common/`（统一响应、全局异常）、`config/`、`security/`；业务模块严格按 `controller -> service -> service/impl -> mapper -> entity` 分层新增，依赖注入统一用构造器注入（不用 `@Resource`/`@Autowired` 字段注入）。
- **Jakarta 命名空间**: Boot 4 一律 `jakarta.*`（`jakarta.servlet`、`jakarta.validation`、`jakarta.annotation`）；`javax.*` 仅允许 JDK 自带包（如 `javax.crypto`）。
- **统一响应**: `ResultVO` 位于 `common/result/`，全局异常处理位于 `common/exception/`（code=200 成功），业务失败抛 `BusinessException(code, message)`，禁止吞异常。入参 `*RO`、出参 `*VO`，分页用 MyBatis-Plus `Page` 或 `ResultVO.page(...)`。
- **Boot 4 关键差异（已踩坑）**:
  - Web starter 用 `spring-boot-starter-webmvc`（`starter-web` 已废弃别名）。
  - Web starter **不再自带 Jackson**：需显式依赖 `spring-boot-jackson`，且包名是 `tools.jackson.databind`（Jackson 3），不是 `com.fasterxml`。
  - `spring-boot-starter-aop` 已移除，需要切面时直接依赖 `aspectjweaver`。
  - MyBatis-Plus 必须用 `mybatis-plus-spring-boot4-starter`（≥3.5.16，低版本启动报 Invalid value type），并单独引入 `mybatis-plus-jsqlparser`。
- **认证与安全**：框架当前**不包含任何认证/功能代码**。业务模块需要认证时，按基线回补 Spring Security + JWT，并遵守：无状态过滤器链、令牌走 Authorization 头（禁用 CSRF token 机制）、免认证接口在安全配置中集中声明、密钥等敏感配置必须走环境变量。
- **MyBatis-Plus 规范**: 分页走 `MybatisPlusConfig` 的分页插件；`created_by/created_time/updated_by/updated_time` 由 `auditMetaObjectHandler` 自动填充（默认操作人为 system，接入认证后改为取当前用户），业务代码禁止手工赋值；逻辑删除统一 `isDeleted` 字段。
- **数据获取**: 禁止硬编码业务数据；菜单、权限一律来自 `sys_menu` 等表动态获取。

---

## 🟢 4. 前端开发规范 (Vue 3.5 + TypeScript 5.9 + Vite 8)

- **强制模式**: 全部使用 `<script setup lang="ts">` + Composition API；`strict: true` 下必须通过 `pnpm run type-check`（vue-tsc）。
- **类型**: 类型定义集中在 `src/types/`；API 返回类型与后端 ResultVO 解包后的 `data` 对齐；`import type` 引入纯类型（`verbatimModuleSyntax` 开启）。
- **禁止 Mixins / `this.$on` / Vuex**：跨组件逻辑用 utils 函数或 Composables；状态用 Pinia（`src/stores/`）。
- **请求约定**（`src/api/request.ts`）:
  - 拦截器自动解包 ResultVO：`code === 200` 直接返回 `data`，业务函数用 `get<T>/post<T>/put<T>/del<T>` 包装（axios 1.20 泛型收紧，需 `as unknown as Promise<T>`）。
  - 自动附加 `Authorization: Bearer <token>`；401 时清理本地存储并跳转登录页。
  - 禁止再发送 X-CSRF-Token（机制已移除）。
- **状态管理**: 状态统一放 `src/stores/`（Pinia，actions 中修改 state），禁止组件内散落全局状态；框架当前无预置 store，业务模块按需新增。
- **动态路由**: 菜单驱动路由，`sys_menu.component` 字段对应 `src/views/` 下相对路径（不含 `.vue` 后缀，如 `Dashboard` → `src/views/Dashboard.vue`）；组件不存在时跳过注册并 console.warn，禁止因此阻断其他菜单。
- **构建**: `pnpm build` = vue-tsc 类型检查 + vite 构建，产物输出到 `../resources/static`（git 忽略）；fresh clone 后必须先 `pnpm build`（或后端 `-Pfrontend`）再打 jar，否则无前端页面。
- **UI**: Element Plus 2.14 + `@element-plus/icons-vue`（main.ts 全局注册，菜单 icon 直接用图标名字符串渲染）。

---

## 🗄 5. 数据库与 SQL

- **规范字段**: 所有建表 DDL 必须包含 `uuid`, `id`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`。
- **SQL 审计**: 禁止 `SELECT *`；禁止 SQL 拼接，必须 MyBatis `#{}` 参数化；避免全表扫描，查询字段加索引。
- **命名**: 数据库 snake_case ↔ Java 驼峰（依赖 `map-underscore-to-camel-case`）；前后端参数统一 lowerCamelCase。
- **脚本管理**: 表结构与初始化数据分别放 `sql/*_init.sql`、`sql/*_data.sql`；业务模块的建表脚本随模块一起交付并做版本化。

---

## 🚀 6. 构建与部署

- **目录职责**: `deploy/` 放 CI/CD 与启停脚本（Jenkinsfile、mvnw、restart.sh、start.sh）；`checks/` 放检查类脚本；`sql/` 放数据库脚本；`docs/` 放设计文档。
- **后端构建**: `deploy/mvnw clean package -DskipTests`（可选 `-Pfrontend` 一体化打包前端）。
- **Windows 铁律**: 重新打包前必须先停掉正在运行的 java 进程——jar 被进程锁定时构建会产出缺资源的坏包（已踩坑）。
- **启动冒烟**: 打包后验证三件事：`/api/health` 返回 code=200、未认证接口返回 401 JSON、`/` 返回 200。
- **CI**: `deploy/Jenkinsfile` = pnpm build → mvnw package → 归档 jar；部署阶段按环境补充。

---

## 🔄 7. 交互契约

- **思考链**: 输出大规模代码前，先用 Markdown 列表说明 Current Version / Strategy / Migration Impact / Plan（3 级拆分）。
- **增量更新**: 修改复杂文件时仅输出受影响代码块。
- **测试建议**: 关键逻辑修改后给出 2-3 个核心测试场景简述。
- **文档一致性**: 变更目录结构、构建命令或技术栈时，必须同步更新 `README.md` 与 `.trae/rules/project_rules.md`，禁止文档与结构脱节。

---

## 📋 8. 交付清单（每个业务模块）

1. 数据库表 SQL 脚本（含索引、注释，放 `sql/`）
2. Entity + RO（入参）+ VO（出参）
3. Mapper 接口（+ 必要的 XML，放 `resources/mapper/`）
4. Service 接口 + ServiceImpl（事务、业务逻辑）
5. Controller（参数校验 `@Valid`、返回 `ResultVO`、必要时同步 `PUBLIC_APIS`）
6. 前端：`types/` 类型 + `api/` 封装 + views 页面（菜单类模块提供 `sys_menu` 数据即可被动态路由框架自动挂载）
7. 全链路字段一致性：表字段 → Entity → DTO → 前端类型，名称/类型/非空约束完全一致
