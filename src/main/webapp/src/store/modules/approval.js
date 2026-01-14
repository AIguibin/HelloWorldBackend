/**
 * 审批模块
 * 管理审批相关的状态
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
    // 当前审批请求
    currentApproval: null,
    // 审批历史记录
    approvalHistory: [],
    // 审批任务
    approvalTasks: {
      // 待办任务
      todo: [],
      // 已办任务
      done: [],
      // 已结任务
      closed: []
    },
    // 审批流程配置
    approvalFlows: [],
    // 审批节点配置
    approvalNodes: [],
    // 审批状态
    approvalStatus: {
      // 审批状态列表
      list: [
        { value: 'DRAFT', label: '草稿' },
        { value: 'PENDING', label: '待审批' },
        { value: 'APPROVED', label: '已通过' },
        { value: 'REJECTED', label: '已拒绝' },
        { value: 'CANCELED', label: '已取消' },
        { value: 'TRANSFERRED', label: '已转办' }
      ]
    },
    // 审批操作类型
    approvalOperations: {
      // 审批操作类型列表
      list: [
        { value: 'SUBMIT', label: '提交' },
        { value: 'APPROVE', label: '同意' },
        { value: 'REJECT', label: '拒绝' },
        { value: 'TRANSFER', label: '转办' },
        { value: 'CANCEL', label: '取消' }
      ]
    }
  },
  
  /**
   * mutations
   */
  mutations: {
    /**
     * 设置当前审批请求
     * @param {Object} state - 状态
     * @param {Object|null} payload - 审批请求
     */
    setCurrentApproval(state, payload) {
      state.currentApproval = payload;
    },
    
    /**
     * 设置审批历史记录
     * @param {Object} state - 状态
     * @param {Array} payload - 审批历史记录
     */
    setApprovalHistory(state, payload) {
      state.approvalHistory = payload || [];
    },
    
    /**
     * 添加审批历史记录
     * @param {Object} state - 状态
     * @param {Object} payload - 审批历史记录
     */
    addApprovalHistory(state, payload) {
      if (payload) {
        state.approvalHistory.unshift(payload);
      }
    },
    
    /**
     * 设置待办任务
     * @param {Object} state - 状态
     * @param {Array} payload - 待办任务列表
     */
    setTodoTasks(state, payload) {
      state.approvalTasks.todo = payload || [];
    },
    
    /**
     * 设置已办任务
     * @param {Object} state - 状态
     * @param {Array} payload - 已办任务列表
     */
    setDoneTasks(state, payload) {
      state.approvalTasks.done = payload || [];
    },
    
    /**
     * 设置已结任务
     * @param {Object} state - 状态
     * @param {Array} payload - 已结任务列表
     */
    setClosedTasks(state, payload) {
      state.approvalTasks.closed = payload || [];
    },
    
    /**
     * 设置审批流程配置
     * @param {Object} state - 状态
     * @param {Array} payload - 审批流程配置列表
     */
    setApprovalFlows(state, payload) {
      state.approvalFlows = payload || [];
    },
    
    /**
     * 设置审批节点配置
     * @param {Object} state - 状态
     * @param {Array} payload - 审批节点配置列表
     */
    setApprovalNodes(state, payload) {
      state.approvalNodes = payload || [];
    },
    
    /**
     * 更新任务状态
     * @param {Object} state - 状态
     * @param {Object} payload - 更新信息
     * @param {string} payload.taskId - 任务ID
     * @param {string} payload.status - 新状态
     */
    updateTaskStatus(state, payload) {
      const { taskId, status } = payload;
      
      // 更新待办任务
      const todoIndex = state.approvalTasks.todo.findIndex(task => task.taskId === taskId);
      if (todoIndex !== -1) {
        state.approvalTasks.todo[todoIndex].taskStatus = status;
        // 如果任务已完成，移至已办任务
        if (status === '已完成' || status === 'APPROVED' || status === 'REJECTED' || status === 'CANCELED') {
          const task = state.approvalTasks.todo.splice(todoIndex, 1)[0];
          state.approvalTasks.done.unshift(task);
        }
      }
      
      // 更新已办任务
      const doneIndex = state.approvalTasks.done.findIndex(task => task.taskId === taskId);
      if (doneIndex !== -1) {
        state.approvalTasks.done[doneIndex].taskStatus = status;
      }
      
      // 更新已结任务
      const closedIndex = state.approvalTasks.closed.findIndex(task => task.taskId === taskId);
      if (closedIndex !== -1) {
        state.approvalTasks.closed[closedIndex].taskStatus = status;
      }
    },
    
    /**
     * 清空审批状态
     * @param {Object} state - 状态
     */
    clearApprovalState(state) {
      state.currentApproval = null;
      state.approvalHistory = [];
      state.approvalTasks = {
        todo: [],
        done: [],
        closed: []
      };
    }
  },
  
  /**
   * actions
   */
  actions: {
    /**
     * 获取审批历史记录
     * @param {Object} context - 上下文
     * @param {string|number} approvalId - 审批ID
     */
    getApprovalHistory({ commit }, approvalId) {
      // 这里应该调用API获取审批历史记录
      // 模拟获取审批历史记录
      commit('setApprovalHistory', [
        {
          id: '1',
          taskId: 'TASK001',
          flowId: 'FLOW001',
          nodeId: 'NODE001',
          businessType: 'CHANGE_RECORD',
          businessId: '123',
          businessCode: 'CR001',
          operationType: 'SUBMIT',
          operatorNum: 'user001',
          operatorName: '张三',
          operationTime: '2024-01-01T12:00:00Z',
          operationRemark: '提交审批',
          beforeStatus: 'DRAFT',
          afterStatus: 'PENDING'
        },
        {
          id: '2',
          taskId: 'TASK001',
          flowId: 'FLOW001',
          nodeId: 'NODE001',
          businessType: 'CHANGE_RECORD',
          businessId: '123',
          businessCode: 'CR001',
          operationType: 'APPROVE',
          operatorNum: 'user002',
          operatorName: '李四',
          operationTime: '2024-01-02T10:00:00Z',
          operationRemark: '同意审批',
          beforeStatus: 'PENDING',
          afterStatus: 'APPROVED'
        }
      ]);
    },
    
    /**
     * 获取待办任务
     * @param {Object} context - 上下文
     * @param {string} assigneeNum - 审批人用户编号
     */
    getTodoTasks({ commit }, assigneeNum) {
      // 这里应该调用API获取待办任务
      // 模拟获取待办任务
      commit('setTodoTasks', [
        {
          id: '1',
          taskId: 'TASK003',
          flowId: 'FLOW001',
          nodeId: 'NODE001',
          businessType: 'CHANGE_RECORD',
          businessId: '456',
          businessCode: 'CR002',
          approverNum: assigneeNum,
          approverName: '管理员',
          taskStatus: 'PENDING',
          currentStatus: '审批中-节点1',
          assignTime: '2024-01-03T09:00:00Z',
          title: '变更记录审批',
          reason: '系统升级变更'
        }
      ]);
    },
    
    /**
     * 获取已办任务
     * @param {Object} context - 上下文
     * @param {string} operatorNum - 操作人用户编号
     */
    getDoneTasks({ commit }, operatorNum) {
      // 这里应该调用API获取已办任务
      // 模拟获取已办任务
      commit('setDoneTasks', [
        {
          id: '2',
          taskId: 'TASK001',
          flowId: 'FLOW001',
          nodeId: 'NODE001',
          businessType: 'CHANGE_RECORD',
          businessId: '123',
          businessCode: 'CR001',
          approverNum: operatorNum,
          approverName: '管理员',
          taskStatus: 'APPROVED',
          currentStatus: '已通过',
          assignTime: '2024-01-01T12:00:00Z',
          approvalTime: '2024-01-02T10:00:00Z',
          approvalRemark: '同意审批',
          title: '变更记录审批',
          reason: '系统升级变更'
        }
      ]);
    },
    
    /**
     * 获取已结任务
     * @param {Object} context - 上下文
     * @param {string} operatorNum - 操作人用户编号
     */
    getClosedTasks({ commit }, operatorNum) {
      // 这里应该调用API获取已结任务
      // 模拟获取已结任务
      commit('setClosedTasks', [
        {
          id: '3',
          taskId: 'TASK002',
          flowId: 'FLOW001',
          nodeId: 'NODE001',
          businessType: 'CHANGE_RECORD',
          businessId: '789',
          businessCode: 'CR003',
          approverNum: operatorNum,
          approverName: '管理员',
          taskStatus: 'REJECTED',
          currentStatus: '已拒绝',
          assignTime: '2024-01-04T14:00:00Z',
          approvalTime: '2024-01-05T16:00:00Z',
          approvalRemark: '拒绝审批',
          title: '变更记录审批',
          reason: '系统降级变更'
        }
      ]);
    },
    
    /**
     * 触发审批
     * @param {Object} context - 上下文
     * @param {Object} payload - 审批参数
     */
    triggerApproval({ commit }, payload) {
      // 这里应该调用API触发审批
      // 模拟触发审批成功
      commit('setCurrentApproval', {
        id: '123',
        businessId: payload.businessId,
        businessType: payload.businessType,
        status: 'PENDING',
        createTime: new Date().toISOString(),
        ...payload.metadata
      });
    },
    
    /**
     * 同意审批
     * @param {Object} context - 上下文
     * @param {Object} payload - 审批参数
     */
    approve({ commit }, payload) {
      // 这里应该调用API同意审批
      // 模拟同意审批成功
      commit('updateTaskStatus', {
        taskId: payload.taskId,
        status: 'APPROVED'
      });
      
      // 添加审批历史记录
      commit('addApprovalHistory', {
        id: Date.now().toString(),
        taskId: payload.taskId,
        flowId: payload.flowId,
        nodeId: payload.nodeId,
        businessType: payload.businessType,
        businessId: payload.businessId,
        businessCode: payload.businessCode,
        operationType: 'APPROVE',
        operatorNum: payload.operatorNum,
        operatorName: payload.operatorName,
        operationTime: new Date().toISOString(),
        operationRemark: payload.remark,
        beforeStatus: 'PENDING',
        afterStatus: 'APPROVED'
      });
    },
    
    /**
     * 拒绝审批
     * @param {Object} context - 上下文
     * @param {Object} payload - 审批参数
     */
    reject({ commit }, payload) {
      // 这里应该调用API拒绝审批
      // 模拟拒绝审批成功
      commit('updateTaskStatus', {
        taskId: payload.taskId,
        status: 'REJECTED'
      });
      
      // 添加审批历史记录
      commit('addApprovalHistory', {
        id: Date.now().toString(),
        taskId: payload.taskId,
        flowId: payload.flowId,
        nodeId: payload.nodeId,
        businessType: payload.businessType,
        businessId: payload.businessId,
        businessCode: payload.businessCode,
        operationType: 'REJECT',
        operatorNum: payload.operatorNum,
        operatorName: payload.operatorName,
        operationTime: new Date().toISOString(),
        operationRemark: payload.remark,
        beforeStatus: 'PENDING',
        afterStatus: 'REJECTED'
      });
    },
    
    /**
     * 转办审批
     * @param {Object} context - 上下文
     * @param {Object} payload - 审批参数
     */
    transfer({ commit }, payload) {
      // 这里应该调用API转办审批
      // 模拟转办审批成功
      commit('updateTaskStatus', {
        taskId: payload.taskId,
        status: 'TRANSFERRED'
      });
      
      // 添加审批历史记录
      commit('addApprovalHistory', {
        id: Date.now().toString(),
        taskId: payload.taskId,
        flowId: payload.flowId,
        nodeId: payload.nodeId,
        businessType: payload.businessType,
        businessId: payload.businessId,
        businessCode: payload.businessCode,
        operationType: 'TRANSFER',
        operatorNum: payload.operatorNum,
        operatorName: payload.operatorName,
        operationTime: new Date().toISOString(),
        operationRemark: payload.remark,
        beforeStatus: 'PENDING',
        afterStatus: 'TRANSFERRED'
      });
    },
    
    /**
     * 取消审批
     * @param {Object} context - 上下文
     * @param {Object} payload - 审批参数
     */
    cancel({ commit }, payload) {
      // 这里应该调用API取消审批
      // 模拟取消审批成功
      commit('updateTaskStatus', {
        taskId: payload.taskId,
        status: 'CANCELED'
      });
      
      // 添加审批历史记录
      commit('addApprovalHistory', {
        id: Date.now().toString(),
        taskId: payload.taskId,
        flowId: payload.flowId,
        nodeId: payload.nodeId,
        businessType: payload.businessType,
        businessId: payload.businessId,
        businessCode: payload.businessCode,
        operationType: 'CANCEL',
        operatorNum: payload.operatorNum,
        operatorName: payload.operatorName,
        operationTime: new Date().toISOString(),
        operationRemark: payload.remark,
        beforeStatus: 'PENDING',
        afterStatus: 'CANCELED'
      });
    }
  },
  
  /**
   * getters
   */
  getters: {
    /**
     * 获取当前审批请求
     * @param {Object} state - 状态
     * @returns {Object|null} - 当前审批请求
     */
    getCurrentApproval: state => state.currentApproval,
    
    /**
     * 获取审批历史记录
     * @param {Object} state - 状态
     * @returns {Array} - 审批历史记录
     */
    getApprovalHistory: state => state.approvalHistory,
    
    /**
     * 获取待办任务
     * @param {Object} state - 状态
     * @returns {Array} - 待办任务
     */
    getTodoTasks: state => state.approvalTasks.todo,
    
    /**
     * 获取已办任务
     * @param {Object} state - 状态
     * @returns {Array} - 已办任务
     */
    getDoneTasks: state => state.approvalTasks.done,
    
    /**
     * 获取已结任务
     * @param {Object} state - 状态
     * @returns {Array} - 已结任务
     */
    getClosedTasks: state => state.approvalTasks.closed,
    
    /**
     * 获取审批状态列表
     * @param {Object} state - 状态
     * @returns {Array} - 审批状态列表
     */
    getApprovalStatusList: state => state.approvalStatus.list,
    
    /**
     * 根据值获取审批状态标签
     * @param {Object} state - 状态
     * @returns {Function} - 根据值获取标签的函数
     */
    getApprovalStatusLabel: state => value => {
      const status = state.approvalStatus.list.find(item => item.value === value);
      return status ? status.label : value;
    },
    
    /**
     * 获取审批操作类型列表
     * @param {Object} state - 状态
     * @returns {Array} - 审批操作类型列表
     */
    getApprovalOperationsList: state => state.approvalOperations.list,
    
    /**
     * 根据值获取审批操作类型标签
     * @param {Object} state - 状态
     * @returns {Function} - 根据值获取标签的函数
     */
    getApprovalOperationLabel: state => value => {
      const operation = state.approvalOperations.list.find(item => item.value === value);
      return operation ? operation.label : value;
    },
    
    /**
     * 获取待办任务数量
     * @param {Object} state - 状态
     * @returns {number} - 待办任务数量
     */
    getTodoTaskCount: state => state.approvalTasks.todo.length,
    
    /**
     * 获取已办任务数量
     * @param {Object} state - 状态
     * @returns {number} - 已办任务数量
     */
    getDoneTaskCount: state => state.approvalTasks.done.length,
    
    /**
     * 获取已结任务数量
     * @param {Object} state - 状态
     * @returns {number} - 已结任务数量
     */
    getClosedTaskCount: state => state.approvalTasks.closed.length
  }
};
