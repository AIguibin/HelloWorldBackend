import axios, { type AxiosError } from 'axios';
import { ElMessage } from 'element-plus';

/** 后端统一响应包装（ResultVO） */
interface ApiEnvelope<T = unknown> {
  code: number;
  message: string;
  data: T;
  timestamp?: number;
  traceId?: string;
}

const service = axios.create({
  // 多环境 API 地址（.env.development / .env.production 的 VITE_API_BASE_URL）
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
});

// 请求拦截器扩展点：接入认证时在此添加 Authorization 头，例如：
// service.interceptors.request.use((config) => {
//   config.headers.Authorization = `Bearer ${localStorage.getItem('token')}`;
//   return config;
// });

service.interceptors.response.use(
  (response) => {
    // 文件流（导出）直接透传
    if (response.headers['content-type'] === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet') {
      return response;
    }
    const envelope = response.data as ApiEnvelope | undefined;
    if (envelope && typeof envelope.code === 'number' && envelope.code !== 200) {
      const message = envelope.message || '请求失败';
      ElMessage.error(message);
      return Promise.reject(new Error(message));
    }
    return envelope?.data as never;
  },
  (error: AxiosError<ApiEnvelope>) => {
    const serverMsg = error.response?.data?.message || error.message || '网络错误';
    ElMessage.error(serverMsg);
    return Promise.reject(error);
  },
);

/** GET：响应拦截器已解包，直接返回业务数据 */
export async function get<T>(url: string, params?: Record<string, unknown>): Promise<T> {
  return service.get(url, { params }) as unknown as Promise<T>;
}

export async function post<T>(url: string, data?: unknown): Promise<T> {
  return service.post(url, data) as unknown as Promise<T>;
}

export async function put<T>(url: string, data?: unknown): Promise<T> {
  return service.put(url, data) as unknown as Promise<T>;
}

export async function del<T>(url: string): Promise<T> {
  return service.delete(url) as unknown as Promise<T>;
}

export default service;
