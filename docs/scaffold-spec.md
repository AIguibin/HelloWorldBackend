# Java 工程脚手架规范（新项目搭建提示词）

> 用途：创建新 Java 项目时，将本文档作为提示词/规范交给开发者或 AI，按此规则搭建工程。
> 来源：从 aiguibin-platform-arch 纯净骨架（已验证可构建、可启动）提炼。
> 基线日期：2026-09。所有版本均为当前验证可用的稳定版本。

---

## 1. 技术基线（约定版本 / 最低版本）

| 层 | 组件 | 约定版本 | 最低版本 |
| --- | --- | --- | --- |
| 运行时 | JDK | 21（LTS） | 17 |
| 后端框架 | Spring Boot | 4.1.x | 4.0 |
| ORM | MyBatis-Plus | 3.5.16（`mybatis-plus-spring-boot4-starter`） | 3.5.9 |
| 分页解析 | mybatis-plus-jsqlparser | 3.5.16（与 starter 同版本） | 3.5.9 |
| 数据库 | MySQL | 8.4 LTS | 8.0 |
| 驱动 | mysql-connector-j | 9.x（随 Boot BOM 管理） | 8.x |
| 构建 | Maven Wrapper | 3.9.11 | 3.9.6 |
| 运行时 | Node.js | 24 LTS（`.nvmrc` 固定） | 22.12 |
| 包管理 | pnpm | 11.x（`packageManager` 字段固定） | 10.21 |
| 前端构建 | Vite | 8.x（Rolldown 架构） | 8.0 |
| 前端框架 | Vue | 3.5.x | 3.5 |
| 语言 | TypeScript（`strict: true`） | 5.9.x | 5.5 |
| UI | Element Plus | 2.14.x | 2.9 |
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
├── deploy/                          # 全部构建部署脚本集中于此
│   ├── Jenkinsfile                  # CI：pnpm build → deploy/mvnw package → 归档 jar
│   ├── mvnw                         # Linux/macOS 构建入口（bash）
│   ├── mvnw.cmd                     # Windows 构建入口
│   ├── restart.sh                   # 生产重启（kill 旧进程 + nohup 启动）
│   └── start.sh                     # 本地构建并运行
├── checks/                          # 检查/校验类脚本（预留目录，放 .gitkeep）
├── sql/                             # 数据库脚本：*_init.sql(表结构) + *_data.sql(初始数据)
├── docs/                            # 项目文档（本规范即放这里）
├── .trae/rules/project_rules.md     # AI 开发规范（可选，团队约定）
├── .gitignore                       # 忽略 target/ node_modules/ static/ logs/ .idea 等
├── pom.xml                          # Maven：parent=spring-boot-starter-parent 4.1.1
└── README.md                        # 含技术基线表 + 快速开始
```

规则：
- Maven Wrapper 放在 `deploy/`，但 `.mvn/` 必须在根目录——wrapper 从脚本所在目录向上查找 `.mvn/wrapper`，因此任意位置执行 `deploy/mvnw` 都有效。
- 生成方式：空目录放最小 pom 后执行 `mvn org.apache.maven.plugins:maven-wrapper-plugin:3.2.0:wrapper -Dmaven=3.9.11`，把生成的 `.mvn/`、`mvnw*` 拷入项目对应位置。

---

## 3. 后端结构与文件清单

包根：`src/main/java/com/<域名>/<应用名>/`（下例以 `com.aiguibin.platform.arch` 为例，新项目替换为实际 groupId/artifactId）。

```
src/main/java/com/aiguibin/platform/arch/
├── SpringbootStarterApplication.java     # 启动类：@SpringBootApplication + @ConfigurationPropertiesScan
├── common/                               # ★ 框架公共层（固定不变）
│   ├── result/
│   │   └── ResultVO.java                 # 统一响应：code(Integer,200=成功)/message/data/timestamp/traceId，
│   │                                     #   内含 PageData<T> 分页包装；提供 success()/error()/page() 静态工厂
│   └── exception/
│       ├── BusinessException.java        # 业务异常：code(默认400) + message
│       └── GlobalExceptionHandler.java   # @RestControllerAdvice：Business→ResultVO.error(code,msg)、
│                                         #   MethodArgumentNotValid→400首条字段错误、DuplicateKey→400、
│                                         #   Exception→500"系统异常"并打日志
└── config/                               # ★ 框架配置层（固定不变）
    └── MybatisPlusConfig.java            # 两个 Bean：
                                          #   MybatisPlusInterceptor + PaginationInnerInterceptor（分页）
                                          #   MetaObjectHandler（审计填充：createdTime/updatedTime/createdBy/updatedBy，
                                          #   默认操作人 system，接入认证后改为取当前用户）
