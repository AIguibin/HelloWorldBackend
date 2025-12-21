import axios from 'axios';
import { Message } from 'element-ui';

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
});

service.interceptors.request.use(config => {
  // 输出实际请求的URL，用于调试
  console.log('Request URL:', config.url);
  // 对于check接口，不添加Authorization头，因为还没有生成正式token
  if (!config.url.endsWith('/check')) {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
  } else {
    console.log('Check interface: skipping Authorization header');
  }
  // 附加登录用户信息（将可能包含非 ASCII 的字段进行 URL 编码）
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (user && user.userNum) config.headers['X-User-Numb'] = user.userNum;
    if (user && user.userName) config.headers['X-User-Name'] = encodeURIComponent(user.userName);
  } catch (e) {}
  // 页面路径用于操作轨迹（确保为 ASCII，可解码为原始路径）
  try {
    const pagePath = `${window.location.pathname}${window.location.hash || ''}`;
    if (pagePath) config.headers['X-Page-Path'] = encodeURIComponent(pagePath);
  } catch (e) {}
  // 添加CSRF令牌到变更类方法
  const method = (config.method || '').toUpperCase();
  if (['POST', 'PUT', 'PATCH', 'DELETE'].includes(method)) {
    const csrf = localStorage.getItem('csrfToken');
    if (csrf) {
      config.headers['X-CSRF-Token'] = csrf;
    }
  }
  // 若上层显式设置了按钮名称，统一进行 URL 编码以避免 XHR 头部报错
  if (config.headers && config.headers['X-Button-Name']) {
    config.headers['X-Button-Name'] = encodeURIComponent(config.headers['X-Button-Name']);
  }
  return config;
});

service.interceptors.response.use(
  res => {
    if (res.headers['content-type'] === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet') {
      return res;
    } else {
      const data = res.data || {};
      // 修改响应拦截器，同时支持code=0和code=200作为成功状态码
      if (data.code !== 0 && data.code !== 200) {
        Message.error(data.message || '请求失败');
        return Promise.reject(new Error(data.message || 'error'));
      }
      return data.data;
    }
  },
  err => {
    console.error(err)
    const status = (err && err.response && err.response.status) || 0;
    const serverMsg = (err && err.response && err.response.data && err.response.data.message) || err.message || '网络错误';
    if (status === 401) {
      // 认证失效：清空所有本地存储并跳转登录页
      localStorage.clear(); // 清空所有本地存储，确保状态一致性
      Message.error(serverMsg || '登录已失效，请重新登录');
      // 使用完整URL跳转，避免路由守卫冲突
      window.location.href = window.location.origin + window.location.pathname + '#/login';
    } else {
      Message.error(serverMsg);
    }
    return Promise.reject(err);
  }
);

export default service;