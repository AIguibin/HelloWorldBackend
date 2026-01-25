import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

// 导入权限指令
import { registerDirectives } from './utils/directive';

Vue.config.productionTip = false;
Vue.use(ElementUI);

// 注册权限指令
registerDirectives(Vue);

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app');