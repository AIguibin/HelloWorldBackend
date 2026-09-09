# Java 工程脚手架规范

> 用途：创建新 Java 项目时，将本文档作为提示词/规范交给开发者或 AI，按此规则搭建工程。
> 来源：从 aiguibin-platform-arch 纯净骨架（已验证可构建、可启动、测试通过）提炼。
> 基线日期：2026-09。所有版本均为当前验证可用的稳定版本。

---

## 1. 技术基线（约定版本 / 最低版本）

| 层 | 组件 | 约定版本 | 最低版本 |
| --- | --- | --- | --- |
| 运行时 | JDK | 21（LTS） | 17 |
| 后端框架 | Spring Boot | 4.1.x | 4.0 |
| ORM | MyBatis-Plus | 3.5.16（`mybatis-plus-spring-boot4-starter`） | 3.5.9 |
| 分页解析 | mybatis-plus-jsqlparser | 3.5.16（与 starter 同版本） | 3.5.9 |
| 接口文档 | springdoc-openapi-starter-webmvc-ui | 3.0.3（v3.x 对应 Boot 4） | 3.0 |
| 数据库迁移 | Flyway（flyway-core + flyway-mysql，版本随 Boot BOM） | Boot 管理版 | — |
| 数据库 | MySQL | 8.4 LTS | 8.0 |
| 驱动 | mysql-connector-j | 9.x（随 Boot BOM 管理） | 8.x |
| 构建 | Maven Wrapper | 3.9.11 | 3.9.6 |
| 代码格式 | Spotless（palantirJavaFormat，Maven 插件） | 2.44.0 | — |
| 测试 | spring-boot-starter-test + ArchUnit | 1.4.x | 1.4 |
| 运行时 | Node.js | 24 LTS（`.nvmrc` 固定） | 22.12 |
| 包管理 | pnpm | 11.x（`packageManager` 字段固定） | 10.21 |
| 前端构建 | Vite | 8.x（Rolldown 架构） | 8.0 |
| 前端框架 | Vue | 3.5.x | 3.5 |
| 语言 | TypeScript（`strict: true`） | 5.9.x | 5.5 |
| UI | Element Plus | 2.14.x | 2.9 |
| 代码检查 | ESLint 10（flat config）+ Prettier 3 | — | — |
| 状态/路由/请求 | Pinia 3 + Vue Router 4.5 + Axios 1 | — | — |
| 安全（按需回补） | Spring Security 7.x + JWT（jjwt 0.13） | 随 Boot 4 | 可选 |
| 缓存（按需回补） | Redis + Lettuce | 7.x / 8.x | 可选 |

**禁止**：Spring Boot 2.7/3.5（EOL）、springfox、javax.* 显式依赖（Boot 4 用 jakarta.*）、本机全局 Maven。

---

## 2. 根目录结构（什么文件放什么目录）

```
<项目名>/
├── .mvn/wrapper/
│   ├── maven-wrapper.jar            # Maven Wrapper 运行器（必须提交）
│   └── maven-wrapper.properties     # distributionUrl 指向 3.9.11（必须提交）
├── mvnw                             # Linux/macOS 构建入口（Maven 官方标准：项目根目录）
├── mvnw.cmd                         # Windows 构建入口
├── Jenkinsfile                      # CI 流水线（Jenkins 标准：仓库根目录）
├── scripts/                         # 运维/工具脚本（业界惯例目录）
│   ├── restart.sh                   # 生产重启（kill 旧进程 + nohup 启动）
│   ├── start.sh                     # 本地构建并运行
│   ├── check_kill_port.sh           # 端口占用检查/清理
│   └── local_debug_restart.sh       # 本地调试重启
├── docs/                            # 项目文档（本规范即放这里）
├── .trae/rules/project_rules.md     # AI 开发规范（可选，团队约定）
├── .editorconfig                    # 编辑器格式统一（缩进/字符集/换行）
├── .gitignore                       # 忽略 target/ node_modules/ static/ logs/ .idea 等
├── pom.xml                          # Maven：parent=spring-boot-starter-parent 4.1.1
└── README.md                        # 含技术基线表 + 快速开始
```

