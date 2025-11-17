<template>
  <el-container class="main-layout">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
      <div class="logo">
        <span v-if="!isCollapse" class="logo-text">架构管理系统</span>
        <span v-else class="logo-icon">架构</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :unique-opened="true"
        router
        class="sidebar-menu"
        background-color="transparent"
        text-color="#666"
        active-text-color="#7B68EE"
      >
        <menu-item
          v-for="menu in menuList"
          :key="menu.id"
          :menu="menu"
        />
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-button
            type="text"
            icon="el-icon-s-fold"
            @click="toggleCollapse"
            class="collapse-btn"
          />
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item
              v-for="item in breadcrumbList"
              :key="item.path"
              :to="item.path"
            >
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
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
      </el-header>

      <!-- 内容区域 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { getUserMenus } from '../api';
import MenuItem from './MenuItem.vue';

export default {
  name: 'MainLayout',
  components: {
    MenuItem
  },
  data() {
    return {
      isCollapse: false,
      menuList: [],
      currentUser: {},
      activeMenu: '',
      breadcrumbList: []
    };
  },
  watch: {
    $route: {
      immediate: true,
      handler(to) {
        this.activeMenu = to.path;
        this.updateBreadcrumb(to);
      }
    }
  },
  created() {
    this.loadUserInfo();
    this.loadMenus();
  },
  methods: {
    toggleCollapse() {
      this.isCollapse = !this.isCollapse;
    },
    async loadMenus() {
      try {
        const response = await getUserMenus();
        if (response.code === 200) {
          this.menuList = response.data || [];
        }
      } catch (e) {
        console.error('加载菜单失败:', e);
        this.$message.error('加载菜单失败');
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
  height: 100vh;
}

.sidebar {
  background: linear-gradient(180deg, 
    rgba(255, 182, 193, 0.15) 0%, 
    rgba(255, 218, 185, 0.12) 20%,
    rgba(255, 250, 205, 0.1) 40%,
    rgba(173, 216, 230, 0.12) 60%,
    rgba(221, 160, 221, 0.15) 80%,
    rgba(255, 182, 193, 0.12) 100%);
  transition: width 0.3s;
  overflow: hidden;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.04);
  margin: 0;
  padding: 0;
  border: none;
}

.logo {
  height: 59px;
  line-height: 59px;
  text-align: center;
  background: linear-gradient(135deg, 
    rgba(255, 182, 193, 0.3) 0%, 
    rgba(255, 218, 185, 0.25) 25%,
    rgba(173, 216, 230, 0.3) 50%,
    rgba(221, 160, 221, 0.25) 75%,
    rgba(255, 182, 193, 0.3) 100%);
  color: #5a5a5a;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.5px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
}

.logo-text {
  display: inline-block;
}

.logo-icon {
  display: inline-block;
  font-size: 16px;
}

.sidebar-menu {
  border: none;
  height: calc(100vh - 64px);
  overflow-y: auto;
  background: transparent;
  padding: 8px 0;
}

.sidebar-menu ::v-deep .el-menu-item {
  border-radius: 8px;
  margin: 4px 12px;
  transition: all 0.3s ease;
  color: #666;
}

.sidebar-menu ::v-deep .el-menu-item:hover {
  background: linear-gradient(90deg, 
    rgba(255, 182, 193, 0.2) 0%, 
    rgba(221, 160, 221, 0.2) 100%) !important;
  color: #7B68EE !important;
}

.sidebar-menu ::v-deep .el-menu-item.is-active {
  background: linear-gradient(90deg, 
    rgba(173, 216, 230, 0.3) 0%, 
    rgba(221, 160, 221, 0.3) 100%) !important;
  color: #7B68EE !important;
  font-weight: 500;
  border-left: 3px solid #7B68EE;
}

.sidebar-menu ::v-deep .el-submenu__title {
  border-radius: 8px;
  margin: 4px 12px;
  transition: all 0.2s ease;
  color: #666;
}

.sidebar-menu ::v-deep .el-submenu__title:hover {
  background: linear-gradient(90deg, 
    rgba(255, 182, 193, 0.2) 0%, 
    rgba(221, 160, 221, 0.2) 100%) !important;
  color: #7B68EE !important;
}

.sidebar-menu ::v-deep .el-submenu.is-opened > .el-submenu__title {
  color: #7B68EE;
}

.header {
  background: linear-gradient(135deg,
  rgba(255, 182, 193, 0.3) 0%,
  rgba(255, 218, 185, 0.25) 25%,
  rgba(173, 216, 230, 0.3) 50%,
  rgba(221, 160, 221, 0.25) 75%,
  rgba(255, 182, 193, 0.3) 100%);
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  height: 64px;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  font-size: 20px;
  margin-right: 20px;
  color: #666;
  transition: all 0.2s ease;
}

.collapse-btn:hover {
  color: #7B68EE;
}

.breadcrumb {
  line-height: 40px;
}

.header-right {
  display: flex;
  align-items: center;
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

.main-content {
  background: linear-gradient(180deg, 
    rgba(255, 182, 193, 0.15) 0%, 
    rgba(255, 218, 185, 0.12) 20%,
    rgba(255, 250, 205, 0.1) 40%,
    rgba(173, 216, 230, 0.12) 60%,
    rgba(221, 160, 221, 0.15) 80%,
    rgba(255, 182, 193, 0.12) 100%);
  padding: 12px;
  overflow-y: auto;
  min-height: calc(100vh - 60px);
  position: relative;
}

.main-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  z-index: 0;
  pointer-events: none;
}

.main-content > * {
  position: relative;
  z-index: 1;
}
</style>

