<template>
  <div class="main-layout">
    <!-- 顶部导航栏 -->
    <div class="top-navbar">
      <div class="navbar-container">
        <!-- 系统标题 -->
        <div class="logo-container">
          <h1 class="system-title">架构管理系统</h1>
        </div>
        
        <!-- 顶部菜单导航 -->
        <div class="top-menu-container">
          <div class="top-menu-wrapper">
            <ul class="top-menu">
              <li 
                v-for="menu in topMenuList" 
                :key="menu.id"
                class="top-menu-item"
                :class="{ 'active': currentMenu === menu.path }"
                @click="toggleSubMenu(menu)"
                @mouseleave="closeSubMenuOnLeave(menu)"
              >
                <span class="menu-text">{{ menu.menuName }}</span>
                <i v-if="hasChildren(menu)" class="el-icon-arrow-down submenu-arrow" :class="{ 'rotate': showSubMenu === menu.id }"></i>
                
                <!-- 子菜单 -->
                <div 
                  v-if="hasChildren(menu)" 
                  class="submenu"
                  :class="{ 'show': showSubMenu === menu.id }"
                >
                  <ul class="submenu-list">
                    <li 
                      v-for="child in menu.children" 
                      :key="child.id"
                      class="submenu-item"
                      :class="{ 'active': currentMenu === child.path }"
                      @click.stop="navigateTo(child)"
                    >
                      {{ child.menuName }}
                    </li>
                  </ul>
                </div>
              </li>
            </ul>
          </div>
        </div>
        
        <!-- 用户信息区域 -->
        <div class="user-info-container">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <i class="el-icon-user"></i>
              <span>{{ currentUser.username || '用户' }}</span>
              <i class="el-icon-arrow-down"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="changePassword">
                <i class="el-icon-lock"></i> 修改密码
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                <i class="el-icon-switch-button"></i> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
      <!-- 内容区域 -->
      <el-main class="main-content">
        <div class="main-content-wrapper">
          <!-- 首页内容 -->
          <DashboardCards v-if="isHomePage" />
          
          <!-- 其他页面内容 -->
          <transition v-else name="fade" mode="out-in">
            <div class="child-page-container">
              <router-view v-slot="{ Component }">
                <component :is="Component" />
              </router-view>
            </div>
          </transition>
        </div>
      </el-main>
  </div>
</template>

<script>
import { getUserMenus } from '../api';
import DashboardCards from './DashboardCards.vue';

