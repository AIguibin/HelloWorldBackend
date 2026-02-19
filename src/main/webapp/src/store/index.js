/**
 * Vuex store 入口文件
 * 管理系统的全局状态
 */
import Vue from 'vue';
import Vuex from 'vuex';

// 导入模块
// import user from './modules/user';
// import approval from './modules/approval';

// 使用Vuex
Vue.use(Vuex);

// 创建并导出store
const store = new Vuex.Store({
  /**
   * 根状态
   */
  state: {
    // 应用名称
    appName: '审批工作流系统',
    // 系统版本
    version: '1.0.0',
    // 加载状态
    loading: false,
    // 错误信息
    error: null
  },
  
  /**
   * 根mutations
   */
  mutations: {
    /**
     * 设置加载状态
     * @param {Object} state - 状态
     * @param {boolean} payload - 加载状态
     */
    setLoading(state, payload) {
      state.loading = payload;
    },
    
    /**
     * 设置错误信息
     * @param {Object} state - 状态
     * @param {string|null} payload - 错误信息
     */
    setError(state, payload) {
      state.error = payload;
    }
  },
  
  /**
   * 根actions
   */
  actions: {
    /**
     * 显示加载状态
     * @param {Object} context - 上下文
     */
    showLoading({ commit }) {
      commit('setLoading', true);
    },
    
    /**
     * 隐藏加载状态
     * @param {Object} context - 上下文
     */
    hideLoading({ commit }) {
      commit('setLoading', false);
    },
    
    /**
     * 显示错误信息
     * @param {Object} context - 上下文
     * @param {string} payload - 错误信息
     */
    showError({ commit }, payload) {
      commit('setError', payload);
    },
    
    /**
     * 隐藏错误信息
     * @param {Object} context - 上下文
     */
    hideError({ commit }) {
      commit('setError', null);
    }
  },
  
  /**
   * 根getters
   */
  getters: {
    /**
     * 是否加载中
     * @param {Object} state - 状态
     * @returns {boolean} - 是否加载中
     */
    isLoading: state => state.loading,
    
    /**
     * 获取错误信息
     * @param {Object} state - 状态
     * @returns {string|null} - 错误信息
     */
    getError: state => state.error
  },
  
  /**
   * 模块
   */
  modules: {}
});

export default store;
