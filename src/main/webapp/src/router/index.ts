import { createRouter, createWebHashHistory } from 'vue-router';

import Dashboard from '@/views/Dashboard.vue';
import MainLayout from '@/components/MainLayout.vue';

/**
 * 路由配置：仅静态壳路由。
 * 业务模块接入时在此注册，或恢复"菜单驱动动态路由"框架能力（配合 sys_menu 数据）。
 */
const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      name: 'MainLayout',
      component: MainLayout,
      redirect: '/dashboard',
      meta: { title: '首页' },
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: Dashboard,
          meta: { title: '首页' },
        },
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/dashboard',
    },
  ],
});

export default router;
