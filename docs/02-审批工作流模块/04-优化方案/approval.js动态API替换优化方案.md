# approval.js动态API替换优化方案

## 1. 文件分析

### 1.1 文件结构
`approval.js`是一个Vuex store模块，用于管理审批相关的状态，包含以下部分：
- **state**：存储审批相关的状态数据
- **mutations**：修改状态的同步方法
- **actions**：处理异步操作的方法
- **getters**：从状态中派生出新的状态

### 1.2 硬编码数据问题

当前文件中存在大量硬编码的模拟数据，主要集中在`actions`部分，具体问题如下：

| Action方法 | 硬编码内容 | 问题 |
|------------|------------|------|
| `getApprovalHistory` | 模拟审批历史记录数组 | 应从API获取实际审批历史 |
| `getTodoTasks` | 模拟待办任务数组 | 应从API获取实际待办任务 |
| `getDoneTasks` | 模拟已办任务数组 | 应从API获取实际已办任务 |
| `getClosedTasks` | 模拟已结任务数组 | 应从API获取实际已结任务 |
| `triggerApproval` | 模拟审批触发结果 | 应调用API实际触发审批 |
| `approve` | 模拟同意审批结果 | 应调用API实际同意审批 |
| `reject` | 模拟拒绝审批结果 | 应调用API实际拒绝审批 |
| `transfer` | 模拟转办审批结果 | 应调用API实际转办审批 |
| `cancel` | 模拟取消审批结果 | 应调用API实际取消审批 |

### 1.3 静态配置
以下静态配置可以保留，因为它们是系统定义的常量：
- `approvalStatus.list`：审批状态列表
- `approvalOperations.list`：审批操作类型列表

## 2. 优化方案

### 2.1 引入API服务
首先需要引入审批相关的API服务，假设项目中已有`api/approval`模块：

```javascript
import approvalApi from '@/api/approval';
```

### 2.2 修改Actions方法

#### 2.2.1 获取审批历史记录
```javascript
getApprovalHistory({ commit }, approvalId) {
  return approvalApi.getApprovalHistory(approvalId)
    .then(data => {
      commit('setApprovalHistory', data || []);
    })
    .catch(error => {
      console.error('获取审批历史记录失败:', error);
      commit('setApprovalHistory', []);
      throw error;
    });
}
```

#### 2.2.2 获取待办任务
```javascript
getTodoTasks({ commit }, assigneeNum) {
  return approvalApi.getTodoTasks(assigneeNum)
    .then(data => {
      commit('setTodoTasks', data || []);
    })
    .catch(error => {
      console.error('获取待办任务失败:', error);
      commit('setTodoTasks', []);
      throw error;
    });
}
```

#### 2.2.3 获取已办任务
```javascript
getDoneTasks({ commit }, operatorNum) {
  return approvalApi.getDoneTasks(operatorNum)
    .then(data => {
      commit('setDoneTasks', data || []);
    })
    .catch(error => {
      console.error('获取已办任务失败:', error);
      commit('setDoneTasks', []);
      throw error;
    });
}
```

#### 2.2.4 获取已结任务
```javascript
getClosedTasks({ commit }, operatorNum) {
  return approvalApi.getClosedTasks(operatorNum)
    .then(data => {
      commit('setClosedTasks', data || []);
    })
    .catch(error => {
      console.error('获取已结任务失败:', error);
      commit('setClosedTasks', []);
      throw error;
    });
}
```

#### 2.2.5 触发审批
```javascript
triggerApproval({ commit }, payload) {
  return approvalApi.triggerApproval(payload)
    .then(data => {
      commit('setCurrentApproval', data);
    })
    .catch(error => {
      console.error('触发审批失败:', error);
      throw error;
    });
}
```

#### 2.2.6 同意审批
```javascript
approve({ commit }, payload) {
  return approvalApi.approveTask(payload)
    .then(response => {
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
      
      return response;
    })
    .catch(error => {
      console.error('同意审批失败:', error);
      throw error;
    });
}
```

#### 2.2.7 拒绝审批
```javascript
reject({ commit }, payload) {
  return approvalApi.rejectTask(payload)
    .then(response => {
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
      
      return response;
    })
    .catch(error => {
      console.error('拒绝审批失败:', error);
      throw error;
    });
}
```

#### 2.2.8 转办审批
```javascript
transfer({ commit }, payload) {
  return approvalApi.transferTask(payload)
    .then(response => {
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
      
      return response;
    })
    .catch(error => {
      console.error('转办审批失败:', error);
      throw error;
    });
}
```

#### 2.2.9 取消审批
```javascript
cancel({ commit }, payload) {
  return approvalApi.cancelTask(payload)
    .then(response => {
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
      
      return response;
    })
    .catch(error => {
      console.error('取消审批失败:', error);
      throw error;
    });
}
```

### 2.3 添加API错误处理
为所有API调用添加统一的错误处理，确保在API调用失败时能够优雅处理，不影响系统正常运行。

## 3. 预期效果

### 3.1 数据实时性
审批数据将从API动态获取，确保数据实时准确，反映最新的审批状态。

### 3.2 功能完整性
审批操作将实际调用API执行，而非模拟，确保所有审批功能正常工作。

### 3.3 错误处理
添加了完善的错误处理机制，提高系统稳定性，在API调用失败时能够优雅处理。

### 3.4 代码可维护性
减少硬编码，提高代码的可扩展性和可维护性，便于后续功能扩展。

### 3.5 优化前后对比

| 优化前 | 优化后 |
|--------|--------|
| 硬编码模拟数据 | 动态API获取数据 |
| 无错误处理 | 完善的错误处理 |
| 功能模拟 | 实际功能执行 |
| 数据不实时 | 数据实时准确 |
| 代码可维护性差 | 代码可维护性高 |

## 4. 实现步骤

### 4.1 引入API服务
首先需要引入审批相关的API服务模块。

### 4.2 替换硬编码数据
依次替换每个action方法中的硬编码数据为API调用。

### 4.3 添加错误处理
为所有API调用添加统一的错误处理机制。

### 4.4 测试验证
测试所有审批相关功能，确保与后端API正确对接。

## 5. 依赖条件

### 5.1 后端API
已实现审批相关的后端API，包括：
- 获取审批历史记录
- 获取待办任务
- 获取已办任务
- 获取已结任务
- 触发审批
- 同意审批
- 拒绝审批
- 转办审批
- 取消审批

### 5.2 前端API服务
已封装审批相关的前端API服务模块，提供与后端API对接的方法。

### 5.3 API返回格式
后端API返回格式与前端预期一致，包含`data`字段存储实际数据。
