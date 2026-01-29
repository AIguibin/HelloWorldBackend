import request from '../utils/request';

/**
 * 分页查询链路列表
 */
export const listNetLinks = (params) => request.get('/net-links', { params });

/**
 * 根据ID查询链路详情
 */
export const getNetLink = (id) => request.get(`/net-links/${id}`);

/**
 * 创建链路
 */
export const createNetLink = (data) => request.post('/net-links', data, { 
  headers: { 'X-Button-Name': '创建链路' } 
});

/**
 * 更新链路
 */
export const updateNetLink = (id, data) => request.put(`/net-links/${id}`, data, { 
  headers: { 'X-Button-Name': '更新链路' } 
});

/**
 * 删除链路
 */
export const deleteNetLink = (id) => request.delete(`/net-links/${id}`, { 
  headers: { 'X-Button-Name': '删除链路' } 
});

/**
 * 导出Excel
 */
export const exportNetLinks = (params) => request.get('/net-links/export', { 
  params, 
  responseType: 'blob',
  headers: { 'X-Button-Name': '导出Excel' } 
});
