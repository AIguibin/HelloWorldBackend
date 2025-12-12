# aiguibin-platform-arch

前后端一体化的架构管理系统（Spring Boot 2.7 + MyBatis-Plus + MySQL，前端 Vue 2 + Element UI，支持 RBAC 与 CSRF）。

## 项目结构
```
aiguibin-platform-arch/
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
- MySQL 建库：`aiguibin_arch_tables`
- 表结构：

```sql
-- ============================================
-- 权限管理及业务系统建表语句
-- 使用统一的字符集和排序规则
-- ============================================
```

## 构建与运行
- 构建：` mvn -gs "D:\Maven\settings-aiguibin.xml" -Dmaven.repo.local="E:\Repository\Local" -T 1C clean package -DskipTests -U -Dmaven.compile.fork=true -Dmaven.test.skip=true`
- 运行：`java -jar target/aiguibin-platform-arch.jar`
- 调试：`sh local_debug_restart.sh full-restart`
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


   权限分类体系
用户权限通常分为三大类：功能权限、数据权限和字段权限。
权限体系
├── 功能权限 (能做什么)
├── 数据权限 (能看到什么数据)
└── 字段权限 (能看到什么字段信息)
 功能权限
2.1 菜单权限
控制用户能够访问的系统菜单和页面。
权限粒度：
● 目录权限：能否看到一级菜单目录
● 页面权限：能否访问具体功能页面
● 子菜单权限：能否看到页面内的子菜单
示例：
├── 系统管理 (目录)
│   ├── 用户管理 (页面)
│   ├── 角色管理 (页面)
│   └── 菜单管理 (页面)
├── 业务管理 (目录)
│   ├── 订单管理 (页面)
│   └── 客户管理 (页面)
└── 报表管理 (目录)
    ├── 销售报表 (页面)
    └── 统计报表 (页面)
2.2 操作权限
控制用户在页面内的具体操作能力。
权限类型：
● 按钮权限：页面内按钮的显示和操作权限
● 链接权限：页面内链接的访问权限
● 操作权限：特定的功能操作
常见操作权限示例：
-- 用户管理模块操作权限
user:create     -- 创建用户
user:update     -- 修改用户
user:delete     -- 删除用户
user:view       -- 查看用户
user:export     -- 导出用户
user:reset-pwd  -- 重置密码
user:assign-role -- 分配角色

-- 订单管理模块操作权限
order:create    -- 创建订单
order:edit      -- 编辑订单
order:cancel    -- 取消订单
order:approve   -- 审核订单
order:reject    -- 驳回订单
order:export    -- 导出订单
2.3 接口权限
控制后端API接口的访问权限。
权限类型：
● HTTP方法权限：GET、POST、PUT、DELETE等
● API路径权限：具体的接口路径访问权限
● 接口参数权限：接口参数的访问限制
示例：
// 接口权限配置
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping
    @PreAuthorize("hasPermission('user:list')")
    public List<User> getUsers() { ... }
    
    @PostMapping
    @PreAuthorize("hasPermission('user:create')")
    public User createUser(@RequestBody User user) { ... }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('user:update')")
    public User updateUser(@PathVariable Long id, @RequestBody User user) { ... }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('user:delete')")
    public void deleteUser(@PathVariable Long id) { ... }
    
    @GetMapping("/change-password")
    @PreAuthorize("hasPermission('user:change-password')")
    public void changePassword(@RequestBody PasswordChangeRequest request) { ... }
}
3. 数据权限
3.1 数据范围权限
控制用户能够访问的数据范围。
权限级别：
● 全部数据权限：可以访问所有数据
● 本机构数据权限：只能访问自己所在机构及下属机构的数据
● 本部门数据权限：只能访问自己所在部门及下属部门的数据
● 本人数据权限：只能访问自己创建的数据
● 自定义数据权限：按照自定义规则访问数据
数据权限实现示例：
3.2 数据操作权限
控制用户对数据的操作能力。
权限类型：
● 查询权限：能否查询数据
● 新增权限：能否新增数据
● 修改权限：能否修改数据
● 删除权限：能否删除数据
● 导入权限：能否导入数据
● 导出权限：能否导出数据
3.3 数据字段权限
控制用户能够看到和操作的数据字段。
权限类型：
● 字段可见性：某些字段对特定用户隐藏
● 字段可编辑性：某些字段对特定用户只读
● 字段必填控制：某些字段对特定用户必填
字段权限示例：
// 字段权限控制 - 使用 sys_user 表字段
public class User {
    // 公开可见字段
    private String userNum;       // 用户编号
    private String userName;      // 用户姓名
    private String nickname;      // 用户昵称
    private String deptCode;      // 主部门编码
    private String email;         // 邮箱
    private String phone;         // 手机号
    
    // 仅管理员可见字段
    @JsonView(Views.AdminOnly.class)
    private String password;      // 密码（加密存储）
    
    @JsonView(Views.AdminOnly.class)
    private Integer loginCount;   // 登录次数
    
    @JsonView(Views.AdminOnly.class)
    private LocalDateTime lastLoginTime; // 最后登录时间
    
    @JsonView(Views.AdminOnly.class)
    private String lastLoginIp;   // 最后登录IP
    
    // 仅HR和管理员可见字段
    @JsonView(Views.HRAndAdmin.class)
    private LocalDateTime pwdExpireTime; // 密码过期时间
    
    @JsonView(Views.HRAndAdmin.class)
    private LocalDateTime pwdModifiedTime; // 密码最后修改时间
}
4. 组织架构权限
4.1 机构权限
基于组织机构的权限控制。
权限维度：
● 机构范围：能够管理的机构范围
● 机构级别：能够管理的机构层级
● 机构类型：能够管理的机构类型
4.2 部门权限
基于部门的权限控制。
权限类型：
● 本部门权限：只能管理本部门
● 跨部门权限：可以管理多个部门
● 下级部门权限：可以管理下级部门
5. 时间维度权限
5.1 时间范围权限
控制用户权限的有效时间范围。
权限类型：
● 永久权限：长期有效
● 临时权限：指定时间段内有效
● 定时权限：特定时间点生效
5.2 访问时段权限
控制用户在什么时间段可以访问系统。
示例：
-- 时间权限表