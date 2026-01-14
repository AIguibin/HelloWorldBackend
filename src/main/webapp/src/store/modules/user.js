/**
 * 用户模块
 * 管理用户相关的状态
 */
export default {
  /**
   * 模块名称空间
   */
  namespaced: true,
  
  /**
   * 状态
   */
  state: {
    // 用户ID
    id: '',
    // 用户编号
    userNum: '',
    // 用户名
    userName: '',
    // 用户角色
    roles: [],
    // 用户权限
    permissions: [],
    // 用户所属机构
    orgCode: '',
    // 用户所属部门
    deptCode: '',
    // 用户邮箱
    email: '',
    // 用户手机号
    phone: '',
    // 用户头像
    avatar: '',
    // 最后登录时间
    lastLoginTime: '',
    // 是否登录
    isLoggedIn: false
  },
  
  /**
   * mutations
   */
  mutations: {
    /**
     * 设置用户信息
     * @param {Object} state - 状态
     * @param {Object} payload - 用户信息
     */
    setUserInfo(state, payload) {
      state.id = payload.id || '';
      state.userNum = payload.userNum || '';
      state.userName = payload.userName || '';
      state.roles = payload.roles || [];
      state.permissions = payload.permissions || [];
      state.orgCode = payload.orgCode || '';
      state.deptCode = payload.deptCode || '';
      state.email = payload.email || '';
      state.phone = payload.phone || '';
      state.avatar = payload.avatar || '';
      state.lastLoginTime = payload.lastLoginTime || '';
      state.isLoggedIn = true;
    },
    
    /**
     * 清除用户信息
     * @param {Object} state - 状态
     */
    clearUserInfo(state) {
      state.id = '';
      state.userNum = '';
      state.userName = '';
      state.roles = [];
      state.permissions = [];
      state.orgCode = '';
      state.deptCode = '';
      state.email = '';
      state.phone = '';
      state.avatar = '';
      state.lastLoginTime = '';
      state.isLoggedIn = false;
    },
    
    /**
     * 设置用户权限
     * @param {Object} state - 状态
     * @param {Array} payload - 权限列表
     */
    setPermissions(state, payload) {
      state.permissions = payload || [];
    },
    
    /**
     * 设置用户角色
     * @param {Object} state - 状态
     * @param {Array} payload - 角色列表
     */
    setRoles(state, payload) {
      state.roles = payload || [];
    }
  },
  
  /**
   * actions
   */
  actions: {
    /**
     * 登录
     * @param {Object} context - 上下文
     * @param {Object} payload - 登录信息
     */
    login({ commit }, payload) {
      // 这里应该调用登录API，获取用户信息
      // 模拟登录成功
      commit('setUserInfo', {
        id: payload.id || '1',
        userNum: payload.userNum || 'admin',
        userName: payload.userName || '管理员',
        roles: ['ADMIN'],
        permissions: ['approval:submit', 'approval:approve', 'approval:reject', 'approval:transfer'],
        orgCode: 'ORG001',
        deptCode: 'DEPT001',
        email: 'admin@example.com',
        phone: '13800138000',
        lastLoginTime: new Date().toISOString()
      });
      
      // 保存到localStorage
      localStorage.setItem('userInfo', JSON.stringify({
        id: payload.id || '1',
        userNum: payload.userNum || 'admin',
        userName: payload.userName || '管理员'
      }));
    },
    
    /**
     * 登出
     * @param {Object} context - 上下文
     */
    logout({ commit }) {
      // 清除用户信息
      commit('clearUserInfo');
      // 清除localStorage
      localStorage.removeItem('userInfo');
      localStorage.removeItem('token');
    },
    
    /**
     * 加载用户信息
     * @param {Object} context - 上下文
     */
    loadUserInfo({ commit }) {
      // 从localStorage加载用户信息
      const userInfo = localStorage.getItem('userInfo');
      if (userInfo) {
        try {
          const parsedUserInfo = JSON.parse(userInfo);
          commit('setUserInfo', parsedUserInfo);
        } catch (error) {
          console.error('解析用户信息失败:', error);
        }
      }
    }
  },
  
  /**
   * getters
   */
  getters: {
    /**
     * 是否登录
     * @param {Object} state - 状态
     * @returns {boolean} - 是否登录
     */
    isLoggedIn: state => state.isLoggedIn,
    
    /**
     * 获取用户信息
     * @param {Object} state - 状态
     * @returns {Object} - 用户信息
     */
    userInfo: state => ({
      id: state.id,
      userNum: state.userNum,
      userName: state.userName,
      roles: state.roles,
      permissions: state.permissions,
      orgCode: state.orgCode,
      deptCode: state.deptCode,
      email: state.email,
      phone: state.phone,
      avatar: state.avatar,
      lastLoginTime: state.lastLoginTime
    }),
    
    /**
     * 检查用户是否有某个权限
     * @param {Object} state - 状态
     * @returns {Function} - 权限检查函数
     */
    hasPermission: state => permission => {
      return state.permissions.includes(permission);
    },
    
    /**
     * 检查用户是否有某个角色
     * @param {Object} state - 状态
     * @returns {Function} - 角色检查函数
     */
    hasRole: state => role => {
      return state.roles.includes(role);
    }
  }
};