```

**业务模块**（每个模块按以下分层新增，禁止越层依赖）：

```
├── controller/
│   └── <模块>Controller.java             # @RestController + @RequestMapping("/api/<模块>")
│                                         #   参数 @Valid 校验，返回 ResultVO<T>，禁止写业务逻辑
├── service/
│   ├── <模块>Service.java                # 接口
│   └── impl/<模块>ServiceImpl.java       # 实现：事务、业务逻辑；构造器注入 Mapper
├── mapper/
│   └── <实体>Mapper.java                 # extends BaseMapper<实体>，@Mapper 注解
├── entity/
│   └── <表名转驼峰>.java                 # @TableName；字段 @TableField；主键 @TableId(type=AUTO)；
│                                         #   逻辑删除 @TableLogic @TableField("is_deleted")
└── dto/
    ├── <动作>RO.java                     # 入参（Request Object），jakarta.validation 注解校验
    └── <结果>VO.java                      # 出参（View Object），禁止把 Entity 直接返回给前端
```

**依赖注入**：一律构造器注入（`private final X x;` + 构造函数），禁止 `@Resource`/`@Autowired` 字段注入。

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

认证规则：令牌走 `Authorization: Bearer` 头（禁用 CSRF token 机制）；Redis jti 白名单支持登出吊销与刷新轮换；密钥生产环境用环境变量（如 `ARCH_JWT_SECRET`）。

---

## 4. 后端资源文件（src/main/resources）

```
src/main/resources/
├── application.yml                       # server.port(8080)；spring.datasource(MySQL+HikariCP 连接池参数)；
│                                         #   spring.jackson(date-format/time-zone Asia/Shanghai)；
│                                         #   mybatis-plus(mapper-locations=classpath*:mapper/*.xml、
│                                         #   map-underscore-to-camel-case、逻辑删除 isDeleted 1/0、banner off)；
│                                         #   logging(mapper 包 DEBUG)
├── mapper/                               # 复杂 SQL 的 XML（与 Mapper 接口同名），简单 CRUD 不需要
└── static/                               # 前端构建产物（git 忽略，由 pnpm build 生成，不手工放文件）
```

---

## 5. 前端结构与文件清单（src/main/webapp）

```
src/main/webapp/
├── package.json        # type=module；packageManager: pnpm@11.22.0；engines: node>=22.12.0；
│                       #   scripts: dev/build(vue-tsc --noEmit && vite build)/type-check/preview
├── .nvmrc              # 内容：24
├── .npmrc              # 内容：registry=https://registry.npmmirror.com（按网络环境调整）
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
    ├── env.d.ts                # vite/client 引用 + *.vue 模块声明
    ├── api/
    │   ├── request.ts          # axios 实例(baseURL=/api, timeout 10s)；响应拦截器统一解包 ResultVO
    │   │                       #   （code===200 返回 data，否则 ElMessage.error+reject）；
    │   │                       #   导出泛型包装 get<T>/post<T>/put<T>/del<T>
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
| API 路径 | `/api/<模块>/<动作>` RESTful | `POST /api/auth/login` |

