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
        // 优先从本地存储获取菜单数据
        const cachedMenus = localStorage.getItem('menuPermissions');
        // 临时处理：如果本地存储中有数据，直接跳过，因为权限验证在路由守卫中
        if (!cachedMenus) {
          try {
            const menus = JSON.parse(cachedMenus);
            // 适配API响应格式
            this.topMenuList = menus || [];
            return;
          } catch (parseError) {
            console.error('解析本地存储菜单数据失败:', parseError);
          }
        }

        // 如果本地存储中没有数据，调用API获取
        const response = await getUserMenus();

        if (response) {
          // 适配API响应格式
          this.topMenuList = response || [];
          // 将菜单数据存储到本地存储
          // localStorage.setItem('menuPermissions', JSON.stringify(response));
          // 给一个无需权限菜单列表，用于展示所有的菜单
          localStorage.setItem('menuNoPermissions', JSON.stringify(response));
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
.main-layout {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--gradient-bg);
}

.top-navbar {
  height: 64px;
  background: var(--bg-card);
  box-shadow: var(--shadow-md);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  animation: fadeInDown var(--transition-base) ease-out;
}

.navbar-content {
  width: 100%;
  max-width: 100%;
  margin: 0 auto;
  padding: 0 var(--spacing-lg);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.system-icon {
  font-size: 28px;
  color: var(--color-primary);
  animation: pulse 3s ease-in-out infinite;
}

.brand-name {
  font-size: var(--text-xl);
  font-weight: 700;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.main-nav {
  flex: 1;
  margin: 0 var(--spacing-2xl);
}

.nav-list {
  display: flex;
  list-style: none;
  gap: var(--spacing-sm);
}

.nav-item {
  position: relative;
  height: 64px;
  display: flex;
  align-items: center;
  cursor: pointer;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: 0 var(--spacing-md);
  height: 100%;
  color: var(--text-secondary);
  text-decoration: none;
  font-size: var(--text-sm);
  font-weight: 500;
  transition: all var(--transition-base);
  border-radius: var(--radius-md);
  position: relative;
  overflow: hidden;
}

.nav-link::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 3px;
  background: var(--gradient-primary);
  transition: all var(--transition-base);
  transform: translateX(-50%);
}

.nav-link:hover {
  color: var(--color-primary);
  background: var(--bg-hover);
}

.nav-link:hover::before {
  width: 100%;
}

.nav-item.active .nav-link {
  color: var(--color-primary);
  background: var(--bg-active);
}

.nav-item.active .nav-link::before {
  width: 100%;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  background: var(--bg-card);
  box-shadow: var(--shadow-xl);
  border-radius: var(--radius-lg);
  padding: var(--spacing-sm) 0;
  min-width: 200px;
  opacity: 0;
  visibility: hidden;
  transform: translateY(-8px) scale(0.98);
  transition: all var(--transition-spring);
  z-index: 1001;
  border: 1px solid var(--border-light);
}

.dropdown-menu.show {
  opacity: 1;
  visibility: visible;
  transform: translateY(8px) scale(1);
}

.dropdown-list {
  list-style: none;
}

.dropdown-item {
  padding: 0;
  position: relative;
}

.dropdown-link {
  display: block;
  padding: var(--spacing-sm) var(--spacing-md);
  color: var(--text-secondary);
  text-decoration: none;
  font-size: var(--text-sm);
  font-weight: 500;
  transition: all var(--transition-fast);
  white-space: nowrap;
  position: relative;
  overflow: hidden;
}

.dropdown-link::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 3px;
  background: var(--gradient-primary);
  transform: scaleY(0);
  transition: transform var(--transition-base);
}

.dropdown-link:hover {
  background: var(--bg-hover);
  color: var(--color-primary);
  padding-left: calc(var(--spacing-md) + 4px);
}

.dropdown-link:hover::before {
  transform: scaleY(1);
}

.dropdown-item.active .dropdown-link {
  background: var(--bg-active);
  color: var(--color-primary);
}

.dropdown-item.active .dropdown-link::before {
  transform: scaleY(1);
}

.user-section {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
}

