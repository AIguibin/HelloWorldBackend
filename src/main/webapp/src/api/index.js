import request from '../utils/request';

export const login = (username, password) => request.post('/login', { username, password });

// 新增：修改密码
export const changePassword = (data) => request.post('/users/change-password', data);

// 代码脚本变更登记 API
export const listChangeRecords = (params) => request.get('/change-records', { params });
export const createChangeRecord = (data) => request.post('/change-records', data, { headers: { 'X-Button-Name': '创建记录' } });
export const updateChangeRecord = (id, data) => request.put(`/change-records/${id}`, data, { headers: { 'X-Button-Name': '更新记录' } });
export const deleteChangeRecord = (id) => request.delete(`/change-records/${id}`, { headers: { 'X-Button-Name': '删除记录' } });
export const getChangeRecord = (id) => request.get(`/change-records/${id}`);
export const getChangeRecordDetail = (id) => request.get(`/change-records/${id}`);
export const getChangeRecordHistory = (id, params) => request.get(`/change-records/${id}/history`, { params });
export const exportChangeRecords = (params) => request.get('/change-records/export', { params, responseType: 'blob', headers: { 'X-Button-Name': '下载列表' } });