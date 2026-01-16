<template>
  <div class="main-layout">
    <!-- 顶部导航栏 -->
    <header class="top-navbar">
      <div class="navbar-content">
        <!-- Logo区域 -->
        <div class="logo-section">
          <i class="el-icon-connection system-icon"></i>
          <span class="brand-name">架构管理工作</span>
        </div>

        <!-- 导航菜单 -->
        <nav class="main-nav">
          <ul class="nav-list">
            <li v-for="menu in topMenuList" :key="menu.menuCode" class="nav-item"
              :class="{ 'active': isNavItemActive(menu) }" @click="handleNavClick(menu)"
              @mouseenter="handleNavMouseEnter(menu)" @mouseleave="handleNavMouseLeave(menu)">
              <a href="#" class="nav-link">
                <i :class="menu.icon || 'el-icon-menu'"></i>
                <span>{{ menu.menuName }}</span>
              </a>

              <!-- 下拉菜单 - 包含所有功能选项 -->
              <div v-if="hasChildren(menu)" class="dropdown-menu" :class="{ 'show': activeDropdown === menu.menuCode }">
                <ul class="dropdown-list">
                  <li v-for="child in menu.children" :key="child.menuCode" class="dropdown-item"
                    :class="{ 'active': isNavItemActive(child) }" @click.stop="handleNavClick(child)">
                    <a href="#" class="dropdown-link">
                      {{ child.menuName }}
                    </a>

                    <!-- 三级菜单（如果有） -->
                    <div v-if="hasChildren(child)" class="dropdown-submenu">
                      <ul class="dropdown-submenu-list">
                        <li v-for="grandchild in child.children" :key="grandchild.menuCode"
                          class="dropdown-submenu-item" :class="{ 'active': isNavItemActive(grandchild) }"
                          @click.stop="handleNavClick(grandchild)">
                          <a href="#" class="dropdown-submenu-link">
                            {{ grandchild.menuName }}
                          </a>
                        </li>
                      </ul>
                    </div>
                  </li>
                </ul>
              </div>
            </li>
          </ul>
        </nav>

        <!-- 用户信息区域 -->
        <div class="user-section">
          <!-- 当前日期显示 -->
          <div class="date-display">
            <i class="el-icon-date"></i>
            <span>{{ currentDate }}</span>
          </div>

          <!-- 用户下拉菜单 -->
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <i class="el-icon-user"></i>
              <span>{{ currentUser.userName || '用户' }}</span>
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
    </header>

    <!-- 主体内容区域 -->
    <div class="main-content">
      <!-- 主内容区 - 占满整个宽度 -->
      <div class="content-area">
        <!-- 页面内容 -->
        <div class="page-content">
          <transition name="fade" mode="out-in">
            <router-view v-slot="{ Component }">
              <component :is="Component" />
            </router-view>
          </transition>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// 导入API方法
import { getUserMenus } from '../api';

