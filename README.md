# aiguibin-platform-arch

前后端一体化的架构管理系统（Spring Boot 2.7 + MyBatis-Plus + MySQL，前端 Vue 2 + Element UI，支持 RBAC 与 CSRF）。

## 项目结构
```
aiguibin-platform-arch/
├── src/main/java/com/aiguibin/platform/arch/      # Java 源码
│   ├── controller/                               # REST 控制器
│   ├── entity/                                   # 实体类
│   ├── mapper/                                   # MyBatis-Plus Mapper
│   ├── service/                                  # 业务层
│   ├── dto/                                      # 数据传输对象
│   ├── config/                                   # 配置类（分页、拦截器、异常处理、密码加密等）
│   └── SpringbootStarterApplication.java         # 启动类
├── src/main/resources/
│   ├── static/                                   # Vue 构建后的静态资源
│   ├── templates/                                # 预留模板目录
│   ├── application.yml                           # Spring Boot 配置
│   └── mapper/                                   # XML 映射（MyBatis-Plus无需也可运行）
└── src/main/webapp/                              # Vue 前端源码
    ├── public/
    ├── src/
    │   ├── components/
    │   ├── views/
    │   ├── router/
    │   ├── api/
    │   ├── utils/
    │   └── store/（可选）
    ├── package.json
    └── vue.config.js
```

## 数据库准备
- MySQL 建库：`aiguibin_platform_arch`
- 表结构：

```sql
-- ============================================
-- 权限管理及业务系统建表语句
-- 使用统一的字符集和排序规则
-- ============================================
```

## 构建与运行
- 构建：` mvn -gs "D:\Maven\settings-aiguibin.xml" -Dmaven.repo.local="E:\Repository\Local" -T 1C clean package -DskipTests -U -Dmaven.compile.fork=true`
- 运行：`java -jar target/aiguibin-platform-arch.jar`
- 调试：`sh local_debug_restart.sh full-restart`
- 访问：`http://localhost:8080`
- 前端：`cd /e/WorkSpace/HelloWorldBackend/aiguibin-platform-arch/src/main/webapp && npm run build`

## 认证与安全
- 登录：`POST /api/login`，入参：`{ userNum, password }`
- 返回：`token`（Bearer）、`csrfToken`（CSRF防护）、`userNum`、`orgCode: ""`
- 所有变更类接口（POST/PUT/PATCH/DELETE）必须在请求头携带：
  - `Authorization: Bearer <token>`
  - `X-CSRF-Token: <csrfToken>`
- 存储安全：密码采用 BCrypt 加密；传输安全：生产环境启用 HTTPS。

## 用户功能
- 用户管理接口（需 `Authorization` + `X-CSRF-Token`）：
  - `GET /api/users?page=1&size=10&userNum=&userName=` 列表
  - `GET /api/users/{id}` 详情
  - `POST /api/users` 新增（初始密码强制为 `666666`）
  - `PUT /api/users/{id}` 更新（支持改口令）
  - `DELETE /api/users/{id}` 删除
- 修改密码：
  - `POST /api/users/change-password`
  - 入参：`{ currentPassword, newPassword, confirmPassword }`
  - 校验：当前密码验证；新密码强度（至少6位且包含字母和数字）；一致性校验；CSRF校验
  - 成功返回：`"密码修改成功"`；失败返回相应错误信息