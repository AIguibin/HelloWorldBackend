/**
 * 权限混入
 * 提供权限检查相关方法，用于在组件中进行权限控制
 */
export default {
  methods: {
    /**
     * 检查用户是否具有指定权限
     * @param {string} permKey 权限标识
     * @returns {boolean} 是否具有权限
     */
    hasPermission(permKey) {
      try {
        // 从localStorage获取用户权限信息
        const raw = localStorage.getItem('userPermissions');
        const permissions = JSON.parse(raw || '[]');
        return permissions.includes(permKey);
      } catch (e) {
        console.error('权限检查失败:', e);
        return false;
      }
    },

    /**
     * 检查字段权限
     * @param {string} entityType 实体类型
     * @param {string} fieldName 字段名称
     * @param {number} permType 权限类型：1-可见，2-可编辑，3-必填，4-隐藏，5-只读
     * @returns {boolean} 是否具有权限
     */
    checkFieldPermission(entityType, fieldName, permType) {
      try {
        // 从localStorage获取字段权限信息
        const raw = localStorage.getItem('fieldPermissions');
        const fieldPermissions = JSON.parse(raw || '{}');
        const entityPerms = fieldPermissions[entityType] || [];
        return entityPerms.some(perm => 
          perm.fieldName === fieldName && 
          perm.permType === permType
        );
      } catch (e) {
        console.error('字段权限检查失败:', e);
        return true; // 默认返回true，避免因权限数据异常导致功能不可用
      }
    },

    /**
     * 获取当前用户信息
     * @returns {object} 用户信息对象
     */
    getCurrentUser() {
      try {
        const raw = localStorage.getItem('user');
        return JSON.parse(raw || '{}');
      } catch (e) {
        console.error('获取当前用户信息失败:', e);
        return {};
      }
    },

    /**
     * 获取当前用户编号
     * @returns {string} 用户编号
     */
    getCurrentUserNum() {
      const user = this.getCurrentUser();
      return user.usernumb || user.username || '';
    }
  }
};