规则：
- `mvnw`、`mvnw.cmd`、`.mvn/wrapper/` 按 Maven 官方标准放**项目根目录**（Spring Initializr 与官方文档一致），保证 IDE 与各类 CI 工具默认识别；运维/工具脚本统一放 `scripts/`。
- 生成方式：项目根目录执行 `mvn org.apache.maven.plugins:maven-wrapper-plugin:3.2.0:wrapper -Dmaven=3.9.11`，生成的 `mvnw*` 与 `.mvn/` 即在根目录。

---

## 3. 后端结构与文件清单

包根：`src/main/java/com/<域名>/<应用名>/`（下例以 `com.aiguibin.platform.arch` 为例，新项目替换为实际 groupId/artifactId）。

**框架层（固定不变）**：

```
src/main/java/com/aiguibin/platform/arch/
├── SpringbootStarterApplication.java     # 启动类：@SpringBootApplication + @ConfigurationPropertiesScan
├── common/                               # 框架公共层（禁止反向依赖业务层，由 ArchUnit 测试守护）
│   ├── result/
│   │   ├── ResultCode.java               # 响应码枚举：SUCCESS(200)/BAD_REQUEST(400)/UNAUTHORIZED(401)/
│   │   │                                 #   FORBIDDEN(403)/NOT_FOUND(404)/METHOD_NOT_ALLOWED(405)/ERROR(500)
│   │   └── ResultVO.java                 # 统一响应：code/message/data/timestamp/traceId；内含 PageData<T>；
│   │                                     #   方法命名防二义性：success(data) 只设数据、successMsg(msg,data) 设提示；
│   │                                     #   traceId 构造时自动从 MDC 取值
│   ├── exception/
│   │   ├── BusinessException.java        # 业务异常：code(默认400) + message
│   │   └── GlobalExceptionHandler.java   # @RestControllerAdvice，覆盖：业务异常、body 校验(MethodArgumentNotValid)、
│   │                                     #   参数约束(ConstraintViolation)、缺参、类型不匹配、报文不可读、
│   │                                     #   唯一约束、404、405、未捕获异常
│   └── web/
│       └── TraceIdFilter.java            # OncePerRequestFilter：生成/透传 X-Trace-Id 并写 MDC（最高优先级）
└── config/
    └── MybatisPlusConfig.java            # MybatisPlusInterceptor(分页) + MetaObjectHandler(审计字段填充)
```

**业务模块（按功能分包，不按技术层分包）**——每个业务模块一个子包，内聚自己的五层：

```
└── module/
    └── <模块名>/                          # 如 module/order/
        ├── controller/
        │   └── <模块>Controller.java     # @RestController + @RequestMapping("/api/v1/<模块>")；只做参数
        │                                 #   校验与编排，禁止直调 mapper（ArchUnit 守护）
        ├── service/
        │   ├── <模块>Service.java        # 接口；模块间调用只允许走对方 service 接口
        │   └── impl/<模块>ServiceImpl.java
        ├── mapper/
        │   └── <实体>Mapper.java         # extends BaseMapper<实体> + @Mapper
        ├── entity/
        │   └── <表名转驼峰>.java         # @TableName/@TableField/@TableId/@TableLogic
        └── dto/
            ├── <动作>RO.java             # 入参，jakarta.validation 注解校验
            └── <结果>VO.java             # 出参，禁止把 Entity 直接返回给前端
```

**依赖注入**：一律构造器注入（`private final X x;` + 构造函数），禁止 `@Resource`/`@Autowired` 字段注入。

**测试（src/test/java，与主包对应）**：

```
src/test/java/com/aiguibin/platform/arch/
├── ArchitectureTest.java                 # ArchUnit：controller 不直调 mapper、common 不依赖业务层
│                                         #   （allowEmptyShould，业务模块出现后自动生效）
└── SpringbootStarterApplicationTests.java # contextLoads 启动冒烟（不依赖数据库即可通过）
```

业务模块测试约定：单元测试随模块放同包；需要 MySQL/Redis 的集成测试用 Testcontainers 拉起容器，禁止依赖本机服务。

**认证（按需回补）**：需要登录时新增以下文件，并在 pom 回补 `spring-boot-starter-security`、`jjwt-api/impl/jackson`、`spring-boot-starter-data-redis`：

