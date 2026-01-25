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
// 导入审批相关组件
import WorkListTodo from '../views/work-list/WorkListTodo.vue';
import ApprovalForm from '../views/work-list/ApprovalForm.vue';
// 导入字典变更相关组件
import DictChangeApply from '../views/dict-change/DictChangeApply.vue';

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
  'views/SystemSettings': SystemSettings,
  'views/work-list/WorkListTodo': WorkListTodo,
  'views/work-list/ApprovalForm': ApprovalForm,
  'views/dict-change/DictChangeApply': DictChangeApply
};



// 将菜单转换为路由
function menuToRoute(menu) {
  if (menu.menuType !== 'P' || !menu.path) {
    return null;
  }

  const route = {
    path: menu.path,
    name: menu.menuCode,
    component: componentMap[menu.component] || null,
    meta: {
      title: menu.menuName,
      icon: menu.icon,
      perms: menu.resourceKey
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
      // 处理子菜单
      if (menu.children && menu.children.length > 0) {
        const childRoutes = generateRoutes(menu.children);
        if (childRoutes.length > 0) {
          route.children = childRoutes;
        }
      }
      routes.push(route);
    } else if (menu.menuType === 'M' && menu.children && menu.children.length > 0) {
      // 处理菜单目录，递归处理子菜单
      routes.push(...generateRoutes(menu.children));
    }
  }
  return routes;
}
/**
 * 路由配置
 * 基础路由配置，动态路由将通过API获取后添加
 * @type {Router}
 */
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

// 初始化动态路由
export async function initDynamicRoutes() {
  try {
    // 从本地存储读取菜单数据
    const cachedMenus = localStorage.getItem('menuPermissions');
    if (cachedMenus) {
      try {
        const menus = JSON.parse(cachedMenus);
        if (menus && menus.length > 0) {
          addDynamicRoutes(menus);
          return true;
        }
      } catch (parseError) {
        console.error('解析本地存储菜单数据失败:', parseError);
      }
    }
    return false;
  } catch (error) {
    console.error('初始化动态路由失败:', error);
    return false;
  }
}

// 重新加载动态路由（用于权限变更时）
export async function reloadDynamicRoutes() {
  // 清除现有的动态路由
  const mainLayoutRoute = router.options.routes.find(r => r.path === '/');
  if (mainLayoutRoute && mainLayoutRoute.children) {
    // 只保留基础路由（登录和首页）
    mainLayoutRoute.children = mainLayoutRoute.children.filter(route => 
      route.path === '/dashboard'
    );
  }
  
  // 重新加载路由
  return await initDynamicRoutes();
}

// 标记路由是否已初始化
let routesInitialized = false;

router.beforeEach(async (to, from, next) => {
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

  // 初始化动态路由
  if (!routesInitialized) {
    try {
      await initDynamicRoutes();
      routesInitialized = true;
      // 重新导航到当前路由
      next({ ...to, replace: true });
    } catch (error) {
      console.error('初始化路由失败:', error);
      next('/dashboard');
    }
  } else {
    // 权限验证
    const route = router.options.routes.find(r => r.path === '/');
    if (route && route.children) {
      const targetRoute = route.children.find(r => r.path === to.path);
      if (targetRoute && targetRoute.meta && targetRoute.meta.perms) {
        // 这里可以添加具体的权限验证逻辑
        // 例如：检查用户是否有targetRoute.meta.perms对应的权限
        // 如果没有权限，可以重定向到无权限页面或首页
      }
    }
    next();
  }
});

export default router;