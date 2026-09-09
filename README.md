# aiguibin-platform-arch

前后端一体的**纯净工程框架骨架**——不包含任何业务功能与认证功能，仅提供技术栈基线、分层约定与公共设施。

- 后端：Spring Boot **4.1.x** + MyBatis-Plus **3.5.x**（Boot 4 专用 starter）+ MySQL 8.4
- 前端：Vue **3.5** + Vite **8**（Rolldown）+ TypeScript **5.9**（strict）+ Element Plus **2.14**

## 技术基线

| 层 | 组件 | 约定版本 | 最低版本 |
| --- | --- | --- | --- |
| 运行时 | JDK | 21（LTS） | 17 |
| 后端框架 | Spring Boot | 4.1.x | 4.0 |
| 安全 | Spring Security + JWT（jjwt 0.13） | 随 Boot 4 | 按需回补，当前未启用 |
| ORM | MyBatis-Plus | 3.5.x（boot4-starter） | 3.5.9 |
| 数据库 | MySQL | 8.4 LTS | 8.0 |
| 接口文档 | springdoc-openapi-starter-webmvc-ui | 3.0.3（v3.x 对应 Boot 4） | 3.0 |
| 数据库迁移 | Flyway（默认关闭，接入库后启用） | 随 Boot BOM | — |
| 缓存 | Redis（Lettuce） | 7.x / 8.x | 按需回补，当前未启用 |
| 构建 | Maven Wrapper（自动下载 3.9.11） | 3.9.x | 3.9.6 |
| 运行时 | Node.js | 24 LTS（`.nvmrc` 固定） | 22.12 |
| 包管理 | pnpm（`packageManager` 字段 + corepack 固定） | ≥ 10.21 | 10.21 |
| 构建 | Vite（Rolldown 架构） | 8.x | 8.0 |
| 框架 | Vue | 3.5.x | 3.5 |
| 语言 | TypeScript（`strict: true`） | 5.9.x | 5.5 |
| UI | Element Plus | 2.14.x | 2.9 |
| 状态 | Pinia 3 + Vue Router 4.5 + Axios 1 | — | — |

## 项目结构

```
aiguibin-platform-arch/
├── .mvn/wrapper/                       # Maven Wrapper 发行版配置（必须提交，官方标准位置）
├── mvnw / mvnw.cmd                     # Maven 构建入口（官方标准：项目根目录）
├── Jenkinsfile                         # CI 流水线（Jenkins 标准：仓库根目录）
├── scripts/                            # 运维/工具脚本（业界惯例目录）
│   ├── restart.sh                      # 生产重启脚本
│   ├── start.sh                        # 本地构建并运行
│   ├── check_kill_port.sh              # 端口占用检查/清理
│   └── local_debug_restart.sh          # 本地调试重启
├── docs/                               # 项目文档（含 docs/scaffold-spec.md 脚手架规范）
├── src/main/java/com/aiguibin/platform/arch/
│   ├── SpringbootStarterApplication    # 启动类
│   ├── common/
│   │   ├── exception/                  # BusinessException + 全局异常处理
│   │   ├── result/                     # ResultVO 统一响应 + ResultCode 响应码枚举
│   │   └── web/                        # TraceIdFilter（X-Trace-Id 链路追踪 + MDC）
│   └── config/
│       └── MybatisPlusConfig           # 分页插件 + 审计字段填充
├── src/test/java/.../arch/
│   ├── ArchitectureTest                # ArchUnit 分层守护测试
│   └── SpringbootStarterApplicationTests  # 启动冒烟测试
├── src/main/resources/
│   ├── application.yml                 # 公共配置（默认 dev profile）
│   ├── application-dev.yml             # 本地数据源
│   ├── application-prod.yml            # 生产数据源（环境变量注入）
│   ├── logback-spring.xml              # 日志：文件滚动 + traceId 模式
│   ├── db/migration/                   # Flyway 迁移脚本（V1 RBAC 表结构 / V2 初始数据）
│   └── static/                         # 前端构建产物（git 忽略，pnpm build 生成）
└── src/main/webapp/                    # Vue 3 前端壳（Vite + TS + ESLint/Prettier）
    ├── .env.development / .env.production  # API 地址多环境
    ├── eslint.config.js / .prettierrc.json # 代码检查与格式化
    └── src/
        ├── api/request.ts              # axios 封装（ResultVO 解包约定）
        ├── components/MainLayout.vue   # 布局壳
        ├── router/index.ts             # 静态壳路由
        ├── views/Dashboard.vue         # 空壳首页
        └── styles/                     # 主题变量与全局样式
```

> 业务模块按 `module/<模块名>/{controller, service, mapper, entity, dto}` 功能分包新增；RBAC 十表在 `db/migration/` 预留，框架默认 Flyway 关闭、不依赖数据库即可启动。

## 快速开始

### 环境准备

- JDK 21（LTS，约定版本；最低 17，基线已按 21 编译）
- Node.js ≥ 22.12 + pnpm ≥ 10.21（`corepack enable` 即可获得项目锁定的 pnpm）
- MySQL / Redis：当前骨架未使用，业务模块按需引入

### 后端

```bash
# Windows（wrapper 已按官方标准提交于根目录，无需本机安装 Maven 3.9+）
set JAVA_HOME=D:\Java\jdk21.0.12.1
mvnw.cmd -gs "D:\Maven\settings-aiguibin.xml" -Dmaven.repo.local="E:\Repository\Local" clean package -DskipTests

# Linux / macOS
./mvnw clean package -DskipTests

java -jar target/aiguibin-platform-arch.jar
```

### 前端

```bash
cd src/main/webapp
pnpm install
pnpm dev        # 开发模式 http://localhost:3000，/api 代理到 8080
pnpm build      # 类型检查 + 构建，产物输出到 ../resources/static
```

### 一体化打包（可选）

```bash
./mvnw clean package -DskipTests -Pfrontend   # 自动执行 pnpm install + build
```

访问：`http://localhost:8080`（空壳首页）。

## 业务模块接入约定

- 后端：按 `module/<模块名>/{controller, service, mapper, entity, dto}` 功能分包新增；公共设施复用 `common/`（ResultVO/ResultCode、BusinessException、全局异常、TraceIdFilter）；接口文档用 springdoc 注解（`@Tag`/`@Operation`），访问 `/swagger-ui/index.html`
- 需要认证时按基线回补 Spring Security + JWT：无状态过滤器链、免认证接口集中声明、密钥走环境变量（详见 `.trae/rules/project_rules.md`）
- 数据库结构变更一律新增 Flyway 脚本 `db/migration/V<N>__描述.sql`，禁止修改历史脚本
- 前端：`types/` 类型 + `api/` 封装 + `views/` 页面 + 静态路由（或恢复菜单驱动动态路由）
- 每个模块的完整交付清单见 `.trae/rules/project_rules.md`；提交前执行 `./mvnw spotless:apply` 与 `pnpm lint`

## 生产部署注意

- `src/main/resources/static/` 为构建产物，已被 git 忽略；部署镜像需先执行前端构建或使用 `-Pfrontend`
- 数据源等配置通过 `application.yml` 或环境变量覆盖