export default {
  name: 'MainLayout',
  components: {
    DashboardCards
  },
  data() {
      return {
        // 顶部菜单数据
        currentUser: {},
        breadcrumbList: [],
        // 菜单加载状态
        menuLoading: false,
        // 当前选中的主菜单
        currentMenu: '',
        // 当前显示的子菜单ID
        showSubMenu: null,
        // 当前是否显示首页内容
        isHomePage: true,
        // 菜单数据（通过API获取）
        topMenuList: []
    };
  },
  watch: {
    $route: {
      immediate: true,
      handler(to) {
        this.activeMenu = to.path;
        this.updateBreadcrumb(to);
        // 确保每次路由变化时更新菜单激活状态和首页显示逻辑
        this.setActiveMenuItem();
      }
    }
  },
  created() {
    this.loadUserInfo();
    // 移除在created中加载菜单，改为在mounted中加载以兼容延迟加载机制
  },
  mounted() {
    // 在组件挂载完成后获取菜单数据，以适配Dashboard的延迟加载机制
    // 在组件创建时加载菜单数据
    this.loadMenus();
  },
  methods: {
    // 切换子菜单显示
    toggleSubMenu(menu) {
      // 检查是否为首页菜单
      if (menu.path === '/dashboard' || !this.hasChildren(menu)) {
        this.navigateTo(menu);
        return;
      }
      
      // 切换子菜单显示状态
      this.showSubMenu = this.showSubMenu === menu.id ? null : menu.id;
    },
    
    // 鼠标离开菜单项时关闭子菜单
    closeSubMenuOnLeave(menu) {
      // 使用定时器避免快速移动导致的频繁切换
      setTimeout(() => {
        // 只有当当前显示的子菜单是这个菜单项的子菜单时才关闭
        if (this.showSubMenu === menu.id) {
          this.showSubMenu = null;
        }
      }, 300);
    },
    
    // 检查菜单是否有子菜单
    hasChildren(menu) {
      return menu.children && menu.children.length > 0;
    },
    
    // 导航到指定页面
    navigateTo(menu) {
      // 隐藏所有子菜单
      this.showSubMenu = null;
      
      // 设置当前选中的菜单
      this.currentMenu = menu.path;
      
      // 标记是否为首页
      this.isHomePage = menu.path === '/dashboard' || menu.path === '/';
      
      // 执行路由导航
      if (menu.path) {
        this.$router.push(menu.path);
        
        // 处理首页显示逻辑
        if (menu.path === '/change-records' || menu.path === '/version-management' || menu.path === '/development-standards') {
          this.isHomePage = false;
        }
      }
    },
    async loadMenus() {
      // 添加加载状态管理，避免重复加载
      if (this.menuLoading) return;
      
      this.menuLoading = true;
      try {
        const response = await getUserMenus();
        // 适配API响应格式
        if (response && (response.code === 200 || response.data)) {
          // 确保使用正确的变量名
          this.topMenuList = response.data || [];
          console.log('成功加载菜单数据:', this.topMenuList);
          // 根据当前路由设置激活菜单项
          this.setActiveMenuItem();
        }
      } catch (e) {
        console.error('加载菜单失败:', e);
        // 确保即使在错误情况下也设置激活菜单项
        this.setActiveMenuItem();
      } finally {
        this.menuLoading = false;
      }
    },
    // 设置激活的菜单项，适配延迟加载场景
    setActiveMenuItem() {
      const currentPath = this.$route.path;
      this.currentMenu = currentPath;
      
      // 检查是否为首页路径
      this.isHomePage = currentPath === '/dashboard' || currentPath === '/';
      
      // 对于特定功能页面，确保不显示首页内容
      if (currentPath === '/change-records' || currentPath === '/version-management' || 
          currentPath === '/development-standards' || currentPath.startsWith('/change-records/')) {
        this.isHomePage = false;
      }
      
      // 检查是否需要高亮父菜单
      for (const menu of this.topMenuList) {
        if (this.hasChildren(menu)) {
          const hasActiveChild = menu.children.some(child => {
            // 检查精确匹配或路径前缀匹配
            return child.path === currentPath || currentPath.startsWith(child.path + '/');
          });
          if (hasActiveChild) {
            this.currentMenu = menu.path || `parent-${menu.id}`;
            break;
          }
        }
      }
    },
    loadUserInfo() {
      try {
        const userStr = localStorage.getItem('user');
        if (userStr) {
          this.currentUser = JSON.parse(userStr);
        }
      } catch (e) {
        console.error('加载用户信息失败:', e);
      }
    },
    updateBreadcrumb(route) {
      const matched = route.matched.filter(item => item.meta && item.meta.title);
      this.breadcrumbList = matched.map(item => ({
        path: item.path,
        title: item.meta.title
      }));
    },
    handleCommand(command) {
      if (command === 'logout') {
        this.logout();
      } else if (command === 'changePassword') {
        this.$router.push('/change-password');
      }
    },
    logout() {
      localStorage.removeItem('token');
      localStorage.removeItem('csrfToken');
      localStorage.removeItem('user');
      this.$router.replace('/login');
    }
  }
};
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #F3F4F6;
}

/* 顶部导航栏样式 */
.top-navbar {
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
  height: 64px;
}

.navbar-container {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 24px;
  justify-content: space-between;
}

/* 系统标题 */
.logo-container {
  flex-shrink: 0;
}

.system-title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #7B68EE;
}

/* 顶部菜单容器 */
.top-menu-container {
  flex: 1;
  margin: 0 40px;
  position: relative;
}

