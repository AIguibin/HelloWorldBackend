## 优化动态路由功能，避免重复API请求

### 问题分析
当前代码中存在重复的API请求问题：
- `MainLayout.vue` 通过 `getUserMenus()` API 调用获取菜单数据
- `router/index.js` 通过 `fetchMenuData()` 函数再次调用相同的 API

### 解决方案

#### 1. 修改 MainLayout.vue
- 在 `loadMenus()` 方法中，优先从 `localStorage.getItem('menuPermissions')` 获取菜单数据
- 如果本地存储中没有数据，调用 `getUserMenus()` 获取
- 获取数据后，将其存储到 `localStorage.setItem('menuPermissions', JSON.stringify(data))`

#### 2. 修改 router/index.js
- 移除 `fetchMenuData()` 函数
- 修改 `initDynamicRoutes()` 函数，只从 `localStorage.getItem('menuPermissions')` 获取菜单数据
- 确保路由初始化时能够正确获取菜单数据

#### 3. 同步菜单数据更新
- 确保 `MainLayout.vue` 中菜单数据更新时会同步更新本地存储
- 确保路由模块能够获取到最新的菜单数据

### 实现步骤

1. **修改 MainLayout.vue**
   - 修改 `loadMenus()` 方法，添加本地存储读取和写入逻辑
   - 优先从本地存储获取菜单数据
   - 当本地存储中没有数据时，调用 API 获取
   - 获取数据后，将其存储到本地存储

2. **修改 router/index.js**
   - 移除 `fetchMenuData()` 函数
   - 修改 `initDynamicRoutes()` 函数，只从本地存储读取菜单数据
   - 确保路由初始化时能够正确获取菜单数据

3. **测试验证**
   - 确保应用启动时只发送一次菜单数据请求
   - 验证动态路由功能正常工作
   - 确保菜单数据更新时路由能够正确响应

### 技术要点

- 使用本地存储作为菜单数据的缓存介质
- 优先从本地存储读取数据，避免重复 API 请求
- 确保菜单数据更新时的同步性
- 保持代码的简洁性和可维护性