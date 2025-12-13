import axios from 'axios';
import { Message } from 'element-ui';

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
});

service.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
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
      // 认证失效：清空本地令牌并跳转登录页
      localStorage.removeItem('token');
      localStorage.removeItem('csrfToken');
      localStorage.removeItem('user');
      Message.error(serverMsg || '登录已失效，请重新登录');
      if (window.location.hash !== '#/login') {
        window.location.hash = '#/login';
      }
    } else {
      Message.error(serverMsg);
    }
    return Promise.reject(err);
  }
);

export default service;