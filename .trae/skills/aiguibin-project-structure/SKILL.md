---
name: "aiguibin-project-structure"
description: "提供项目结构文档和开发规范，包括代码结构、命名约定、数据库规范、前后端交互等，指导开发团队遵循统一规范进行开发。"
---

# 项目结构与开发规范技能

## 功能介绍

该技能提供完整的项目结构文档和开发规范，指导开发团队按照统一标准进行项目开发，确保代码质量、可维护性和可扩展性。适用于前后端开发、数据库设计、代码审查等场景。



## 项目规范

### 1. 代码结构与命名规范

- **实体类(Entity)**：每个数据库表对应一个`UpperCamelCase`命名的Entity类
- **Mapper接口**：每个Entity对应一个Mapper接口，按`UpperCamelCase`命名
- **全局异常处理**：统一处理空值、验证异常、业务异常等
- **全局异常捕获**：自定义业务异常 BusinessException
- **详细日志**：关键业务节点添加`INFO/DEBUG`级别日志，便于监控排查
- **统一返回格式**：ResultVO<T> 类（包含 code:Integer、message:String、data:T、timestamp、traceId）

### 2. 开发规则

#### 2.1 数据获取规则
- **禁止硬编码数据**：相关数据必须从API动态获取，避免使用模拟数据
- **API调用规范**：所有API调用必须添加完善的错误处理机制
- **数据实时性**：数据必须反映最新的状态，确保数据一致性

#### 2.2 状态管理规则
- **状态转换逻辑**：状态转换必须遵循业务规则，确保状态转换的原子性
- **状态转换日志**：状态转换过程必须添加详细的日志记录
- **状态一致性**：确保前端状态与后端数据保持同步

#### 2.3 API设计规则
- **参数一致性**：前后端API参数必须保持一致，避免因参数不匹配导致的错误
- **接口完整性**：所有API接口必须有完整的实现，避免接口缺失
- **接口命名规范**：API路径必须清晰反映业务功能，使用RESTful风格

#### 2.4 控制器设计规则
- **职责明确**：控制器职责必须明确，避免接口路径冲突
- **参数验证**：所有API参数必须进行验证，确保数据合法性
- **错误处理**：所有API必须有完善的错误处理机制

### 3. 数据库与实体开发规则

#### 3.1 实体类规则
- **字段一致性**：实体类字段必须与数据库表字段保持一致
- **命名规范**：数据库字段使用下划线命名，实体类字段使用驼峰命名
- **注解规范**：实体类必须使用正确的MyBatis-Plus注解

#### 3.2 数据库操作规则
- **事务管理**：数据库操作必须考虑事务和原子性
- **SQL规范**：SQL语句必须清晰，避免复杂的嵌套查询
- **性能优化**：数据库操作必须考虑性能，添加适当的索引

#### 3.3 状态值规范
- **前后端一致**：状态枚举定义前后端必须使用一致的取值（后端枚举类、前端联合类型）
- **集中管理**：状态值用枚举类（后端 enums/ 包）或常量集中管理，禁止魔法值散落
- **字典扩展**：业务模块需要动态字典时，自行建字典表并提供维护接口，框架不预置

#### 3.4 数据初始化规则
- **初始化脚本**：数据库初始化脚本必须包含完整的初始数据
- **数据完整性**：初始化数据必须保证完整性和一致性
- **脚本版本控制**：初始化脚本必须进行版本控制，便于追溯

### 4. 前端开发规则

#### 4.1 组件设计规则
- **组件复用**：组件必须设计为可复用的，避免重复代码
- **组件职责**：组件职责必须明确，避免组件功能过于复杂
- **组件通信**：组件间通信必须使用Pinia或props/emits机制

#### 4.2 表单开发规则
- **表单验证**：所有表单必须添加验证规则，确保数据合法性
- **空值处理**：表单必须处理空值情况，避免出现空指针错误
- **动态表单**：支持根据业务类型动态生成表单字段

#### 4.3 状态管理规则
- **Pinia使用**：复杂状态必须使用Pinia管理（src/stores/），避免状态混乱
- **状态更新**：在 actions 中修改 state（Pinia 无 mutations），认证类状态统一走 stores/auth.ts
- **异步操作**：异步逻辑必须放在 actions 或组合式函数中，禁止在组件内散落请求

