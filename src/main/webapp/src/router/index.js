import Vue from 'vue';
import Router from 'vue-router';
import Login from '../views/Login.vue';
import MainLayout from '../components/MainLayout.vue';
import DashboardCards from '../components/DashboardCards.vue';
import ChangePassword from '../views/ChangePassword.vue';
import ChangeRecordList from '../views/ChangeRecordList.vue';
import SystemSettings from '../views/SystemSettings.vue';
import ChangeRecordDetail from '../views/ChangeRecordDetail.vue';
import ChangeRecordHistory from '../views/ChangeRecordHistory.vue';
import VersionManagement from '../views/VersionManagement.vue';
import DevelopmentStandards from '../views/DevelopmentStandards.vue';

// 忽略重复导航错误（Vue Router 3 在重复 push/replace 时会抛 NavigationDuplicated）
const originalPush = Router.prototype.push;
Router.prototype.push = function push(location, onResolve, onReject) {
  if (onResolve || onReject) return originalPush.call(this, location, onResolve, onReject);
  return originalPush.call(this, location).catch(err => {
    if (err && (err.name === 'NavigationDuplicated' || (err.message && err.message.includes('Avoided redundant navigation')))) return err;
    throw err;
  });
};
const originalReplace = Router.prototype.replace;
Router.prototype.replace = function replace(location, onResolve, onReject) {
  if (onResolve || onReject) return originalReplace.call(this, location, onResolve, onReject);
  return originalReplace.call(this, location).catch(err => {
    if (err && (err.name === 'NavigationDuplicated' || (err.message && err.message.includes('Avoided redundant navigation')))) return err;
    throw err;
  });
};

Vue.use(Router);

// 动态路由映射表
const componentMap = {
  'views/ChangeRecordList': ChangeRecordList,
  'views/ChangeRecordDetail': ChangeRecordDetail,
  'views/ChangeRecordHistory': ChangeRecordHistory,
  'views/ChangePassword': ChangePassword,
  'views/VersionManagement': VersionManagement,
  'views/DevelopmentStandards': DevelopmentStandards,
  'views/SystemSettings': SystemSettings
};

// 将菜单转换为路由
function menuToRoute(menu) {
  if (menu.menuType !== 2 || !menu.path) {
    return null;
  }

  const route = {
    path: menu.path,
    name: menu.menuCode,
    component: componentMap[menu.component] || null,
    meta: {
      title: menu.menuName,
      icon: menu.icon,
      perms: menu.perms
    }
  };

  // 处理动态路由参数（如 /change-records/:id）
  if (menu.path.includes(':')) {
    route.path = menu.path;
  }

  return route;
}

// 递归处理菜单树，生成路由
function generateRoutes(menus) {
  const routes = [];
  for (const menu of menus) {
    const route = menuToRoute(menu);
    if (route && route.component) {
      routes.push(route);
    }
    if (menu.children && menu.children.length > 0) {
      routes.push(...generateRoutes(menu.children));
    }
  }
  return routes;
}

const router = new Router({
  mode: 'hash',
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: Login,
      meta: { title: '登录' }
    },
    {
      path: '/',
      name: 'MainLayout',
      component: MainLayout,
      meta: { title: '首页' },
      children: [
        {
          path: '/dashboard',
          name: 'Dashboard',
          component: DashboardCards,
          meta: { title: '首页' }
        },
        {
          path: '/version-management',
          name: 'VersionManagement',
          component: VersionManagement,
          meta: { title: '版本管理方案' }
        },
        {
          path: '/development-standards',
          name: 'DevelopmentStandards',
          component: DevelopmentStandards,
          meta: { title: '日常开发规范' }
        },
        {
          path: '/system-settings',
          name: 'SystemSettings',
          component: SystemSettings,
          meta: { title: '系统设置' }
        },
        {
          path: '/change-password',
          name: 'ChangePassword',
          component: ChangePassword,
          meta: { title: '修改密码' }
        },
        {
          path: '/change-records',
          name: 'ChangeRecordList',
          component: ChangeRecordList,
          meta: { title: '变更登记管理' }
        },
        {
          path: '/change-records/:id',
          name: 'ChangeRecordDetail',
          component: ChangeRecordDetail,
          meta: { title: '变更记录详情' }
        },
        {
          path: '/change-records/:id/history',
          name: 'ChangeRecordHistory',
          component: ChangeRecordHistory,
          meta: { title: '变更记录历史' }
        }
      ]
    }
  ]
});

// 动态添加路由的方法（Vue Router 3兼容）
export function addDynamicRoutes(menus) {
  const routes = generateRoutes(menus);
  const mainLayoutRoute = router.options.routes.find(r => r.path === '/');
  if (mainLayoutRoute && mainLayoutRoute.children) {
    routes.forEach(route => {
      // 检查路由是否已存在
      const exists = mainLayoutRoute.children.some(r => r.path === route.path);
      if (!exists && route.component) {
        mainLayoutRoute.children.push(route);
      }
    });
  }
}

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  
  // 登录页面直接放行
  if (to.path === '/login') {
    if (token) {
      next('/');
    } else {
      next();
    }
    return;
  }

  // 未登录跳转到登录页
  if (!token) {
    next('/login');
    return;
  }

  // 已登录，根路径重定向到dashboard
  if (to.path === '/' || to.path === '/index') {
    next('/dashboard');
    return;
  }

  next();
});

export default router;