.top-menu-wrapper {
    display: flex;
    align-items: center;
    height: 100%;
    overflow-x: auto;
    /* 隐藏滚动条但保留滚动功能 */
    scrollbar-width: none; /* Firefox */
    -ms-overflow-style: none; /* IE and Edge */
  }
  
  .top-menu-wrapper::-webkit-scrollbar {
    display: none; /* Chrome, Safari, Opera */
  }
  
  /* 顶部菜单样式 */
  .top-menu {
    display: flex;
    list-style: none;
    margin: 0;
    padding: 0;
    flex-wrap: wrap;
  }
  
  .top-menu-item {
    position: relative;
    margin-right: 8px;
    cursor: pointer;
    border-radius: 4px;
    transition: background-color 0.3s ease;
  }
  
  .top-menu-item:hover {
    background-color: rgba(123, 104, 238, 0.05);
  }
  
  .top-menu-item.active {
    background-color: rgba(123, 104, 238, 0.1);
  }
  
  .top-menu-item > span {
    padding: 18px 24px;
    font-size: 15px;
    color: #303133;
    display: inline-flex;
    align-items: center;
    min-height: 64px;
    box-sizing: border-box;
  }
  
  .top-menu-item:hover > span {
    color: #7B68EE;
  }
  
  .top-menu-item.active > span {
    color: #7B68EE;
    font-weight: 600;
  }
  
  /* 箭头样式 */
  .submenu-arrow {
    margin-left: 6px;
    font-size: 12px;
    vertical-align: middle;
    transition: transform 0.3s ease;
    color: #909399;
  }
  
  .submenu-arrow.rotate {
    transform: rotate(180deg);
  }
  
  .top-menu-item:hover .submenu-arrow {
    color: #7B68EE;
  }
  
  /* 子菜单样式 */
  .submenu {
    position: absolute;
    top: 100%;
    left: 0;
    background-color: white;
    border-radius: 6px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
    padding: 8px 0;
    min-width: 220px;
    z-index: 1001;
    opacity: 0;
    visibility: hidden;
    transform: translateY(-10px);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    overflow: hidden;
  }
  
  .submenu.show {
    opacity: 1;
    visibility: visible;
    transform: translateY(0);
  }
  
  .submenu-list {
    list-style: none;
    margin: 0;
    padding: 0;
  }
  
  .submenu-item {
    padding: 12px 24px;
    font-size: 14px;
    color: #666;
    transition: all 0.3s ease;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  
  .submenu-item:hover {
    background-color: rgba(123, 104, 238, 0.1);
    color: #7B68EE;
    padding-left: 28px;
  }
  
  .submenu-item.active {
    background-color: rgba(123, 104, 238, 0.1);
    color: #7B68EE;
    font-weight: 500;
    padding-left: 28px;
  }
  
  /* 为激活的子菜单项添加左侧指示线 */
  .submenu-item.active::before {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 3px;
    background-color: #7B68EE;
    transition: all 0.3s ease;
  }

/* 用户信息区域 */
.user-info-container {
  flex-shrink: 0;
}

.user-info {
  cursor: pointer;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.user-info:hover {
  color: #7B68EE;
  background: linear-gradient(90deg, 
    rgba(255, 182, 193, 0.15) 0%, 
    rgba(221, 160, 221, 0.15) 100%);
}



/* 主内容区域样式 */
.main-content {
  flex: 1;
  padding: 20px;
  background-color: #F3F4F6;
  overflow: auto;
  min-height: calc(100vh - 64px);
}

/* 主内容包装器 */
.main-content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  padding: 0 16px;
}

/* 响应式样式 */
@media screen and (max-width: 768px) {
  /* 小屏幕下主内容区宽度100% */
  .main-content {
    width: 100% !important;
  }
  
  /* 小屏幕下侧边栏可折叠或隐藏 */
  .main-sidebar {
    position: fixed;
    height: 100vh;
    z-index: 1000;
    left: 0;
    top: 0;
  }
}

/* 过渡效果，增强用户体验 */
.el-container {
  transition: all 0.3s ease;
}

/* 导航到Dashboard的链接样式 */
.to-dashboard {
  color: #3B82F6;
  cursor: pointer;
  text-decoration: none;
  transition: color 0.3s ease;
}

.to-dashboard:hover {
  color: #1D4ED8;
  text-decoration: underline;
}

/* 页面布局相关样式 */
.main-content-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
}

/* 页面头部区域 */
.page-header-section {
  background-color: white;
  padding: 16px 0;
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.breadcrumb {
  margin-bottom: 12px;
}

.page-title h2 {
  margin: 0;
  color: #1F2937;
  font-size: 24px;
  font-weight: 600;
}

/* 子页面容器样式 */
.child-page-container {
  flex: 1;
  min-height: 0;
  background-color: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

/* 页面过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 主内容区域布局 */
.el-main {
  padding: 20px;
  height: calc(100vh - 64px);
  overflow-y: auto;
}

/* 确保内容容器样式 */
.child-page-container {
  background-color: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  min-height: 400px;
}

/* 页面过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar-container {
    padding: 0 16px;
  }
  
  .system-title {
    font-size: 16px;
  }
  
  .top-menu-container {
    margin: 0 20px;
  }
  
  .top-menu-item > span {
    padding: 10px 16px;
    font-size: 13px;
  }
  
  .submenu {
    min-width: 180px;
  }
  
  .submenu-item {
    padding: 8px 16px;
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .navbar-container {
    padding: 0 12px;
  }
  
  .logo-container {
    display: none;
  }
  
  .top-menu-container {
    margin: 0 10px;
    flex: 1;
  }
  
  .top-menu-item > span {
    padding: 8px 12px;
    font-size: 12px;
  }
}
</style>

