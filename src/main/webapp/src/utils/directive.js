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
 * 检查字段权限并控制表单字段状态
 * @param {HTMLElement} el DOM元素
 * @param {Object} fieldConfig 字段配置对象，包含entityType, fieldName, permType
 */
function checkFieldPermission(el, fieldConfig) {
  if (!fieldConfig || !fieldConfig.entityType || !fieldConfig.fieldName) {
    return;
  }
  
  try {
    // 从localStorage获取字段权限信息
    const raw = localStorage.getItem('fieldPermissions');
    const fieldPermissions = JSON.parse(raw || '{}');
    
    const { entityType, fieldName, permType = 2 } = fieldConfig;
    const entityPerms = fieldPermissions[entityType] || [];
    
    // 检查是否具有指定类型的字段权限
    const hasPermission = entityPerms.some(perm => 
      perm.fieldName === fieldName && 
      perm.permType === permType
    );
    
    // 查找表单控件元素
    const inputEl = el.querySelector('input, select, textarea, .el-input__inner, .el-select');
    if (inputEl) {
      if (permType === 2) { // 可编辑权限
        if (!hasPermission) {
          inputEl.setAttribute('readonly', 'readonly');
          inputEl.classList.add('is-disabled');
        } else {
          inputEl.removeAttribute('readonly');
          inputEl.classList.remove('is-disabled');
        }
      } else if (permType === 4) { // 隐藏权限
        el.style.display = !hasPermission ? 'none' : 'block';
      } else if (permType === 5) { // 只读权限
        if (hasPermission) {
          inputEl.setAttribute('readonly', 'readonly');
          inputEl.classList.add('is-disabled');
        } else {
          inputEl.removeAttribute('readonly');
          inputEl.classList.remove('is-disabled');
        }
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