#### 4.4 API调用规则
- **API封装**：API调用必须进行封装，避免重复代码
- **错误处理**：所有API调用必须添加错误处理，确保系统稳定性
- **加载状态**：API调用过程中必须显示加载状态，提升用户体验

### 5. 代码结构规则

#### 5.1 目录结构规则
- **模块化设计**：代码必须按照功能模块进行组织
- **分层架构**：遵循控制器层→服务层→数据访问层的分层架构
- **资源分离**：静态资源必须与代码分离，便于管理

#### 5.2 命名规范
| 组件类型 | 命名规范 | 示例 |
|---------|---------|------|
| Java类 | UpperCamelCase | `UserController` |
| 方法/参数 | lowerCamelCase | `getUserById()` |
| 前端请求/响应参数 | lowerCamelCase | `{ "userName": "张三" }` |
| 数据库字段 | snake_case | `user_name` |
| 前端变量/属性 | lowerCamelCase | `userName` |
| 前端文件/组件 | lowerCamelCase | `userList.vue` |
| 常量 | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |

#### 5.3 注释规范
- **类注释**：所有类必须添加Javadoc注释，说明类的功能和用途
- **方法注释**：所有方法必须添加Javadoc注释，说明方法的功能、参数和返回值
- **关键代码注释**：关键业务逻辑必须添加注释，便于理解和维护

#### 5.4 日志规范
- **日志级别**：根据业务重要性选择适当的日志级别
- **日志内容**：日志内容必须清晰反映业务操作，包含关键参数
- **异常日志**：异常日志必须包含完整的堆栈信息，便于排查问题

### 6. 开发流程规则

#### 6.1 需求分析规则
- **需求理解**：必须充分理解需求，避免误解需求
- **需求拆分**：复杂需求必须拆分为可实现的子任务
- **技术选型**：根据需求选择合适的技术方案

#### 6.2 代码开发规则
- **编码规范**：必须遵循项目的编码规范
- **代码复用**：优先使用现有代码，避免重复开发
- **单元测试**：关键功能必须编写单元测试

#### 6.3 代码审查规则
- **代码质量**：代码必须通过代码审查，确保代码质量
- **规范检查**：代码必须符合项目的开发规范
- **功能验证**：代码必须验证功能正确性

#### 6.4 部署规则
- **构建流程**：前端代码必须通过构建流程生成生产版本
- **部署脚本**：必须使用自动化部署脚本，避免手动部署
- **版本管理**：必须进行版本管理，便于回滚

### 7. 问题修复规则

#### 7.1 问题定位规则
- **问题分析**：必须充分分析问题，找出根本原因
- **日志排查**：利用日志排查问题，定位问题所在
- **重现问题**：必须能够重现问题，便于验证修复效果

#### 7.2 修复方案规则
- **方案设计**：必须设计合理的修复方案，避免引入新问题
- **影响评估**：必须评估修复方案的影响范围
- **风险控制**：必须考虑修复方案的风险，制定风险控制措施

#### 7.3 修复验证规则
- **功能验证**：必须验证修复后的功能是否正常
- **回归测试**：必须进行回归测试，确保不影响其他功能
- **性能验证**：必须验证修复后的性能是否符合要求

### 8. 最佳实践

#### 8.1 审批流程最佳实践
- **使用状态机管理流程**：避免复杂的if-else逻辑
- **流程节点可配置**：支持动态配置审批节点
- **流程操作可追溯**：所有流程操作必须有完整的日志记录

#### 8.2 数据库最佳实践
- **使用MyBatis-Plus简化开发**：避免编写重复的SQL
- **合理设计索引**：提升查询性能
- **使用事务管理**：确保数据一致性
- **分库分表**：处理大数据量场景

#### 8.3 前端最佳实践
- **组件化开发**：提高代码复用率
- **响应式设计**：适配不同设备
- **性能优化**：减少HTTP请求，优化渲染性能
- **用户体验优化**：提供友好的用户界面和交互体验

#### 8.4 团队协作最佳实践
- **代码审查**：确保代码质量
- **文档更新**：及时更新文档，保持文档与代码一致
- **知识共享**：定期分享技术和业务知识
- **持续集成**：自动化构建和测试流程

## 给AI的特别提示

1. **上下文记忆**：始终记住当前项目的类、方法、字段命名
2. **避免重复**：先检查是否存在类似功能，再决定是否创建
3. **字段转换**：SQL中必须将snake_case转换为lowerCamelCase
4. **双向验证**：
   - 修改后端时 → 考虑前端如何调用/展示（types/ 与 DTO 对齐）
   - 修改前端时 → 考虑后端接口格式（ResultVO 解包约定）
   - 修改数据库时 → 考虑前后端字段映射
