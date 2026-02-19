# 修改菜单API端点路径

## 需求分析

需要将前端API调用从 `/menus/user` 修改为 `/menus/userMenus`，并相应更新后端控制器的映射路径。

## 实现计划

### 1. 前端修改

* **文件**: `src/main/webapp/src/api/index.js`

* **修改内容**: 将第10行的 `export const getUserMenus = () => request.get('/menus/user');` 修改为 `export const getUserMenus = () => request.get('/menus/userMenus');`

### 2. 后端修改

* **文件**: `src/main/java/com/aiguibin/platform/arch/controller/MenuController.java`

* **修改内容**: 将第33行的 `@GetMapping("/user")` 修改为 `@GetMapping("/userMenus")`

## 影响范围

* **前端**: `MainLayout.vue` 组件中调用了 `getUserMenus()` 方法来加载菜单数据

* **后端**: `MenuController.java` 中的 `getUserMenus` 方法端点需要修改

## 测试建议

1. 启动前后端服务
2. 登录系统验证菜单是否正常加载
3. 检查浏览器控制台是否有API调用错误
4. 验证其他菜单相关功能是否正常

