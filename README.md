# HelloWorldBackend
# 数据库与Excel互转项目开发指南

## 技术选型建议

### 后端技术栈
| 技术                | 说明                                                                 |
|---------------------|--------------------------------------------------------------------|
| Spring Boot         | 快速构建Web应用，简化配置                                              |
| Apache POI/EasyExcel| Excel处理（POI功能全面，EasyExcel适合大数据量）                          |
| MyBatis/Spring Data | 数据库操作（MyBatis灵活，Spring Data JPA开发快速）                       |
| JDBC Driver         | 根据数据库类型选择（MySQL、PostgreSQL等）                                |
| Swagger             | API文档生成                                                          |
| Quartz              | 定时任务（可选，用于定时同步）                                           |

### 前端技术栈（Web界面版）
| 技术                | 说明                                                                 |
|---------------------|--------------------------------------------------------------------|
| Vue.js              | 前端框架                                                            |
| Element UI          | UI组件库                                                           |
| Axios               | HTTP客户端                                                         |
| FileSaver.js        | 文件保存工具                                                         |
| XLSX.js             | 前端Excel处理（可选）                                                 |

### 命令行工具版
| 技术                | 说明                                                                 |
|---------------------|--------------------------------------------------------------------|
| Picocli             | 命令行界面开发                                                       |
| Logback             | 日志记录                                                            |

## 项目结构示例（Maven多模块）

```
excel-db-sync/
├── excel-core/               # 核心模块
│   ├── src/main/java
│   │   └── com/example/core/
│   │       ├── excel/        # Excel处理核心逻辑
│   │       ├── db/           # 数据库操作核心
│   │       └── converter/    # 数据转换器
├── excel-web/                # Web模块
│   ├── src/main/java
│   │   └── com/example/web/
│   │       ├── controller/   # API接口
│   │       ├── dto/          # 数据传输对象
│   │       └── config/       # 配置类
│   └── src/main/resources
│       ├── static/           # 前端资源
│       └── templates/        # 模板文件（可选）
├── excel-cli/                # 命令行模块（可选）
└── pom.xml
```

## 核心功能实现方案

### 1. Excel导出数据库表
```java
// 使用EasyExcel示例
public class ExcelExporter {
    public void export(String tableName, HttpServletResponse response) {
        List<Map<String, Object>> data = jdbcTemplate.queryForList("SELECT * FROM " + tableName);
        
        EasyExcel.write(response.getOutputStream())
            .head(generateHeaders(tableName))
            .sheet(tableName)
            .doWrite(data);
    }
    
    private List<List<String>> generateHeaders(String tableName) {
        // 获取数据库表结构生成表头
        List<String> columns = getTableColumns(tableName);
        return columns.stream()
            .map(col -> Collections.singletonList(col))
            .collect(Collectors.toList());
    }
}
```

### 2. Excel导入数据库
```java
public class ExcelImporter {
    @Transactional
    public void importData(String tableName, MultipartFile file) {
        List<Map<Integer, String>> dataList = EasyExcel.read(file.getInputStream())
            .sheet()
            .headRowNumber(1)
            .doReadSync();

        dataList.forEach(row -> {
            String sql = generateInsertSQL(tableName, row);
            jdbcTemplate.execute(sql);
        });
    }
    
    private String generateInsertSQL(String tableName, Map<Integer, String> row) {
        // 根据行列位置生成插入语句
        // 实际应使用PreparedStatement防止SQL注入
    }
}
```

## 高级功能实现

### 1. 映射配置（JSON/YAML）
```yaml
# table_mapping.yml
users:
  excel:
    startRow: 2
    columns:
      - name: "用户名"
        field: "username"
        type: "string"
      - name: "注册时间"
        field: "create_time"
        type: "datetime"
  database:
    table: "t_user"
    keyColumn: "id"
```

### 2. 数据校验
```java
public class DataValidator {
    public void validate(Map<String, Object> rowData) {
        // 自定义校验规则
        if (rowData.get("age") != null) {
            int age = Integer.parseInt(rowData.get("age").toString());
            if (age < 0 || age > 150) {
                throw new ValidationException("年龄范围无效");
            }
        }
    }
}
```

### 3. 大文件处理（分页读取）
```java
public void processLargeFile(MultipartFile file) {
    AnalysisEventListener<Map<Integer, String>> listener = new AnalysisEventListener<>() {
        private static final int BATCH_SIZE = 1000;
        private List<Map<Integer, String>> cachedList = new ArrayList<>();

        @Override
        public void invoke(Map<Integer, String> data, AnalysisContext context) {
            cachedList.add(data);
            if (cachedList.size() >= BATCH_SIZE) {
                saveBatch(cachedList);
                cachedList = new ArrayList<>();
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            saveBatch(cachedList);
        }
    };

    EasyExcel.read(file.getInputStream(), listener).sheet().doRead();
}
```

## 工程实践建议

1. **安全防护**：
   - 文件类型白名单校验
   - SQL注入防护（使用PreparedStatement）
   - 文件大小限制（Spring Boot配置）
   ```properties
   spring.servlet.multipart.max-file-size=100MB
   spring.servlet.multipart.max-request-size=100MB
   ```

2. **性能优化**：
   - 使用连接池（HikariCP）
   - 批量插入操作
   - 异步处理大文件
   ```java
   @Async
   public Future<String> asyncImport(String taskId, MultipartFile file) {
       // 导入处理逻辑
   }
   ```

3. **错误处理**：
   - 详细错误日志记录
   - 错误行记录与导出
   - 进度跟踪接口
   ```java
   @GetMapping("/progress/{taskId}")
   public Progress getProgress(@PathVariable String taskId) {
       return taskService.getProgress(taskId);
   }
   ```