```
├── security/
│   ├── JwtProperties.java               # record + @ConfigurationProperties("arch.jwt")，密钥≥32字节校验
│   ├── JwtTokenProvider.java            # 签发/解析（jjwt，HS256），令牌分 access/refresh 两类
│   ├── JwtAuthenticationFilter.java     # extends OncePerRequestFilter，校验通过写入 SecurityContext
│   └── LoginUser.java                   # record(userNum, userName, permissions)
├── config/
│   ├── SecurityConfig.java              # SecurityFilterChain：无状态、CSRF 关闭、免认证路径集中声明
│   └── SecurityBeansConfig.java         # PasswordEncoder + UserDetailsService（独立成类避免循环依赖）
```

认证规则：令牌走 `Authorization: Bearer` 头（禁用 CSRF token 机制）；Redis jti 白名单支持登出吊销与刷新轮换；密钥生产环境用环境变量（如 `ARCH_JWT_SECRET`）；启用后 swagger 端点需纳入访问控制。

---

## 4. 后端资源文件（src/main/resources）

```
src/main/resources/
├── application.yml                       # 公共配置：server.port、spring.profiles.active: dev、
│                                         #   jackson、mybatis-plus(逻辑删除/驼峰)、logging.level、
│                                         #   spring.flyway.enabled: false（骨架无库可启动；接入库后置 true）
├── application-dev.yml                   # 本地数据源（localhost + HikariCP 参数）
├── application-prod.yml                  # 生产数据源：${DB_URL}/${DB_USERNAME}/${DB_PASSWORD} 环境变量占位，
│                                         #   flyway.enabled: true；禁止提交真实凭据
├── logback-spring.xml                    # 控制台 + 文件滚动（logs/arch.log，按天+100MB 切割，保留30天），
│                                         #   日志模式含 %X{traceId}
├── db/migration/                         # Flyway 迁移脚本（启动时按版本号执行）：
│   ├── V1__platform_rbac_schema.sql      #   表结构（示例：RBAC 十表）
│   └── V2__platform_rbac_data.sql        #   初始数据；业务增量一律新增 V<N>__描述.sql，禁止改历史脚本
└── static/                               # 前端构建产物（git 忽略，由 pnpm build 生成）
```

---

## 5. 前端结构与文件清单（src/main/webapp）

```
src/main/webapp/
├── package.json        # type=module；packageManager: pnpm@11.22.0；engines: node>=22.12.0；
│                       #   scripts: dev/build(vue-tsc --noEmit && vite build)/type-check/preview/
│                       #   lint(eslint .)/lint:fix/format(prettier)
├── .nvmrc              # 内容：24
├── .npmrc              # 内容：registry=https://registry.npmmirror.com（按网络环境调整）
├── .env.development    # VITE_API_BASE_URL=/api
├── .env.production     # VITE_API_BASE_URL=/api（分离部署改为完整地址）
├── .prettierrc.json    # semi/singleQuote/printWidth 120/trailingComma all
├── .prettierignore     # node_modules、../resources/static、pnpm-lock.yaml
├── eslint.config.js    # ESLint 10 flat config：eslint-plugin-vue flat/recommended + vue-ts recommended，
│                       #   关闭 multi-word-component-names
├── pnpm-lock.yaml      # pnpm install 生成，必须提交
├── index.html          # <div id="app"> + <script type="module" src="/src/main.ts">
├── vite.config.ts      # @vitejs/plugin-vue；别名 @→src；server.port 3000 + /api 代理到 8080；
│                       #   build.outDir = ../resources/static（fileURLToPath 写法）+ emptyOutDir: true
├── tsconfig.json       # strict: true、target ES2022、moduleResolution bundler、verbatimModuleSyntax、
│                       #   noUnusedLocals/Parameters、paths @/*、types: [vite/client, node]
└── src/
    ├── main.ts                 # 顺序：全局注册 @element-plus/icons-vue → app.use(createPinia())
    │                           #   → app.use(router) → app.use(ElementPlus, { locale: zhCn })
    ├── App.vue                 # 仅 <router-view />
    ├── env.d.ts                # vite/client 引用 + ImportMetaEnv 类型 + *.vue 模块声明
    ├── api/
    │   ├── request.ts          # axios 实例(baseURL 取 VITE_API_BASE_URL)；
    │   │                       #   响应拦截器统一解包 ResultVO（code===200 返回 data）；
    │   │                       #   认证接入点：请求拦截器加 Authorization 头（注释示例已内置）
    │   └── index.ts            # 业务 API 函数（每个函数返回对应 VO 类型）
    ├── types/
    │   └── index.ts            # 与后端 VO 对齐的 TS 接口定义
    ├── router/
    │   └── index.ts            # createWebHashHistory；MainLayout 壳 + 子路由；catch-all 重定向
    ├── stores/                 # Pinia store（actions 中修改 state），随业务新增
    ├── utils/                  # 工具函数（组合式函数/纯函数，禁止 mixins）
    ├── components/             # 可复用组件（<script setup lang="ts">）
    ├── views/                  # 页面级组件（路由挂载目标）
    └── styles/
        ├── variables.css       # 主题 CSS 变量（品牌色/中性色/布局尺寸 + --el-color-primary 对齐）
        └── global.css          # reset + 布局通用样式
```

