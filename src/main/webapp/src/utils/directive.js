/**
 * 权限指令实现
 * 提供v-permission和v-field-permission指令，用于组件中的权限控制
 */

/**
 * v-permission指令
 * 根据权限标识控制元素的显示/隐藏
 */
export const permissionDirective = {
  bind(el, binding) {
    checkPermission(el, binding.value);
  },
  update(el, binding) {
    checkPermission(el, binding.value);
  }
};

/**
 * v-field-permission指令
 * 根据字段权限控制表单字段的可编辑性/只读性/隐藏状态
 */
export const fieldPermissionDirective = {
  bind(el, binding) {
    checkFieldPermission(el, binding.value);
  },
  update(el, binding) {
    checkFieldPermission(el, binding.value);
  }
};

/**
 * 检查权限并控制元素显示/隐藏
 * @param {HTMLElement} el DOM元素
 * @param {string} permKey 权限标识
 */
function checkPermission(el, permKey) {
  if (!permKey) {
    return;
  }
  
  try {
    // 从localStorage获取用户权限信息
    const raw = localStorage.getItem('userPermissions');
    const permissions = JSON.parse(raw || '[]');
    
    if (!permissions.includes(permKey)) {
      el.style.display = 'none';
    }
  } catch (e) {
    console.error('权限检查失败:', e);
    el.style.display = 'none';
  }
}

/**
 * 解析并执行条件表达式
 * @param {object} conditionExpression 条件表达式对象
 * @param {object} businessData 当前业务数据
 * @returns {boolean} 条件是否满足
 */
function evaluateConditionExpression(conditionExpression, businessData) {
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
        return conditions.every(cond => evaluateConditionExpression(cond, businessData));
      } else if (conditionExpression.logic === 'or') {
        // 只要有一个条件满足就返回true
        return conditions.some(cond => evaluateConditionExpression(cond, businessData));
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
}

/**
 * 检查字段权限并控制表单字段状态
 * @param {HTMLElement} el DOM元素
 * @param {Object} fieldConfig 字段配置对象，包含entityType, fieldName, permType, businessData
 */
function checkFieldPermission(el, fieldConfig) {
  if (!fieldConfig || !fieldConfig.entityType || !fieldConfig.fieldName) {
    return;
  }
  
  try {
    // 从localStorage获取字段权限信息
    const raw = localStorage.getItem('fieldPermissions');
    const fieldPermissions = JSON.parse(raw || '{}');
    
    const { entityType, fieldName, permType = 2, businessData = {} } = fieldConfig;
    const entityPerms = fieldPermissions[entityType] || [];
    
    // 查找匹配的权限规则
    const matchedPerms = entityPerms.filter(perm => 
      perm.fieldName === fieldName && 
      perm.permType === permType && 
      perm.status === 1 // 只处理启用状态的权限规则
    );
    
    // 如果没有匹配的权限规则，默认允许
    if (matchedPerms.length === 0) {
      return;
    }
    
    // 检查是否有满足条件的权限规则
    const hasPermission = matchedPerms.some(perm => {
      // 如果没有条件表达式，直接返回true
      if (!perm.condition_expression) {
        return true;
      }
      
      // 解析并执行条件表达式
      return evaluateConditionExpression(perm.condition_expression, businessData);
    });
    
    // 查找表单控件元素
    const inputEl = el.querySelector('input, select, textarea, .el-input__inner, .el-select, .el-switch, .el-date-picker');
    if (inputEl) {
      if (permType === 2) { // 可编辑权限
        if (!hasPermission) {
          inputEl.setAttribute('readonly', 'readonly');
          inputEl.classList.add('is-disabled');
          // 处理element-ui组件的disabled属性
          const parentEl = inputEl.closest('.el-form-item');
          if (parentEl) {
            const componentEl = parentEl.querySelector('.el-input, .el-select, .el-switch, .el-date-picker');
            if (componentEl) {
              componentEl.setAttribute('disabled', 'disabled');
            }
          }
        } else {
          inputEl.removeAttribute('readonly');
          inputEl.classList.remove('is-disabled');
          // 处理element-ui组件的disabled属性
          const parentEl = inputEl.closest('.el-form-item');
          if (parentEl) {
            const componentEl = parentEl.querySelector('.el-input, .el-select, .el-switch, .el-date-picker');
            if (componentEl) {
              componentEl.removeAttribute('disabled');
            }
          }
        }
      } else if (permType === 4) { // 隐藏权限
        el.style.display = !hasPermission ? 'none' : 'block';
      } else if (permType === 5) { // 只读权限
        if (hasPermission) {
          inputEl.setAttribute('readonly', 'readonly');
          inputEl.classList.add('is-disabled');
          // 处理element-ui组件的disabled属性
          const parentEl = inputEl.closest('.el-form-item');
          if (parentEl) {
            const componentEl = parentEl.querySelector('.el-input, .el-select, .el-switch, .el-date-picker');
            if (componentEl) {
              componentEl.setAttribute('disabled', 'disabled');
            }
          }
        } else {
          inputEl.removeAttribute('readonly');
          inputEl.classList.remove('is-disabled');
          // 处理element-ui组件的disabled属性
          const parentEl = inputEl.closest('.el-form-item');
          if (parentEl) {
            const componentEl = parentEl.querySelector('.el-input, .el-select, .el-switch, .el-date-picker');
            if (componentEl) {
              componentEl.removeAttribute('disabled');
            }
          }
        }
      }
    } else {
      // 如果没有找到输入元素，直接控制容器的显示/隐藏
      if (permType === 4) {
        el.style.display = !hasPermission ? 'none' : 'block';
      }
    }
  } catch (e) {
    console.error('字段权限检查失败:', e);
    // 默认隐藏或只读，确保安全
    el.style.display = 'none';
  }
}

/**
 * 注册所有权限指令
 * @param {Vue} Vue Vue实例
 */
export function registerDirectives(Vue) {
  Vue.directive('permission', permissionDirective);
  Vue.directive('field-permission', fieldPermissionDirective);
}