**建表规范**：所有表必含 `uuid varchar(32)`、`id bigint AUTO_INCREMENT`、`created_by`、`created_time`、`updated_by`、`updated_time`、`is_deleted tinyint`（逻辑删除）；SQL 禁止拼接，必须 `#{}` 参数化。

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
</properties>
<dependencies>
    spring-boot-starter-webmvc                   <!-- Boot 4 不叫 starter-web -->
    spring-boot-starter-validation
    spring-boot-jackson                          <!-- Boot 4 web starter 不自带 Jackson，包名 tools.jackson -->
    spring-boot-starter-actuator
    com.baomidou:mybatis-plus-spring-boot4-starter:${mybatis-plus.version}
    com.baomidou:mybatis-plus-jsqlparser:${mybatis-plus.version}
    com.mysql:mysql-connector-j (runtime，版本随 BOM)
    org.projectlombok:lombok (optional)
    spring-boot-starter-test + spring-security-test (test，按需)
</dependencies>
```

**Boot 4 已知坑（务必遵守）**：
1. `spring-boot-starter-web` 已废弃 → 用 `spring-boot-starter-webmvc`；
2. web starter 不含 Jackson → 显式加 `spring-boot-jackson`，且 Jackson 3 包名是 `tools.jackson.databind`；
3. `spring-boot-starter-aop` 已移除（需要切面直接依赖 aspectjweaver）；
4. MyBatis-Plus 必须 `mybatis-plus-spring-boot4-starter` 且 ≥3.5.15（低版本启动报 Invalid value type），jsqlparser 单独引入；
5. 本机 Maven < 3.6.3 解析不了 Boot 4 的 pom → 一律用 `deploy/mvnw`；
6. Windows 下重新打包前先停掉运行中的 java 进程，否则 jar 被锁定会产出缺资源的坏包。

---

## 8. 构建与验证命令

```bash
# 后端（JAVA_HOME 指向 JDK 21；-gs/-Dmaven.repo.local 按本机镜像环境替换）
set JAVA_HOME=D:\Java\jdk21.0.12.1
deploy\mvnw.cmd -gs "D:\Maven\settings-aiguibin.xml" -Dmaven.repo.local="E:\Repository\Local" clean package -DskipTests

# 前端
cd src/main/webapp && pnpm install && pnpm build     # 产物输出 ../resources/static

# 一体化打包（CI 用）
deploy/mvnw clean package -DskipTests -Pfrontend      # 自动 pnpm install + build
```

**交付前验证三件事**：后端 `BUILD SUCCESS`；`java -jar target/*.jar` 能启动且首页/actuator 可访问；前端 `pnpm build` 通过 vue-tsc 严格检查。

---

## 9. 新项目搭建执行清单（可直接作为提示词）

1. 建根目录，按第 2 节创建 `deploy/`、`checks/`、`sql/`、`docs/`、`.gitignore`、`README.md`
2. 写 `pom.xml`（第 7 节骨架，替换 groupId/artifactId/项目名）
3. 生成并提交 Maven Wrapper：`.mvn/wrapper/` + `deploy/mvnw`、`deploy/mvnw.cmd`
4. 写 `deploy/Jenkinsfile`、`deploy/restart.sh`、`deploy/start.sh`
5. 按 第 3 节 创建后端 5 个框架文件：`SpringbootStarterApplication`、`common/result/ResultVO`、`common/exception/BusinessException`、`common/exception/GlobalExceptionHandler`、`config/MybatisPlusConfig`
6. 按 第 4 节 写 `application.yml`（数据源按项目实际修改）
7. 按 第 5 节 搭前端：先写 `package.json`/`.nvmrc`/`.npmrc`/`tsconfig.json`/`vite.config.ts`/`index.html`，`pnpm add` 安装依赖（让注册表解析真实版本），再写 `src/` 下 9 个文件
8. 执行第 8 节验证命令，三项全绿后基线搭建完成
9. 业务模块按第 3 节分层清单逐个交付，每模块含：SQL + Entity + RO/VO + Mapper + Service + Controller + 前端类型/API/页面
```