---

## 6. 命名规范

| 对象 | 规范 | 示例 |
| --- | --- | --- |
| Java 类 | UpperCamelCase | `UserController` |
| 方法/变量 | lowerCamelCase | `getUserById()` |
| 数据库字段/表 | snake_case | `user_name` |
| 前端文件 | lowerCamelCase | `userList.vue` |
| 常量 | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| 入参类 | `XxxRO` | `LoginRO` |
| 出参类 | `XxxVO` | `LoginVO` |
| 响应码 | ResultCode 枚举，禁止裸数字 | `ResultVO.error(ResultCode.NOT_FOUND)` |
| API 路径 | `/api/v1/<模块>/<动作>` RESTful | `POST /api/v1/orders` |
| 迁移脚本 | `V<N>__<描述>.sql` | `V3__add_order_index.sql` |

**建表规范**：所有表必含 `uuid varchar(32)`、`id bigint AUTO_INCREMENT`、`created_by`、`created_time`、`updated_by`、`updated_time`、`is_deleted tinyint`（逻辑删除，由 MetaObjectHandler 自动填充审计字段）；SQL 禁止拼接，必须 `#{}` 参数化。

---

## 7. pom.xml 关键内容（骨架）

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.1.1</version>
</parent>
<properties>
    <java.version>21</java.version>              <!-- 最低 17 -->
    <mybatis-plus.version>3.5.16</mybatis-plus.version>
    <springdoc.version>3.0.3</springdoc.version>
    <archunit.version>1.4.1</archunit.version>
</properties>
<dependencies>
    spring-boot-starter-webmvc                   <!-- Boot 4 不叫 starter-web -->
    spring-boot-starter-validation
    spring-boot-jackson                          <!-- Boot 4 web starter 不自带 Jackson，包名 tools.jackson -->
    spring-boot-starter-actuator
    org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3
    org.flywaydb:flyway-core + flyway-mysql      <!-- 版本随 Boot BOM -->
    com.baomidou:mybatis-plus-spring-boot4-starter:${mybatis-plus.version}
    com.baomidou:mybatis-plus-jsqlparser:${mybatis-plus.version}
    com.mysql:mysql-connector-j (runtime，版本随 BOM)
    org.projectlombok:lombok (optional)
    spring-boot-starter-test (test)
    com.tngtech.archunit:archunit-junit5:1.4.1 (test)
</dependencies>
<build><plugins>
    spring-boot-maven-plugin（exclude lombok）
    com.diffplug.spotless:spotless-maven-plugin:2.44.0（palantirJavaFormat + removeUnusedImports + importOrder，
        不绑定生命周期：./mvnw spotless:apply 手动格式化、spotless:check 供 CI 校验）