5. **立即验证**：
   - 前端修改 → `pnpm build`（含 vue-tsc 严格类型检查）
   - 后端修改 → `./mvnw clean package` 并重启应用（Windows 下先停旧 java 进程）
6. **计划文档**：产出的plan开头的计划文档放在.trae/plan/目录下
7. **其他文档**：其他文档（如设计文档、用户手册等）放在docs/目录下
8. **任何功能开发，必须包含**：
   - Flyway 迁移脚本（含索引、注释，放 src/main/resources/db/migration/V<N>__xxx.sql）
   - 实体类（Entity）+ RO(入参) + VO（出参）
   - Mapper 接口 + XML/SQL（或 MyBatis-Plus 注解）
   - Service 接口 + ServiceImpl（含业务逻辑、事务）
   - Controller（含接口注解、参数校验、返回 ResultVO；springdoc @Tag/@Operation 注解）
   - 前端 types/ 类型 + api/ 封装 + views 页面 + 路由注册
   - 单元测试（随模块放 src/test 同包；集成测试用 Testcontainers，禁止依赖本机服务）
   - 各层依赖注入正确（推荐构造器注入，Controller 注入 Service，Service 注入 Mapper）
   - 数据库表字段 → 实体类字段 → DTO字段 → Mapper SQL → Service 逻辑 → Controller 参数，必须完全一致（名称、类型、非空约束）
9. **构建工具**：后端一律根目录 `./mvnw`（Maven 官方标准位置；禁止本机 mvn，3.6.0 无法解析 Boot 4 pom）；前端一律 pnpm（packageManager 字段固定）

## 项目结构文档树

## 1. 项目根目录结构

```
aiguibin-platform-arch/          # 项目根目录
├── .mvn/wrapper/                # Maven Wrapper 配置（必须提交，发行版 3.9.x）
├── .trae/                       # Trae IDE相关配置和文档
│   ├── plan/                    # 项目计划文档
│   ├── rules/                   # 项目开发规范
│   └── skills/                  # 技能库和工具集
├── Jenkinsfile                  # CI 流水线（Jenkins 标准：仓库根目录）
├── mvnw / mvnw.cmd              # Maven 构建入口（Maven 官方标准：项目根目录）
├── scripts/                     # 运维/工具脚本（restart/start/check_kill_port/local_debug_restart）
├── docs/                        # 项目文档目录
├── src/test/java/               # 测试：ArchitectureTest(分层守护) + 启动冒烟
├── docs/scaffold-spec.md        # 工程脚手架规范（新项目搭建提示词）
├── src/                         # 源代码目录
│   ├── main/                    # 主代码目录
│   │   ├── java/                # Java后端代码
│   │   ├── resources/           # 后端资源（application.yml、mapper XML、static 构建产物）
│   │   └── webapp/              # Vue 3 前端代码（Vite + TypeScript）
│   └── test/                    # 测试代码目录
├── .gitignore                   # Git忽略文件配置
├── pom.xml                      # Maven项目配置（Spring Boot 4.1 parent）
└── README.md                    # 项目说明文档（含技术基线表）
```

## 2. 后端代码结构 (src/main/java)

## 3. 后端资源目录 (src/main/resources)

## 4. 前端项目结构 (src/main/webapp)

## 5. 目录功能说明

