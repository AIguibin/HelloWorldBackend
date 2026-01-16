## 添加后端返回字段到localStorage

### 需求分析
根据用户提供的后端代码，登录接口返回了多个字段，但目前前端只保存了部分字段到localStorage中。需要将所有后端返回的字段都保存到localStorage。

### 实现步骤
1. **定位修改位置**：在`Login.vue`文件的第174行之后，即保存`csrfToken`的位置。
2. **添加缺失字段**：将后端返回的以下字段添加到localStorage：
   - `tokenType`
   - `expiresIn`
   - `refreshToken`
   - `loginTime`
   - `selectedOrgCode`
   - `selectedOrgTime`
   - `sessionId`
3. **保持代码风格**：遵循现有的代码格式和命名规范。

### 预期效果
修改后，所有后端返回的登录信息字段都会被正确保存到localStorage中，前端可以根据需要使用这些字段。

### 修改文件
- `e:\WorkSpace\HelloWorldBackend\aiguibin-platform-arch\src\main\webapp\src\views\Login.vue`