import Vue from 'vue';
import Router from 'vue-router';
import Login from '../views/Login.vue';
// import VersionList from '../views/VersionList.vue';
// import VersionDetail from '../views/VersionDetail.vue';
import ChangePassword from '../views/ChangePassword.vue';
import ChangeRecordList from '../views/ChangeRecordList.vue';
import ChangeRecordDetail from '../views/ChangeRecordDetail.vue';
import ChangeRecordHistory from '../views/ChangeRecordHistory.vue';

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

const router = new Router({
  mode: 'hash',
  routes: [
    { path: '/login', name: 'Login', component: Login },
    { path: '/', name: 'ChangeRecordList', component: ChangeRecordList },
    { path: '/change-password', name: 'ChangePassword', component: ChangePassword },
    { path: '/change-records', name: 'ChangeRecordListAlias', component: ChangeRecordList },
    { path: '/change-records/:id', name: 'ChangeRecordDetail', component: ChangeRecordDetail },
    { path: '/change-records/:id/history', name: 'ChangeRecordHistory', component: ChangeRecordHistory }
  ]
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  if (to.path === '/login') return next();
  if (!token) return next('/login');
  next();
});

export default router;