| 目录/文件 | 主要功能 | 包含关键文件类型 |
|-----------|----------|------------------|
| **根目录** | 项目主目录，包含所有子目录和配置文件 | 构建脚本、配置文件、说明文档 |
| **.trae/** | Trae IDE相关配置和文档 | 设计方案、开发规范、技能库 |
| **Jenkinsfile** | CI 流水线（仓库根目录，Jenkins 标准） | pnpm lint/build → mvnw test package |
| **docs/** | 项目文档目录 | Markdown文档、设计方案 |
| **src/main/resources/db/migration/** | Flyway 迁移脚本 | V1__platform_rbac_schema.sql、V2__platform_rbac_data.sql |
| **src/main/java/** | 后端Java源代码 | Java类文件（控制器、服务、实体等） |
| **src/main/resources/** | 后端资源文件 | 配置文件、MyBatis映射文件、静态资源 |
| **src/main/webapp/** | 前端Vue 3项目代码 | Vue组件、TypeScript文件、Vite配置 |
| **config/** | 后端配置类 | MybatisPlusConfig（分页+审计填充） |
| **module/<模块名>/** | 业务模块（按功能分包） | controller/service(+impl)/mapper/entity/dto 各子包，随业务新增 |
| **common/** | 框架公共层 | ResultVO/ResultCode（result）、BusinessException + 全局异常（exception）、TraceIdFilter（web） |

| **components/** | Vue组件 | 可复用的Vue组件 |
| **router/** | 路由配置 | 静态壳路由（菜单驱动动态路由为预留能力） |
| **stores/** | 状态管理 | Pinia store（随业务模块新增） |
| **types/** | 类型定义 | 与后端DTO对齐的TS类型 |
| **views/** | 视图组件 | 页面级Vue组件（动态路由挂载目标） |

## 6. 技术栈说明

| 分类 | 技术栈 |
|------|--------|
| 后端框架 | Spring Boot 4.1.x（JDK 21，LTS） |
| 安全 | Spring Security + JWT（按需回补，当前未启用；基线 Security 7.x / jjwt 0.13） |
| 持久层框架 | MyBatis-Plus 3.5.x（mybatis-plus-spring-boot4-starter） |
| 数据库 | MySQL 8.4（驱动 mysql-connector-j） |
| 接口文档 | springdoc-openapi 3.0.3（/swagger-ui/index.html） |
| 数据库迁移 | Flyway（db/migration，骨架默认关闭） |
| 测试/格式 | ArchUnit 1.4.x 分层守护、Spotless（palantirJavaFormat）、ESLint 10 + Prettier |
| 缓存 | Redis（按需回补，当前未启用） |
| 前端框架 | Vue 3.5 + TypeScript 5.9（strict） |
| 构建工具 | Vite 8（Rolldown）、Maven Wrapper 3.9.x、pnpm ≥ 10.21 |
| UI / 状态 | Element Plus 2.14、Pinia 3、Vue Router 4.5、Axios 1 |

## 7. 项目架构特点

1. **前后端分离架构**：后端提供RESTful API，前端通过AJAX调用
2. **按功能分包**：业务模块 module/<模块名>/{controller,service,mapper,entity,dto}，ArchUnit 测试守护分层依赖
3. **认证**：框架默认不启用；业务需要时按基线回补 Spring Security + JWT
5. **统一异常处理**：全局异常捕获和统一返回格式
6. **可观测性**：logback 文件滚动日志 + TraceIdFilter 链路追踪（X-Trace-Id）

## 8. 框架核心模块（不含任何业务与认证功能）

1. **启动与约定**：统一响应（ResultVO/ResultCode）、全局异常处理、链路追踪（TraceIdFilter）、MyBatis-Plus 分页与审计字段填充
2. **前端壳**：布局（MainLayout）+ 空壳首页 + axios 封装（ResultVO 解包）
3. **质量设施**：接口文档（springdoc）、ArchUnit 分层守护测试、Spotless/ESLint 代码风格
4. **预留能力**：RBAC 十表（db/migration）、Spring Security + JWT、菜单驱动动态路由，均为业务模块按基线回补项

## 9. 开发流程规范

1. **代码设计**：先理解业务需求，设计代码结构
2. **代码编写**：基于设计实现代码，遵循命名规范
3. **代码审查**：符合规范后合并到主干
4. **文档生成**：所有计划文档放入`.trae/plan/`，其余文档都放在`docs/`目录
5. **前端构建**：`pnpm build`（vue-tsc 类型检查 + Vite 构建，产物输出 src/main/resources/static）
6. **后端重启**：`./mvnw clean package` 后重启应用；Windows 下重启前先停旧 java 进程

## 10. 部署说明

1. **后端部署**：打包为jar文件，通过Java命令运行（启停脚本见 scripts/restart.sh）
2. **前端部署**：`pnpm build` 生成静态资源到 src/main/resources/static，随 jar 一体打包（CI 见根目录 Jenkinsfile）
3. **数据库部署**：启用 Flyway（prod profile 已置 true）后启动时自动执行 db/migration 迁移
4. **环境配置**：application-dev/-prod.yml 区分环境，生产数据源用 ${DB_URL}/${DB_USERNAME}/${DB_PASSWORD} 环境变量注入

---

**文档版本**：v2.2
**生成日期**：2026-09-09
**适用范围**：开发人员、架构师、测试人员
