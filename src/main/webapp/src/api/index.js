import request from '../utils/request';

export const login = (userNum, password) => request.post('/login', { userNum, password });
export const checkLogin = (data) => request.post('/check', data);

// 新增：修改密码
export const changePassword = (data) => request.post('/users/change-password', data);

// 菜单权限 API
export const getUserMenus = () => request.get('/menus/user');
export const getUserPermissions = () => request.get('/menus/permissions');

// 代码脚本变更登记 API
export const listChangeRecords = (params) => request.get('/change-records', { params });
export const createChangeRecord = (data) => request.post('/change-records', data, { headers: { 'X-Button-Name': '创建记录' } });
export const updateChangeRecord = (id, data) => request.put(`/change-records/${id}`, data, { headers: { 'X-Button-Name': '更新记录' } });
export const deleteChangeRecord = (id) => request.delete(`/change-records/${id}`, { headers: { 'X-Button-Name': '删除记录' } });
export const getChangeRecord = (id) => request.get(`/change-records/${id}`);
export const getChangeRecordDetail = (id) => request.get(`/change-records/${id}`);
export const getChangeRecordHistory = (id, params) => request.get(`/change-records/${id}/history`, { params });
export const exportChangeRecords = (params) => request.get('/change-records/export', { params, responseType: 'blob', headers: { 'X-Button-Name': '下载列表' } });

// 字典 API
export const getDictItemsByType = (dictTypeCode) => request.get(`/dict/items/type/${dictTypeCode}`);
export const getDictItemsByTypeAndGroup = (dictTypeCode, groupCode) => request.get(`/dict/items/type/${dictTypeCode}/group/${groupCode}`);
export const listDictTypes = () => request.get('/dict/types/enabled');

// 审批流程 API
export const submitApproval = (recordId) => request.post(`/approval/submit/${recordId}`, {}, { headers: { 'X-Button-Name': '提交审批' } });
export const startApprovalProcess = (businessId, businessType, userNum) => request.post(`/approval/process/start`, { businessId, businessType, userNum }, { headers: { 'X-Button-Name': '启动审批流程' } });
export const approveTask = (taskId, remark) => request.post(`/approval/task/${taskId}/approve`, {}, { params: { remark }, headers: { 'X-Button-Name': '同意审批' } });
export const rejectTask = (taskId, remark) => request.post(`/approval/task/${taskId}/reject`, {}, { params: { remark }, headers: { 'X-Button-Name': '驳回审批' } });
export const transferTask = (taskId, nextAssigneeNum, remark) => request.post(`/approval/task/${taskId}/transfer`, {}, { params: { nextAssigneeNum, remark }, headers: { 'X-Button-Name': '转办审批' } });
export const getApprovalHistory = (recordId, params) => request.get(`/approval/history/${recordId}`, { params });
export const getApprovalTodoTasks = (assigneeNum, params) => request.get('/approval/tasks/todo', { params: { ...params, assigneeNum } });
export const getApprovalProcessedTasks = (operatorNum, params) => request.get('/approval/tasks/processed', { params: { ...params, operatorNum } });
export const getApprovalCompletedTasks = (operatorNum, params) => request.get('/approval/tasks/completed', { params: { ...params, operatorNum } });