export default {
  name: 'MainLayout',
  data() {
    return {
      // 用户信息
      currentUser: {},
      // 面包屑数据
      breadcrumbList: [],
      // 菜单数据
      topMenuList: [],
      // 当前日期
      currentDate: '',
      // 日期更新定时器
      dateTimer: null,
      // 激活的下拉菜单
      activeDropdown: null
    };
  },
  computed: {
    // 计算当前路由路径
    currentRoutePath() {
      return this.$route.path;
    }
  },
  watch: {
    // 监听路由变化
    $route: {
      immediate: true,
      handler(to) {
        // 更新面包屑
        this.updateBreadcrumb(to);
      }
    }
  },
  created() {
    // 加载用户信息
    this.loadUserInfo();
    // 初始化并更新当前日期
    this.updateCurrentDate();
    // 设置定时器，每分钟更新一次日期
    this.dateTimer = setInterval(() => {
      this.updateCurrentDate();
    }, 60000);
  },
  mounted() {
    // 加载菜单数据
    this.loadMenus();
  },
  beforeDestroy() {
    // 清除日期更新定时器
    if (this.dateTimer) {
      clearInterval(this.dateTimer);
    }
  },
  methods: {
    // 更新当前日期
    updateCurrentDate() {
      const now = new Date();
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      const day = String(now.getDate()).padStart(2, '0');
      const hours = String(now.getHours()).padStart(2, '0');
      const minutes = String(now.getMinutes()).padStart(2, '0');
      this.currentDate = `${year}-${month}-${day} ${hours}:${minutes}`;
    },

    // 检查菜单是否有子菜单
    hasChildren(menu) {
      return menu && menu.children && Array.isArray(menu.children) && menu.children.length > 0;
    },

    // 判断导航项是否激活
    isNavItemActive(menu) {
      if (!menu.path) return false;
      const currentPath = this.currentRoutePath;

      // 精确匹配
      if (currentPath === menu.path) {
        return true;
      }

      // 前缀匹配（用于嵌套路由）
      if (currentPath.startsWith(menu.path) && menu.path !== '/') {
        return currentPath === menu.path || currentPath.startsWith(menu.path + '/');
      }

      return false;
    },

    // 处理导航点击
    handleNavClick(menu) {
      // 如果是目录类型，不支持点击
      if (menu.menuType === "M") {
        return;
      }
      
      if (menu.path) {
        this.$router.push(menu.path).catch(err => {
          // 忽略重复导航的错误
          if (err.name !== 'NavigationDuplicated') {
            console.error('导航错误:', err);
          }
        });
      }

      // 切换下拉菜单显示状态
      this.activeDropdown = this.activeDropdown === menu.menuCode ? null : menu.menuCode;
    },

    // 处理导航鼠标进入事件
    handleNavMouseEnter(menu) {
      if (this.hasChildren(menu)) {
        this.activeDropdown = menu.menuCode;
      }
    },

    // 处理导航鼠标离开事件
    handleNavMouseLeave(menu) {
      // 延迟关闭下拉菜单，提升用户体验
      setTimeout(() => {
        // 只有当当前激活的还是该菜单时才关闭
        if (this.activeDropdown === menu.menuCode) {
          this.activeDropdown = null;
        }
      }, 200);
    },

    // 加载菜单数据
    async loadMenus() {
      try {
        const response = await getUserMenus();

        if (response) {
          // 适配API响应格式
          this.topMenuList = response || [];
        } else {
          console.error('加载菜单失败: API返回空响应');
          this.topMenuList = [];
        }
      } catch (e) {
        console.error('加载菜单失败:', e);
        this.topMenuList = [];
      }
    },

    // 加载用户信息
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

    // 更新面包屑
    updateBreadcrumb(route) {
      const matched = route.matched.filter(item => item.meta && item.meta.title);
      this.breadcrumbList = matched.map(item => ({
        path: item.path,
        title: item.meta.title
      }));
    },

    // 处理用户下拉菜单命令
    handleCommand(command) {
      if (command === 'logout') {
        this.logout();
      } else if (command === 'changePassword') {
        this.$router.push('/change-password');
      }
    },

    // 退出登录
    logout() {
      // 清除本地存储的用户信息
      localStorage.removeItem('loginInfo');
      localStorage.removeItem('user');
      localStorage.removeItem('token');
      localStorage.removeItem('session');
      localStorage.removeItem('csrfToken');    
      localStorage.removeItem('currentOrg');
      localStorage.removeItem('currentDept');
      localStorage.removeItem('permissions');
      localStorage.removeItem('authorization');
      localStorage.removeItem('availableOrgs');
      localStorage.removeItem('availableDepts');

      // 跳转到登录页
      this.$router.replace('/login');
    }
  }
};
</script>

<style scoped>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 主布局容器 */
.main-layout {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #F5F7FA;
}

/* 顶部导航栏 */
.top-navbar {
  height: 60px;
  background-color: #FFFFFF;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
}

/* 导航栏内容 */
.navbar-content {
  width: 100%;
  max-width: 100%;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* Logo区域 */
.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.system-icon {
  font-size: 24px;
  color: #7B68EE;
}

.brand-name {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
}

/* 主导航菜单 */
.main-nav {
  flex: 1;
  margin: 0 40px;
}

.nav-list {
  display: flex;
  list-style: none;
  gap: 8px;
}

.nav-item {
  position: relative;
  height: 60px;
  display: flex;
  align-items: center;
  cursor: pointer;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 16px;
  height: 100%;
  color: #303133;
  text-decoration: none;
  font-size: 15px;
  transition: all 0.3s ease;
  border-radius: 4px;
}

.nav-link:hover {
  background-color: rgba(123, 104, 238, 0.05);
  color: #7B68EE;
}

.nav-item.active .nav-link {
  background-color: rgba(123, 104, 238, 0.1);
  color: #7B68EE;
  font-weight: 500;
}

/* 下拉菜单 */
.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  background-color: #FFFFFF;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  border-radius: 6px;
  padding: 8px 0;
  min-width: 180px;
  opacity: 0;
  visibility: hidden;
  transform: translateY(-10px);
  transition: all 0.3s ease;
  z-index: 1001;
}