.date-display {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  color: var(--text-secondary);
  font-size: var(--text-sm);
  font-weight: 500;
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--bg-hover);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
  transition: all var(--transition-base);
}

.date-display:hover {
  background: var(--bg-active);
  border-color: var(--color-primary-light);
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

.date-display i {
  color: var(--color-primary);
  font-size: 18px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-base);
  color: var(--text-secondary);
  font-size: var(--text-sm);
  font-weight: 500;
  background: var(--bg-hover);
  border: 1px solid var(--border-light);
}

.user-info:hover {
  background: var(--bg-active);
  color: var(--color-primary);
  border-color: var(--color-primary-light);
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

.main-content {
  display: flex;
  margin-top: 64px;
  height: calc(100vh - 64px);
}

.content-area {
  flex: 1;
  padding: var(--spacing-md);
  overflow-y: auto;
  height: calc(100vh - 64px);
}

.dropdown-submenu {
  position: absolute;
  top: 0;
  left: 100%;
  background: var(--bg-card);
  box-shadow: var(--shadow-xl);
  border-radius: var(--radius-lg);
  padding: var(--spacing-sm) 0;
  min-width: 200px;
  opacity: 0;
  visibility: hidden;
  transform: translateX(-8px) scale(0.98);
  transition: all var(--transition-spring);
  z-index: 1002;
  border: 1px solid var(--border-light);
  margin-left: var(--spacing-sm);
}

.dropdown-item:hover .dropdown-submenu {
  opacity: 1;
  visibility: visible;
  transform: translateX(0) scale(1);
}

.dropdown-submenu-list {
  list-style: none;
}

.dropdown-submenu-item {
  padding: 0;
}

.dropdown-submenu-link {
  display: block;
  padding: var(--spacing-sm) var(--spacing-md);
  color: var(--text-secondary);
  text-decoration: none;
  font-size: var(--text-sm);
  font-weight: 500;
  transition: all var(--transition-fast);
  white-space: nowrap;
  position: relative;
  overflow: hidden;
}

.dropdown-submenu-link::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 3px;
  background: var(--gradient-primary);
  transform: scaleY(0);
  transition: transform var(--transition-base);
}

.dropdown-submenu-link:hover {
  background: var(--bg-hover);
  color: var(--color-primary);
  padding-left: calc(var(--spacing-md) + 4px);
}

.dropdown-submenu-link:hover::before {
  transform: scaleY(1);
}

.dropdown-submenu-item.active .dropdown-submenu-link {
  background: var(--bg-active);
  color: var(--color-primary);
}

.dropdown-submenu-item.active .dropdown-submenu-link::before {
  transform: scaleY(1);
}

.page-content {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  padding: var(--spacing-md);
  min-height: calc(100% - 32px);
  border: 1px solid var(--border-light);
  animation: fadeInUp var(--transition-base) ease-out;
}

.fade-enter-active,
.fade-leave-active {
  transition: all var(--transition-base);
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

@media (max-width: 768px) {
  .navbar-content {
    padding: 0 var(--spacing-md);
  }

  .brand-name {
    display: none;
  }

  .main-nav {
    margin: 0 var(--spacing-lg);
  }

  .nav-link span {
    display: none;
  }

  .user-section {
    gap: var(--spacing-md);
  }

  .date-display {
    display: none;
  }

  .content-area {
    padding: var(--spacing-md);
  }

  .page-content {
    padding: var(--spacing-md);
  }

  .dropdown-submenu {
    display: none;
  }
}

@media (max-width: 480px) {
  .navbar-content {
    padding: 0 var(--spacing-sm);
  }

  .logo-section {
    gap: var(--spacing-sm);
  }

  .system-icon {
    font-size: 24px;
  }

  .main-nav {
    margin: 0 var(--spacing-md);
  }

  .nav-link {
    padding: 0 var(--spacing-sm);
  }

  .user-section {
    gap: var(--spacing-sm);
  }

  .user-info {
    padding: var(--spacing-xs) var(--spacing-sm);
    font-size: var(--text-xs);
  }

  .content-area {
    padding: var(--spacing-sm);
  }

  .page-content {
    padding: var(--spacing-sm);
  }
}
</style>