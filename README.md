# aiguibin-online-table

前后端一体化的变更登记系统（Spring Boot 2.7 + MyBatis-Plus + MySQL，前端 Vue 2 + Element UI，支持 RBAC 与 CSRF）。

## 项目结构
```
aiguibin-online-table/
├── src/main/java/com/aiguibin/online/table/      # Java 源码
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
- MySQL 建库：`aiguibin_online_tables`
- 表结构：

```sql
CREATE TABLE `system_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `usernumb` varchar(50) NOT NULL COMMENT '用户编号',
  `username` varchar(50) NOT NULL COMMENT '用户姓名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_usernumb` (`usernumb`)
);
```

> 说明：`application.yml` 已指向上述数据库名；表字段与实体 `User` 完全匹配（`create_time`/`update_time` 通过 MyBatis-Plus 自动映射到 `createTime`/`updateTime`）。

建议初始化一个测试用户（初始密码策略固定为 `666666`，存储为BCrypt）：
```sql
INSERT INTO `system_user` (`usernumb`, `username`, `password`) VALUES
('admin', '管理员', '$2a$10$w9wHk7WbYULh6R8aC9mO4uF7B3vH1Jp4sZpZfYF0xXqE1u3xYlZl6');
-- 上述密文对应明文：666666（示例，具体以你环境生成的BCrypt为准）
```

## 构建与运行
- 构建：`mvn -DskipTests clean package`
- 运行：`java -jar target\aiguibin-online-table.jar`
- 访问：`http://localhost:8080`

## 认证与安全
- 登录：`POST /api/login`，入参：`{ username, password }`
- 返回：`token`（Bearer）、`csrfToken`（CSRF防护）、`username`、`chineseName: ""`
- 所有变更类接口（POST/PUT/PATCH/DELETE）必须在请求头携带：
  - `Authorization: Bearer <token>`
  - `X-CSRF-Token: <csrfToken>`
- 存储安全：密码采用 BCrypt 加密；传输安全：生产环境启用 HTTPS。

## 用户功能
- 用户管理接口（需 `Authorization` + `X-CSRF-Token`）：
  - `GET /api/users?page=1&size=10&usernumb=&username=` 列表
  - `GET /api/users/{id}` 详情
  - `POST /api/users` 新增（初始密码强制为 `666666`）
  - `PUT /api/users/{id}` 更新（支持改口令）
  - `DELETE /api/users/{id}` 删除
- 修改密码：
  - `POST /api/users/change-password`
  - 入参：`{ currentPassword, newPassword, confirmPassword }`
  - 校验：当前密码验证；新密码强度（至少6位且包含字母和数字）；一致性校验；CSRF校验
  - 成功返回：`"密码修改成功"`；失败返回相应错误信息

## 变更记录
- 数据库名更新为 `aiguibin_online_tables`
- 用户表改为 `system_user`，新增字段 `usernumb`（唯一约束 `uk_usernumb`），移除 `chineseName`
- 实体 `User` 与登录响应已适配新结构；新增密码修改流程与CSRF安全机制