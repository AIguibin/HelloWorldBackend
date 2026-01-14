/**
 * 事件总线
 * 用于组件间通信，支持事件的发布和订阅
 */
import Vue from 'vue';

// 创建一个新的Vue实例作为事件总线
const eventBus = new Vue();

/**
 * 事件总线API
 * @type {Object}
 */
export default {
  /**
   * 发布事件
   * @param {string} eventName - 事件名称
   * @param {*} [payload] - 事件数据
   */
  emit(eventName, payload = null) {
    eventBus.$emit(eventName, payload);
  },

  /**
   * 订阅事件
   * @param {string} eventName - 事件名称
   * @param {Function} callback - 事件回调函数
   * @param {Object} [context] - 回调函数上下文
   * @returns {Object} - 订阅信息，包含unsubscribe方法
   */
  on(eventName, callback, context = null) {
    const wrappedCallback = context ? callback.bind(context) : callback;
    eventBus.$on(eventName, wrappedCallback);
    
    return {
      /**
       * 取消订阅
       */
      unsubscribe() {
        eventBus.$off(eventName, wrappedCallback);
      }
    };
  },

  /**
   * 订阅事件，只执行一次
   * @param {string} eventName - 事件名称
   * @param {Function} callback - 事件回调函数
   * @param {Object} [context] - 回调函数上下文
   */
  once(eventName, callback, context = null) {
    const wrappedCallback = context ? callback.bind(context) : callback;
    eventBus.$once(eventName, wrappedCallback);
  },

  /**
   * 取消订阅
   * @param {string} [eventName] - 事件名称，如果不提供则取消所有事件的订阅
   * @param {Function} [callback] - 事件回调函数，如果不提供则取消该事件的所有订阅
   */
  off(eventName = null, callback = null) {
    if (eventName) {
      eventBus.$off(eventName, callback);
    } else {
      eventBus.$off();
    }
  },

  /**
   * 审批相关事件常量
   */
  events: {
    // 审批触发事件
    APPROVAL_TRIGGERED: 'approval:triggered',
    // 审批通过事件
    APPROVAL_APPROVED: 'approval:approved',
    // 审批拒绝事件
    APPROVAL_REJECTED: 'approval:rejected',
    // 审批转办事件
    APPROVAL_TRANSFERRED: 'approval:transferred',
    // 审批完成事件
    APPROVAL_COMPLETED: 'approval:completed',
    // 审批任务更新事件
    APPROVAL_TASK_UPDATED: 'approval:task:updated',
    // 审批状态更新事件
    APPROVAL_STATUS_UPDATED: 'approval:status:updated'
  }
};