</plugins></build>
```

**Boot 4 已知坑（务必遵守）**：
1. `spring-boot-starter-web` 已废弃 → 用 `spring-boot-starter-webmvc`；
2. web starter 不含 Jackson → 显式加 `spring-boot-jackson`，且 Jackson 3 包名是 `tools.jackson.databind`；
3. `spring-boot-starter-aop` 已移除（需要切面直接依赖 aspectjweaver）；
4. MyBatis-Plus 必须 `mybatis-plus-spring-boot4-starter` 且 ≥3.5.15（低版本启动报 Invalid value type），jsqlparser 单独引入；
5. 本机 Maven < 3.6.3 解析不了 Boot 4 的 pom → 一律用项目根目录的 `./mvnw`；
6. Windows 下重新打包前先停掉运行中的 java 进程，否则 jar 被锁定会产出缺资源的坏包。

---

## 8. 构建与验证命令

```bash
# 后端（JAVA_HOME 指向 JDK 21；镜像参数按本机环境替换，建议走 MAVEN_ARGS 环境变量注入）
set JAVA_HOME=D:\Java\jdk21.0.12.1
mvnw.cmd clean package                                # 含单元测试 + ArchUnit 架构测试
mvnw.cmd spotless:apply                               # 代码格式化（提交前执行）
mvnw.cmd spotless:check                               # 格式校验（CI 用）

# 前端
cd src/main/webapp
pnpm install
pnpm lint                    # ESLint 检查
pnpm build                   # vue-tsc 严格类型检查 + 构建，产物输出 ../resources/static

# 一体化打包（CI 用）
./mvnw clean package -Pfrontend                  # 自动 pnpm install + build
```

**交付前验证五件事**：后端 `BUILD SUCCESS`（含测试）；`java -jar target/*.jar` 能启动且 `/swagger-ui/index.html`、`/actuator/health` 可访问、响应头含 `X-Trace-Id`；前端 `pnpm lint` 与 `pnpm build` 双绿；`spotless:check` 通过。

---

## 9. 新项目搭建执行清单（可直接作为提示词）

1. 建根目录，按第 2 节创建 `scripts/`、`db/migration/`（resources 下）、`docs/`、`.editorconfig`、`.gitignore`、`README.md`
2. 写 `pom.xml`（第 7 节骨架，替换 groupId/artifactId/项目名）
3. 生成并提交 Maven Wrapper（官方标准位置）：根目录 `mvnw`、`mvnw.cmd` + `.mvn/wrapper/`
4. 写根目录 `Jenkinsfile` 与 `scripts/restart.sh`、`scripts/start.sh`
5. 按第 3 节创建后端框架文件：`SpringbootStarterApplication`、`common/result/ResultCode`、`common/result/ResultVO`、`common/web/TraceIdFilter`、`common/exception/BusinessException`、`common/exception/GlobalExceptionHandler`、`config/MybatisPlusConfig`
6. 按第 4 节写 `application.yml` + `application-dev.yml` + `application-prod.yml` + `logback-spring.xml`，初始表结构放 `db/migration/V1__xxx.sql`
7. 按第 3 节写测试：`ArchitectureTest`（分层守护）+ `SpringbootStarterApplicationTests`（启动冒烟）
8. 按第 5 节搭前端：先写根部配置文件（package.json/.nvmrc/.npmrc/.env.*/tsconfig/vite.config/eslint.config/.prettierrc/index.html），`pnpm add` 安装依赖，再写 `src/` 下文件
9. 执行第 8 节验证命令，五项全绿后基线搭建完成
10. 业务模块按第 3 节 `module/<模块名>/` 分包逐个交付，每模块含：Flyway 迁移脚本 + Entity + RO/VO + Mapper + Service + Controller + 前端类型/API/页面 + 单元测试

---

## 10. 设计取舍说明（有意为之，勿"修复"）

1. **前端产物打进 jar（src/main/webapp → resources/static）**：单 jar 一体化部署，前端发版=后端发版；需要独立发版/CDN 的项目应把 webapp 拆独立仓库。
2. **镜像/路径不进代码**：`-gs`、本地仓库路径等机器相关参数通过 `MAVEN_ARGS` 环境变量或个人 settings 注入，文档示例仅为本机参考。
3. **API 版本化**：新项目建议业务路径带版本（`/api/v1/...`），破坏性变更升 v2，避免路径无版本导致的兼容困境。
4. **springdoc 默认开放**：骨架无认证，Swagger UI 公开仅限开发；接入认证后必须把 `/swagger-ui/**`、`/v3/api-docs/**` 纳入访问控制。
