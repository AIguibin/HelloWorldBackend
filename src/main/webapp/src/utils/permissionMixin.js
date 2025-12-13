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
 * @param {object} businessData 当前业务数据，用于条件表达式解析
 * @returns {boolean} 是否具有权限
 */
    checkFieldPermission(entityType, fieldName, permType, businessData = {}) {
      try {
        // 从localStorage获取字段权限信息
        const raw = localStorage.getItem('fieldPermissions');
        const fieldPermissions = JSON.parse(raw || '{}');
        const entityPerms = fieldPermissions[entityType] || [];
        
        // 查找匹配的权限规则
        const matchedPerms = entityPerms.filter(perm => 
          perm.fieldName === fieldName && 
          perm.permType === permType && 
          perm.status === 1 // 只处理启用状态的权限规则
        );
        
        // 如果没有匹配的权限规则，返回默认值
        if (matchedPerms.length === 0) {
          return true; // 默认返回true，避免因权限数据异常导致功能不可用
        }
        
        // 检查是否有满足条件的权限规则
        return matchedPerms.some(perm => {
          // 如果没有条件表达式，直接返回true
          if (!perm.condition_expression) {
            return true;
          }
          
          // 解析并执行条件表达式
          return this.evaluateConditionExpression(perm.condition_expression, businessData);
        });
      } catch (e) {
        console.error('字段权限检查失败:', e);
        return true; // 默认返回true，避免因权限数据异常导致功能不可用
      }
    },
    
    /**
     * 解析并执行条件表达式
     * @param {object} conditionExpression 条件表达式对象
     * @param {object} businessData 当前业务数据
     * @returns {boolean} 条件是否满足
     */
    evaluateConditionExpression(conditionExpression, businessData) {
      try {
        // 如果条件表达式是字符串，尝试解析为JSON
        if (typeof conditionExpression === 'string') {
          conditionExpression = JSON.parse(conditionExpression);
        }
        
        // 基础条件表达式格式：{ "field": "fieldName", "operator": "eq", "value": "targetValue" }
        // 支持逻辑运算符：{ "logic": "and/or", "conditions": [condition1, condition2, ...] }
        
        if (!conditionExpression) {
          return true;
        }
        
        // 处理逻辑表达式
        if (conditionExpression.logic && conditionExpression.conditions) {
          const conditions = conditionExpression.conditions;
          if (conditionExpression.logic === 'and') {
            // 所有条件都满足才返回true
            return conditions.every(cond => this.evaluateConditionExpression(cond, businessData));
          } else if (conditionExpression.logic === 'or') {
            // 只要有一个条件满足就返回true
            return conditions.some(cond => this.evaluateConditionExpression(cond, businessData));
          }
        }
        
        // 处理简单条件表达式
        if (conditionExpression.field && conditionExpression.operator && conditionExpression.value !== undefined) {
          const fieldValue = businessData[conditionExpression.field];
          const targetValue = conditionExpression.value;
          const operator = conditionExpression.operator;
          
          // 根据运算符执行比较
          switch (operator) {
            case 'eq':
              return fieldValue === targetValue;
            case 'neq':
              return fieldValue !== targetValue;
            case 'gt':
              return Number(fieldValue) > Number(targetValue);
            case 'lt':
              return Number(fieldValue) < Number(targetValue);
            case 'gte':
              return Number(fieldValue) >= Number(targetValue);
            case 'lte':
              return Number(fieldValue) <= Number(targetValue);
            case 'contains':
              return String(fieldValue).includes(String(targetValue));
            case 'notContains':
              return !String(fieldValue).includes(String(targetValue));
            case 'in':
              return Array.isArray(targetValue) && targetValue.includes(fieldValue);
            case 'notIn':
              return !Array.isArray(targetValue) || !targetValue.includes(fieldValue);
            case 'isNull':
              return fieldValue === null || fieldValue === undefined || fieldValue === '';
            case 'isNotNull':
              return fieldValue !== null && fieldValue !== undefined && fieldValue !== '';
            default:
              console.error('未知的条件运算符:', operator);
              return true;
          }
        }
        
        return true;
      } catch (e) {
        console.error('条件表达式解析失败:', e);
        return true; // 解析失败时默认返回true，避免权限异常
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
      return user.userNum || user.userName || '';
    }
  }
};