<template>
  <!-- 表单验证组件 -->
  <div class="form-validator" style="display: none;">
    <!-- 该组件主要通过JavaScript API提供验证功能，不需要UI界面 -->
  </div>
</template>

<script>
/**
 * 通用表单验证组件
 * 提供统一的表单验证逻辑，支持动态验证规则和自定义验证
 */
export default {
  name: 'FormValidator',
  props: {
    /**
     * 验证规则配置
     */
    rules: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      /**
       * 内置验证规则
       */
      defaultRules: {
        // 必填项验证
        required: (value, param) => {
          if (param === false) return true;
          if (value === undefined || value === null) return '该项为必填项';
          if (typeof value === 'string' && value.trim() === '') return '该项为必填项';
          if (Array.isArray(value) && value.length === 0) return '该项为必填项';
          return true;
        },
        // 最小长度验证
        minLength: (value, param) => {
          if (value === undefined || value === null) return true;
          if (typeof value === 'string' && value.length < param) {
            return `该项长度不能小于${param}个字符`;
          }
          return true;
        },
        // 最大长度验证
        maxLength: (value, param) => {
          if (value === undefined || value === null) return true;
          if (typeof value === 'string' && value.length > param) {
            return `该项长度不能大于${param}个字符`;
          }
          return true;
        },
        // 数值范围验证
        range: (value, param) => {
          const num = Number(value);
          if (isNaN(num)) return '该项必须为数字';
          if (num < param[0] || num > param[1]) {
            return `该项必须在${param[0]}和${param[1]}之间`;
          }
          return true;
        },
        // 正则表达式验证
        pattern: (value, param) => {
          if (value === undefined || value === null) return true;
          const regex = new RegExp(param);
          if (!regex.test(value)) return '该项格式不正确';
          return true;
        },
        // 邮箱格式验证
        email: (value) => {
          if (value === undefined || value === null) return true;
          const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
          if (!regex.test(value)) return '邮箱格式不正确';
          return true;
        },
        // 电话格式验证
        phone: (value) => {
          if (value === undefined || value === null) return true;
          const regex = /^1[3-9]\d{9}$/;
          if (!regex.test(value)) return '手机号码格式不正确';
          return true;
        }
      },
      /**
       * 自定义验证规则
       */
      customRules: {}
    };
  },
  methods: {
    /**
     * 添加自定义验证规则
     * @param {string} name - 规则名称
     * @param {Function} validator - 验证函数，返回true表示验证通过，返回字符串表示验证失败
     */
    addRule(name, validator) {
      this.customRules[name] = validator;
    },

    /**
     * 移除自定义验证规则
     * @param {string} name - 规则名称
     */
    removeRule(name) {
      delete this.customRules[name];
    },

    /**
     * 验证单个字段
     * @param {string} field - 字段名
     * @param {*} value - 字段值
     * @param {Object} [fieldRules] - 字段验证规则，如果不提供则使用props.rules中的规则
     * @returns {Array} - 验证结果数组，每个元素为错误信息字符串
     */
    validateField(field, value, fieldRules = null) {
      const rules = fieldRules || this.rules[field];
      if (!rules) return [];

      const errors = [];
      
      // 遍历所有验证规则
      for (const [ruleName, param] of Object.entries(rules)) {
        // 跳过false值的规则
        if (param === false) continue;
        
        // 获取验证函数
        let validator;
        if (this.customRules[ruleName]) {
          validator = this.customRules[ruleName];
        } else if (this.defaultRules[ruleName]) {
          validator = this.defaultRules[ruleName];
        } else {
          console.warn(`未知的验证规则: ${ruleName}`);
          continue;
        }
        
        // 执行验证
        const result = validator(value, param);
        if (result !== true) {
          errors.push(result);
        }
      }
      
      return errors;
    },

    /**
     * 验证整个表单
     * @param {Object} formData - 表单数据
     * @param {Object} [customRules] - 自定义验证规则，如果不提供则使用props.rules
     * @returns {Object} - 验证结果
     * @returns {boolean} returns.valid - 是否验证通过
     * @returns {Object} returns.errors - 错误信息，key为字段名，value为错误信息数组
     */
    validate(formData, customRules = null) {
      const rules = customRules || this.rules;
      const errors = {};
      
      // 遍历所有需要验证的字段
      for (const field in rules) {
        const value = formData[field];
        const fieldErrors = this.validateField(field, value, rules[field]);
        
        if (fieldErrors.length > 0) {
          errors[field] = fieldErrors;
        }
      }
      
      return {
        valid: Object.keys(errors).length === 0,
        errors
      };
    },

    /**
     * 获取错误信息的字符串表示
     * @param {Object} errors - 错误信息对象，key为字段名，value为错误信息数组
     * @param {string} [separator] - 错误信息分隔符，默认为'\n'
     * @returns {string} - 格式化后的错误信息字符串
     */
    getErrorString(errors, separator = '\n') {
      const errorList = [];
      for (const field in errors) {
        errorList.push(...errors[field]);
      }
      return errorList.join(separator);
    },

    /**
     * 清空验证规则
     */
    clearRules() {
      this.$emit('update:rules', {});
      this.customRules = {};
    }
  }
};
</script>

<style scoped>
.form-validator {
  display: none;
}
</style>