## 界面设计建议（Web版）

```vue
<template>
  <div class="container">
    <el-upload
      action="/api/import"
      :before-upload="handleBeforeUpload"
      :on-success="handleSuccess">
      <el-button type="primary">选择Excel文件</el-button>
    </el-upload>

    <el-table :data="tables">
      <el-table-column prop="tableName" label="数据表"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button @click="exportTable(scope.row)">导出</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div v-if="progress.visible" class="progress-container">
      <el-progress :percentage="progress.percent"></el-progress>
      <div>{{ progress.message }}</div>
    </div>
  </div>
</template>
```

## 部署架构

```
+-----------------+        +-----------------+
|   Web Browser   |        |  CLI Client     |
+-----------------+        +-----------------+
         ↓                          ↓
+-----------------+        +-----------------+
|  Nginx Reverse  |        |  Java Runtime   |
|     Proxy       |        |  Environment    |
+-----------------+        +-----------------+
         ↓
+-----------------+
| Spring Boot App |
+-----------------+
         ↓
+-----------------+
|   Database      |
+-----------------+
```

## 关键配置文件示例

### application.yml
```yaml
excel:
  max-rows: 1000000
  allowed-types: [xlsx, xls]
  
database:
  driver-class-name: com.mysql.cj.jdbc.Driver
  url: jdbc:mysql://localhost:3306/mydb
  username: root
  password: 123456

async:
  pool:
    core-size: 10
    max-size: 50
    queue-capacity: 1000
```

## 开发路线建议

1. **核心功能开发**：
   - 实现基本导入/导出功能
   - 添加数据库元数据读取
   - 支持常见数据类型转换

2. **增强功能开发**：
   - 添加映射配置支持
   - 实现数据校验规则
   - 添加任务队列管理

3. **界面优化**：
   - 实现可视化映射配置
   - 添加实时进度显示
   - 支持模板下载

4. **企业级功能**：
   - 添加LDAP/SSO集成
   - 实现操作审计日志
   - 添加分布式任务支持

## 注意事项

1. **内存管理**：
   - 使用流式API处理大文件
   - 限制最大处理行数
   - 及时关闭IO资源

2. **数据一致性**：
   - 使用数据库事务
   - 实现断点续传
   - 添加唯一性校验

3. **格式兼容性**：
   - 处理不同Excel版本
   - 处理不同字符编码
   - 处理日期格式转换

4. **扩展性设计**：
   - 使用策略模式处理不同数据库
   - 支持插件式扩展
   - 提供API接口

该项目可以作为基础数据交换平台，后续可扩展以下功能：
- 支持更多文件格式（CSV、JSON）
- 添加数据转换流水线
- 集成消息队列实现异步处理
- 支持云存储（OSS、S3）对接


在Maven多模块项目中，`excel-core`和`excel-web`的模块关系及运行方式如下：

---

### **模块关系**
#### 1. **依赖关系**
- `excel-web` **依赖** `excel-core`
- `excel-core` **不依赖** `excel-web`

#### 2. **职责划分**
| 模块          | 职责                                                                 | 是否可独立运行 |
|---------------|--------------------------------------------------------------------|----------------|
| `excel-core`  | 封装核心逻辑：<br>- Excel解析/生成<br>- 数据库操作<br>- 数据转换规则<br>- 公共工具类 | ❌ 不可独立运行 |
| `excel-web`   | 提供Web应用能力：<br>- REST API<br>- 前端界面<br>- Web安全配置<br>- 文件上传下载    | ✅ 可独立运行 |

---

### **启动规则**
#### 场景1：Web应用开发
- **只需启动 `excel-web`**
- `excel-core` 会作为依赖被自动打包到 `excel-web` 中
- 启动方式：
  ```bash
  # 在项目根目录下
  mvn spring-boot:run -pl excel-web
  ```

#### 场景2：核心功能调试
- **无需启动任何模块**
- 直接对 `excel-core` 编写单元测试：
  ```java
  // ExcelConverterTest.java
  @SpringBootTest
  public class ExcelConverterTest {
      @Autowired
      private ExcelConverter converter;
      
      @Test
      public void testExport() {
          List<User> data = Arrays.asList(new User("Alice", 25));
          ByteArrayOutputStream output = converter.export(data);
          assertTrue(output.size() > 0);
      }
  }
  ```

---

### **模块交互示意图**
```
+----------------+       +----------------+
|   excel-web    |       |   excel-cli    |
| (Web应用入口)   |       | (命令行入口)    |
+----------------+       +----------------+
        ▲                       ▲
        |                       |
        ▼                       ▼
+-------------------------------+
|          excel-core           |
| (核心逻辑，无启动入口)          |
+-------------------------------+
```

---

### **典型开发流程**
1. **开发阶段**
   ```bash
   # 安装core模块到本地仓库
   mvn install -pl excel-core -am
   
   # 启动web模块（自动包含core）
   mvn spring-boot:run -pl excel-web
   ```

2. **生产打包**
   ```bash
   # 打包整个项目
   mvn clean package
   
   # 生成的可运行JAR位置
   excel-web/target/excel-web-1.0.0.jar
   ```

---

### **为什么这样设计？**
1. **代码复用**：核心逻辑可被Web、CLI、未来其他模块复用
2. **关注点分离**：Web模块只关注HTTP相关逻辑
3. **构建优化**：修改core代码时只需重新编译依赖它的模块
4. **安全隔离**：Web模块可以独立配置安全策略而不影响核心逻辑

实际项目中，你只需要关注`excel-web`的启动，核心模块会被自动集成。这种设计是典型的分层架构（Layered Architecture）实践。