import Vue from 'vue';
import Router from 'vue-router';
import Login from '../views/Login.vue';
import MainLayout from '../components/MainLayout.vue';
import DashboardCards from '../components/DashboardCards.vue';

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

// 将菜单转换为路由
function menuToRoute(menu) {
  if (menu.menuType !== 'P' || !menu.path) {
    return null;
  }

  let component = null;
  if (menu.component) {
    try {
      // 动态导入组件
      component = () => import(`../views/${menu.component}.vue`).then(module => {
        console.log('组件导入成功:', module);
        // 返回组件的 default 导出，Vue 组件通常是 default 导出
        return module.default || module;
      }).catch(error => {
        console.error('组件导入失败:', error);
        // 导入失败时返回 null，避免路由注册错误
        return null;
      });
      console.log('=====component======', `../views/${menu.component}.vue`);
    } catch (error) {
      console.error('动态导入组件失败:', error);
    }
  }

  const route = {
    path: menu.path,
    name: menu.menuCode,
    component,
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
  console.log("routes1=>",routes)
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
  // 1. 生成路由配置
  const routes = generateRoutes(menus);
  // 2. 查找主布局路由
  const mainLayoutRoute = router.options.routes.find(r => r.path === '/');
  // 3. 检查主布局路由是否存在且有子路由
  if (mainLayoutRoute && mainLayoutRoute.children) {
    // 4. 过滤出需要添加的新路由（去重）
    const newRoutes = routes.filter(route => {
      const exists = mainLayoutRoute.children.some(r => r.path === route.path);
      return !exists && route.component;
    });
    
    if (newRoutes.length > 0) {
      // 5. 添加到 children 数组（用于后续查找和路由匹配）
      newRoutes.forEach(route => {
        mainLayoutRoute.children.push(route);
      });
      
      // 6. 使用 router.addRoutes() 方法添加路由（Vue Router 3 正确方式）
      // 构建包含主布局路由和所有子路由的完整路由配置
      // Vue Router 3 会合并相同路径的路由，所以我们需要添加一个包含所有子路由的完整主布局路由配置
      const routesToAdd = [{
        path: '/',
        component: MainLayout,
        children: newRoutes
      }];
      router.addRoutes(routesToAdd);
    }
  }
}

// 初始化动态路由
export async function initDynamicRoutes() {
  try {
    // 从本地存储读取菜单数据
    // const cachedMenus = localStorage.getItem('menuPermissions');
    // 临时处理无需要权限菜单
    const cachedMenus = localStorage.getItem('menuNoPermissions');
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