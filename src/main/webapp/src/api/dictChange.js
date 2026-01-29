import request from '../utils/request';

// 获取字典类型列表
export const getDictTypes = () => request.get('/dict/change/dict-types');

// 加载字典类型和字典项数据
export const loadDictType = (dictTypeId) => request.get(`/dict/change/load-dict-type/${dictTypeId}`);