# 修复MainLayout菜单点击问题

## 问题分析
在`MainLayout.vue`的`handleNavClick`方法中，当前逻辑是只要菜单有path就会进行导航跳转。但根据需求，menuType为"M"（目录类型）的菜单不应该支持点击导航。

## 解决方案
在`handleNavClick`方法中添加menuType检查，当menuType为"M"时直接返回，不执行导航逻辑。

## 修复步骤
1. 打开`MainLayout.vue`文件
2. 定位到`handleNavClick`方法（行189-198）
3. 在方法开始处添加menuType检查：
   ```javascript
   // 如果是目录类型，不支持点击
   if (menu.menuType === "M") {
     return;
   }
   ```
4. 保存文件

## 预期效果
- menuType为"M"的目录菜单点击时无响应
- 其他类型（如页面、按钮）的菜单正常导航

## 验证方法
- 运行前端服务
- 点击目录类型菜单，确认无导航跳转
- 点击页面类型菜单，确认正常导航