.dropdown-menu.show {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.dropdown-list {
  list-style: none;
}

.dropdown-item {
  padding: 0;
}

.dropdown-link {
  display: block;
  padding: 10px 16px;
  color: #606266;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.dropdown-link:hover {
  background-color: rgba(123, 104, 238, 0.05);
  color: #7B68EE;
  padding-left: 20px;
}

.dropdown-item.active .dropdown-link {
  background-color: rgba(123, 104, 238, 0.1);
  color: #7B68EE;
  font-weight: 500;
}

/* 用户信息区域 */
.user-section {
  display: flex;
  align-items: center;
  gap: 24px;
}

/* 日期显示样式 */
.date-display {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
  font-size: 14px;
  padding: 8px 12px;
  background-color: rgba(123, 104, 238, 0.05);
  border-radius: 6px;
  border: 1px solid rgba(123, 104, 238, 0.1);
}

.date-display i {
  color: #7B68EE;
  font-size: 16px;
}

/* 用户信息样式 */
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #606266;
  font-size: 14px;
}

.user-info:hover {
  background-color: rgba(123, 104, 238, 0.05);
  color: #7B68EE;
}

/* 主体内容区域 */
.main-content {
  display: flex;
  margin-top: 60px;
  height: calc(100vh - 20px);
}

/* 主内容区 - 占满整个宽度 */
.content-area {
  flex: 1;
  padding: 2px;
  overflow-y: auto;
  height: calc(100vh - 20px);
}

/* 三级菜单样式 */
.dropdown-submenu {
  position: absolute;
  top: 0;
  left: 100%;
  background-color: #FFFFFF;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  border-radius: 6px;
  padding: 8px 0;
  min-width: 180px;
  opacity: 0;
  visibility: hidden;
  transform: translateY(-10px);
  transition: all 0.3s ease;
  z-index: 1002;
}

/* 当鼠标悬停在父菜单项上时显示三级菜单 */
.dropdown-item:hover .dropdown-submenu {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.dropdown-submenu-list {
  list-style: none;
}

.dropdown-submenu-item {
  padding: 0;
}

.dropdown-submenu-link {
  display: block;
  padding: 10px 16px;
  color: #606266;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.dropdown-submenu-link:hover {
  background-color: rgba(123, 104, 238, 0.05);
  color: #7B68EE;
  padding-left: 20px;
}

.dropdown-submenu-item.active .dropdown-submenu-link {
  background-color: rgba(123, 104, 238, 0.1);
  color: #7B68EE;
  font-weight: 500;
}

/* 页面内容 */
.page-content {
  background-color: #FFFFFF;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  padding: 2px;
  min-height: calc(100% - 60px);
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

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #F5F7FA;
}

::-webkit-scrollbar-thumb {
  background: #C0C4CC;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar-content {
    padding: 0 16px;
  }

  .brand-name {
    display: none;
  }

  .main-nav {
    margin: 0 20px;
  }

  .nav-link span {
    display: none;
  }

  .user-section {
    gap: 16px;
  }

  .date-display {
    display: none;
  }

  .content-area {
    padding: 16px;
  }

  .page-content {
    padding: 16px;
  }

  /* 移动端隐藏三级菜单 */
  .dropdown-submenu {
    display: none;
  }
}

@media (max-width: 480px) {
  .navbar-content {
    padding: 0 12px;
  }

  .logo-section {
    gap: 8px;
  }

  .system-icon {
    font-size: 20px;
  }

  .main-nav {
    margin: 0 10px;
  }

  .nav-link {
    padding: 0 10px;
  }

  .user-section {
    gap: 12px;
  }

  .user-info {
    padding: 6px 10px;
    font-size: 13px;
  }

  .content-area {
    padding: 12px;
  }

  .page-content {
    padding: 12px;
  }
}
</style>