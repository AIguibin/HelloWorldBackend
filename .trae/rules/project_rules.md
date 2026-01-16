## 📋 项目规范

### **1. 代码结构与命名规范**
- **实体类(Entity)**：每个数据库表对应一个`UpperCamelCase`命名的Entity类
- **Mapper接口**：每个Entity对应一个Mapper接口，按`UpperCamelCase`命名
- **全局异常处理**：统一处理空值、验证异常、业务异常等，
- **全局异常捕获**：自定义业务异常 BusinessException
- **详细日志**：关键业务节点添加`INFO/DEBUG`级别日志，便于监控排查
- **统一返回格式**：Result<T> 类（包含 code:Integer、msg:String、data:T）
- **命名规范**：
  - 数据库表：下划线命名（如 product_info、order_main）
  - 实体类：驼峰命名（对应表名，如 ProductInfo、OrderMain）
  - Mapper接口：XXXMapper（如 ProductInfoMapper）
  - Service接口：XXXService + 实现类：XXXServiceImpl
  - Controller：XXXController（如 ProductController）
  - 字段：数据库下划线→实体类驼峰（如 product_name → productName）
- **任何功能开发，必须包含**：
  - 数据库表 SQL 脚本（含索引、注释）
  - 实体类（Entity）+ RO(入参) + VO（出参）
  - Mapper 接口 + XML/SQL（或 MyBatis-Plus 注解）
  - Service 接口 + ServiceImpl（含业务逻辑、事务）
  - Controller（含接口注解、参数校验、返回Result）
  - 各层依赖注入正确（如 Service 注入 Mapper，Controller 注入 Service）  
  - 数据库表字段 → 实体类字段 → DTO字段 → Mapper SQL → Service 逻辑 → Controller 参数，必须完全一致（名称、类型、非空约束）
  - 禁止出现“数据库有字段但实体类没有”“SQL用错字段名”等情况


### **2. 命名约定（严格执行）**
| 组件类型 | 命名规范 | 示例 |
|---------|---------|------|
| Java类 | UpperCamelCase | `UserController` |
| 方法/参数 | lowerCamelCase | `getUserById()` |
| 前端请求/响应参数 | lowerCamelCase | `{ "userName": "张三" }` |
| 数据库字段 | snake_case | `user_name` |
| 前端变量/属性 | lowerCamelCase | `userName` |
| 前端文件/组件 | lowerCamelCase | `userList.vue` |

### **3. 数据库与SQL规范**
```sql
-- ❌ 避免：字段名直接暴露
SELECT user_name FROM users;

-- ✅ 必须：使用AS转换为驼峰
SELECT 
    user_id AS userId,
    user_name AS userName,
    create_time AS createTime
FROM users;
```

### **4. 注释规范（Javadoc格式）**
**实体类示例：**
```java
/**
 * 用户实体类
 * 对应数据库表：sys_user
 */
public class User {
    /**
     * 用户ID - 主键
     * 类型：Long，必填：是，默认值：无，备注：自增主键
     */
    private Long userId;
    
    /**
     * 用户名
     * 类型：String，必填：是，默认值：无，备注：唯一，长度3-20
     */
    private String userName;
    // ... 其他字段
}
```

**方法注释示例：**
```java
/**
 * 根据ID查询用户
 * @param userId 用户ID，必填
 * @return 用户实体，查询成功返回User对象，未找到返回null
 * @throws ServiceException 参数非法或系统异常时抛出
 * @影响数据库：SELECT操作
 */
User getUserById(Long userId);
```

### **5. 前后端交互规范**
**请求/响应格式：**
```json
// 请求（lowerCamelCase）
{
    "pageNum": 1,
    "pageSize": 10,
    "userName": "张三"
}

// 成功响应（统一包装）
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "userId": 1,
        "userName": "张三",
        "createTime": "2024-01-01 10:00:00"
    }
}

// 错误响应
{
    "code": 500,
    "message": "系统内部错误",
    "data": null
}
```

### **6. 开发流程规范**
0. **代码设计** → 先理解业务需求，并依据`sql/`下的表结构与初始化数据，再设计代码结构
1. **代码编写** → 基于代码设计思考，使用现有类/方法，避免重复
2. **代码审查** → 符合规范后再合并到主干
3. **文档生成** → 所有文档放入 `.trae/plan/`
4. **前端构建** → `npm run build`（生成dist目录）
5. **后端重启** → Debug模式下重启SpringBoot应用

### **7. 异常处理原则**
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    // 处理空指针
    // 处理业务异常
    // 处理验证异常
    // 处理数据库异常
    // 统一返回格式
}
```

### **8. 日志记录标准**
```java
// 关键节点必须记录
log.info("开始处理用户请求，userId: {}", userId);
log.debug("查询参数: {}", queryParams);
log.warn("用户输入异常: {}", input);
log.error("数据库操作失败", e);
```

### **9. 前后端字段映射规则**
```
数据库字段 → SQL转换 → Java实体 → 前端展示
user_name → AS userName → userName → {{ userName }}
注意：不能依赖框架自动转换，必须在SQL中显式转换
```

---

## 🎯 **给AI的特别提示**

1. **上下文记忆**：始终记住当前项目的类、方法、字段命名
2. **避免重复**：先检查是否存在类似功能，再决定是否创建
3. **字段转换**：SQL中必须将snake_case转换为lowerCamelCase
4. **双向验证**：
   - 修改后端时 → 考虑前端如何调用/展示
   - 修改前端时 → 考虑后端接口格式
   - 修改数据库时 → 考虑前后端字段映射
5. **立即验证**：
   - 前端修改 → 运行 `npm run build`
   - 后端修改 → 重启SpringBoot应用

## 📁 **文档结构**
```
.trae/plan/
├── api-design/          # API设计文档
├── database/           # 数据库设计文档
├── coding-standards/   # 编码规范文档
└── changelog/         # 变更记录
```
