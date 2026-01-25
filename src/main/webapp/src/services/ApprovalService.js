import request from '../utils/request';

/**
 * 统一审批服务
 * 处理所有审批请求的创建和触发
 */
export default {
  /**
   * 触发审批流程
   * @param {Object} params - 审批参数
   * @param {string|number} params.businessId - 业务ID
   * @param {string} params.businessType - 业务类型
   * @param {string} params.userId - 触发用户ID
   * @param {Object} [params.metadata] - 附加信息
   * @returns {Promise} - 审批结果
   */
  triggerApproval(params) {
    const { businessId, businessType, userId, metadata = {} } = params;
    
    // 根据业务类型调用不同的API
    switch (businessType) {
      case 'CHANGE_RECORD':
        // 变更记录审批
        return request.post(`/api/approval/submit/${businessId}`, {}, {
          headers: {
            'X-Button-Name': '提交审批'
          }
        });
      case 'RELEASE':
      default:
        // 统一调用审批触发接口
        return request.post('/api/approval/trigger', {
          businessId,
          businessType,
          userNum: userId
        }, {
          headers: {
            'X-Button-Name': '提交审批'
          }
        });
    }
  },

  /**
   * 获取审批状态
   * @param {string|number} approvalId - 审批ID
   * @returns {Promise} - 审批状态
   */
  getApprovalStatus(approvalId) {
    return request.get(`/api/approval/status/${approvalId}`);
  },

  /**
   * 获取审批历史
   * @param {string|number} approvalId - 审批ID
   * @param {Object} [params] - 查询参数
   * @param {number} [params.page] - 页码
   * @param {number} [params.size] - 每页记录数
   * @returns {Promise} - 审批历史
   */
  getApprovalHistory(approvalId, params = {}) {
    return request.get(`/api/approval/history/${approvalId}`, { params });
  },

  /**
   * 同意审批
   * @param {string} taskId - 任务ID
   * @param {string} remark - 审批备注
   * @returns {Promise} - 审批结果
   */
  approveTask(taskId, remark) {
    return request.post(`/api/approval/task/${taskId}/approve`, {}, {
      params: { remark },
      headers: {
        'X-Button-Name': '同意审批'
      }
    });
  },

  /**
   * 拒绝审批
   * @param {string} taskId - 任务ID
   * @param {string} remark - 拒绝原因
   * @returns {Promise} - 审批结果
   */
  rejectTask(taskId, remark) {
    return request.post(`/api/approval/task/${taskId}/reject`, {}, {
      params: { remark },
      headers: {
        'X-Button-Name': '拒绝审批'
      }
    });
  },

  /**
   * 转办审批
   * @param {string} taskId - 任务ID
   * @param {string} nextAssigneeNum - 下一个审批人用户编号
   * @param {string} remark - 转办备注
   * @returns {Promise} - 转办结果
   */
  transferTask(taskId, nextAssigneeNum, remark) {
    return request.post(`/api/approval/task/${taskId}/transfer`, {}, {
      params: { nextAssigneeNum, remark },
      headers: {
        'X-Button-Name': '转办审批'
      }
    });
  },

  /**
   * 取消审批任务
   * @param {string} taskId - 任务ID
   * @param {string} remark - 取消原因
   * @returns {Promise} - 取消结果
   */
  cancelTask(taskId, remark) {
    return request.post(`/api/approval/task/${taskId}/cancel`, {}, {
      params: { remark },
      headers: {
        'X-Button-Name': '取消审批'
      }
    });
  },

  /**
   * 查询待办任务
   * @param {string} assigneeNum - 审批人用户编号
   * @param {Object} [params] - 查询参数
   * @param {number} [params.page] - 页码
   * @param {number} [params.size] - 每页记录数
   * @returns {Promise} - 待办任务列表
   */
  getTodoTasks(assigneeNum, params = {}) {
    return request.get('/api/approval/tasks/todo', {
      params: {
        assigneeNum,
        page: params.page || 1,
        size: params.size || 10
      }
    });
  },

  /**
   * 查询已办任务
   * @param {string} operatorNum - 操作人用户编号
   * @param {Object} [params] - 查询参数
   * @param {number} [params.page] - 页码
   * @param {number} [params.size] - 每页记录数
   * @returns {Promise} - 已办任务列表
   */
  getProcessedTasks(operatorNum, params = {}) {
    return request.get('/api/approval/tasks/processed', {
      params: {
        operatorNum,
        page: params.page || 1,
        size: params.size || 10
      }
    });
  },

  /**
   * 查询已结任务
   * @param {string} operatorNum - 操作人用户编号
   * @param {Object} [params] - 查询参数
   * @param {number} [params.page] - 页码
   * @param {number} [params.size] - 每页记录数
   * @returns {Promise} - 已结任务列表
   */
  getCompletedTasks(operatorNum, params = {}) {
    return request.get('/api/approval/tasks/completed', {
      params: {
        operatorNum,
        page: params.page || 1,
        size: params.size || 10
      }
    });
  },

  /**
   * 获取审批流程信息
   * @param {string} instanceId - 流程实例ID
   * @returns {Promise} - 流程信息
   */
  getFlowByInstanceId(instanceId) {
    return request.get(`/api/approval/flow/instance/${instanceId}`);
  }
};
