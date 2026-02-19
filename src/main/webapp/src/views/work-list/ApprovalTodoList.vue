<template>
  <div class="approval-todo-page">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <div class="search-container">
        <!-- 搜索输入区域 -->
        <div class="search-inputs-wrapper">
          <!-- 默认显示的搜索项 -->
          <el-input v-model="search.assignee" placeholder="审批人" class="search-input" prefix-icon="el-icon-user" />
          <el-select v-model="search.taskStatus" placeholder="任务状态" class="search-select">
            <el-option label="待审批" value="PENDING" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
          <el-input v-model="search.businessCode" placeholder="业务编码" class="search-input" prefix-icon="el-icon-document" />
          <el-input v-model="search.businessTitle" placeholder="业务标题" class="search-input" prefix-icon="el-icon-edit" />
          <el-select v-model="search.currentNode" placeholder="当前节点" class="search-select">
            <el-option v-for="item in nodeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>

          <!-- 点击展开后显示的搜索项 -->
          <template v-if="isSearchExpanded">
            <el-date-picker v-model="searchRange" type="datetimerange" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" class="search-date" />
          </template>

          <!-- 展开/收起按钮 -->
          <el-button @click="toggleSearchExpanded" type="text" class="expand-btn">
            {{ isSearchExpanded ? '收起' : '展开' }}
            <i :class="['el-icon-arrow-down', { 'rotate-180': isSearchExpanded }]"></i>
          </el-button>
        </div>

        <!-- 按钮组区域 -->
        <div class="search-buttons">
          <el-button @click="resetSearch" class="action-btn reset-btn">
            <i class="el-icon-refresh-left"></i>
            重置
          </el-button>
          <el-button type="primary" @click="fetchList(1)" class="action-btn search-btn">
            <i class="el-icon-search"></i>
            查询
          </el-button>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table :data="list" stripe class="data-table" :fit="true" border v-loading="loading">
        <el-table-column width="200" label="操作" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleApprove(scope.row)" class="table-btn" :disabled="!canEdit" v-permission="'approval:task:approve'">
              <i class="el-icon-check"></i>
              处理
            </el-button>
            <el-button size="mini" @click="handleTransfer(scope.row)" class="table-btn transfer-btn" :disabled="!canEdit" v-permission="'approval:task:transfer'">
              <i class="el-icon-share"></i>
              转办
            </el-button>
            <el-button size="mini" @click="handleDetail(scope.row)" class="table-btn detail-btn" v-permission="'approval:task:detail'">
              <i class="el-icon-view"></i>
              查看详情
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="taskId" label="任务ID" width="160" :show-overflow-tooltip="true" />
        <el-table-column prop="businessCode" label="业务编码" width="140" :show-overflow-tooltip="true" />
        <el-table-column prop="businessTitle" label="业务标题" width="200" :show-overflow-tooltip="true" />
        <el-table-column prop="currentNode" label="当前节点" width="120" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <el-tag :type="getNodeTagType(scope.row.currentNode)" class="node-tag">{{ scope.row.currentNode || '未分配' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assignTime" label="接收时间" width="180">
          <template slot-scope="scope">{{ formatDateTime(scope.row.assignTime) }}</template>
        </el-table-column>
        <el-table-column prop="expireTime" label="过期时间" width="180">
          <template slot-scope="scope">
            <span :class="{ 'expire-warning': isExpireWarning(scope.row.expireTime) }">
              {{ formatDateTime(scope.row.expireTime) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="assignee" label="审批人" width="140" :show-overflow-tooltip="true" />
        <el-table-column prop="businessType" label="业务类型" width="140" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <el-tag :type="getBusinessTypeTagType(scope.row.businessType)" class="business-tag">{{ scope.row.businessType || '变更记录' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="readStatus" label="状态" width="80" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <el-tag :type="scope.row.readStatus === 'UNREAD' ? 'warning' : 'success'" class="status-tag">
              {{ scope.row.readStatus === 'UNREAD' ? '未读' : '已读' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 空数据提示 -->
        <template slot="empty">
          <div class="empty-data">
            <i class="el-icon-info empty-icon"></i>
            <span class="empty-text">暂无待办任务</span>
          </div>
        </template>
        <!-- 加载失败提示 -->
        <template v-if="loadError" slot="empty">
          <div class="load-error">
            <i class="el-icon-error error-icon"></i>
            <span class="error-text">{{ errorMessage }}</span>
            <el-button type="primary" size="small" @click="fetchList" class="retry-btn">
              <i class="el-icon-refresh"></i>
              重新加载
            </el-button>
          </div>
        </template>
      </el-table>
    </div>

    <div class="pagination-container">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="page" :page-sizes="[5, 10, 20, 30]" :page-size="pageSize" layout="total,sizes,prev,pager,next,jumper" :total="total">
      </el-pagination>
    </div>

    <!-- 审批处理对话框 -->
    <approval-process-dialog 
      ref="approvalProcessDialog" 
      :visible="showProcessDialog"
      :task="selectedTask" 
      :business-data="{}" 
      @success="onProcessSuccess" 
      @update:visible="showProcessDialog = $event"
    />

    <!-- 转办对话框 -->
    <el-dialog :visible.sync="showTransferDialog" title="任务转办" width="50%" class="transfer-dialog">
      <el-form :model="transferForm" label-width="100px" class="transfer-form">
        <el-form-item label="任务ID" prop="taskId">
          <el-input v-model="transferForm.taskId" disabled class="readonly-input" />
        </el-form-item>
        <el-form-item label="当前审批人" prop="currentAssignee">
          <el-input v-model="transferForm.currentAssignee" disabled class="readonly-input" />
        </el-form-item>
        <el-form-item label="新审批人" prop="newAssignee" required>
          <el-select v-model="transferForm.newAssignee" placeholder="请选择新审批人" class="select-input">
            <el-option v-for="user in userOptions" :key="user.value" :label="user.label" :value="user.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="转办原因" prop="remark" required>
          <el-input v-model="transferForm.remark" type="textarea" rows="3" placeholder="请输入转办原因" class="textarea-input" />
        </el-form-item>
      </el-form>
      <div class="dialog-actions">
        <el-button @click="showTransferDialog = false" class="cancel-btn">
          <i class="el-icon-close"></i>
          取消
        </el-button>
        <el-button type="primary" @click="onTransferSubmit" class="confirm-btn">
          <i class="el-icon-check"></i>
          确定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// 导入API函数
import { getApprovalTodoTasks, approveTask, transferTask } from '../../api';
// 导入审批处理对话框组件
import ApprovalProcessDialog from './ApprovalProcessDialog.vue';
// 导入权限混入
import permissionMixin from '../../utils/permissionMixin';

export default {
  name: 'ApprovalTodoList',
  components: { ApprovalProcessDialog },
  mixins: [permissionMixin],
  data() {
    return {
      list: [],
      page: 1,
      pageSize: 10,
      total: 0,
      isSearchExpanded: false,
      search: {
        assignee: '',
        taskStatus: '',
        businessCode: '',
        businessTitle: '',
        currentNode: ''
      },
      searchRange: [],
      // 节点选项（示例数据，实际应从API获取）
      nodeOptions: [
        { value: '节点1', label: '节点1 - 项目经理审批' },
        { value: '节点2', label: '节点2 - 部门经理审批' },
        { value: '节点3', label: '节点3 - 总监审批' },
        { value: '节点4', label: '节点4 - 总经理审批' }
      ],
      // 用户选项（示例数据，实际应从API获取）
      userOptions: [
        { value: 'user001', label: '张三' },
        { value: 'user002', label: '李四' },
        { value: 'user003', label: '王五' },
        { value: 'user004', label: '赵六' }
      ],
      showProcessDialog: false,
      showTransferDialog: false,
      selectedTask: null,
      transferForm: {
        taskId: '',
        currentAssignee: '',
        newAssignee: '',
        remark: ''
      },
      pollingTimer: null,
      loading: false, // 加载状态
      loadError: false, // 加载错误状态
      errorMessage: '' // 错误信息
    };
  },
  mounted() {
    this.fetchList();
    // 启动轮询机制，每30秒刷新一次待办列表
    this.startPolling();
  },
  beforeDestroy() {
    // 组件销毁前清除轮询定时器
    this.stopPolling();
  },
  computed: {
    canEdit() {
      // 使用权限混入的hasPermission方法进行权限检查
      return this.hasPermission('approval:task:edit');
    }
  },
  methods: {
    // 获取当前登录用户编号
    getCurrentUserNum() {
      try {
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        return user.userNum || '';
      } catch (e) {
        return '';
      }
    },
    // 获取待办任务列表
    async fetchList(page = 1) {
      this.page = page;
      this.loading = true;
      this.loadError = false;
      this.errorMessage = '';
      
      const currentUserNum = this.getCurrentUserNum();
      const params = {
        page: this.page,
        size: this.pageSize,
        taskStatus: this.search.taskStatus || undefined,
        businessCode: this.search.businessCode || undefined,
        businessTitle: this.search.businessTitle || undefined,
        currentNode: this.search.currentNode || undefined
      };
      if (this.searchRange && this.searchRange.length === 2) {
        params.startTime = this.formatDate(this.searchRange[0]);
        params.endTime = this.formatDate(this.searchRange[1]);
      }
      try {
        // 使用正确的API函数调用，传递当前用户编号作为assigneeNum
        const data = await getApprovalTodoTasks(currentUserNum, params);
        this.list = data.records || [];
        this.total = data.total || 0;
      } catch (e) {
        this.loadError = true;
        this.errorMessage = '待办任务列表加载失败';
        this.$message && this.$message.error(this.errorMessage);
      } finally {
        this.loading = false;
      }
    },
    // 日期格式化
    formatDate(d) {
      if (!d) return undefined;
      const t = new Date(d);
      const yyyy = t.getFullYear();
      const mm = String(t.getMonth() + 1).padStart(2, '0');
      const dd = String(t.getDate()).padStart(2, '0');
      return `${yyyy}-${mm}-${dd} 00:00:00`;
    },
    // 日期时间格式化
    formatDateTime(v) {
      if (!v) return '';
      try {
        const s = String(v);
        return s.replace('T', ' ');
      } catch (e) {
        return String(v);
      }
    },
    // 重置搜索
    resetSearch() {
      this.search = {
        assignee: '',
        taskStatus: '',
        businessCode: '',
        businessTitle: '',
        currentNode: ''
      };
      this.searchRange = [];
    },
    // 切换搜索展开状态
    toggleSearchExpanded() {
      this.isSearchExpanded = !this.isSearchExpanded;
    },
    // 分页处理
    handleSizeChange(val) {
      this.page = 1;
      this.pageSize = val;
      this.fetchList(this.page);
    },
    handleCurrentChange(val) {
      this.page = val;
      this.fetchList(this.page);
    },
    // 节点标签类型
    getNodeTagType(node) {
      switch (node) {
        case '节点1':
          return 'info';
        case '节点2':
          return 'warning';
        case '节点3':
          return 'primary';
        case '节点4':
          return 'danger';
        default:
          return '';
      }
    },
    // 业务类型标签类型
    getBusinessTypeTagType(type) {
      switch (type) {
        case 'CHANGE_RECORD':
          return 'info';
        case 'PROJECT':
          return 'warning';
        case 'REQUIREMENT':
          return 'primary';
        default:
          return '';
      }
    },
    
    // 判断是否临近过期（24小时内过期）
    isExpireWarning(expireTime) {
      if (!expireTime) {
        return false;
      }
      try {
        const expireDate = new Date(expireTime);
        const now = new Date();
        const diffMs = expireDate - now;
        // 24小时内过期显示警告
        return diffMs > 0 && diffMs < 24 * 60 * 60 * 1000;
      } catch (e) {
        return false;
      }
    },
    // 处理审批
    handleApprove(row) {
      this.selectedTask = row;
      this.showProcessDialog = true;
    },
    // 转办任务
    handleTransfer(row) {
      this.selectedTask = row;
      this.transferForm = {
        taskId: row.taskId,
        currentAssignee: row.assignee,
        newAssignee: '',
        remark: ''
      };
      this.showTransferDialog = true;
    },
    // 查看详情
    handleDetail(row) {
      // 跳转到详情页面或打开详情对话框
      this.$message && this.$message.info('查看详情功能待实现');
    },
    // 审批处理成功回调
    onProcessSuccess() {
      this.showProcessDialog = false;
      this.fetchList(this.page);
      this.$message && this.$message.success('审批处理成功');
    },
    // 转办提交
    async onTransferSubmit() {
      try {
        await transferTask(this.transferForm.taskId, this.transferForm.newAssignee, this.transferForm.remark);
        this.showTransferDialog = false;
        this.fetchList(this.page);
        this.$message && this.$message.success('任务转办成功');
      } catch (e) {
        this.$message && this.$message.error('任务转办失败');
      }
    },
    
    // 启动轮询机制
    startPolling() {
      // 轮询间隔设置为30秒，符合项目标准
      this.pollingTimer = setInterval(() => {
        this.fetchList(this.page);
      }, 30000);
    },
    
    // 停止轮询机制
    stopPolling() {
      if (this.pollingTimer) {
        clearInterval(this.pollingTimer);
        this.pollingTimer = null;
      }
    }
  }
};
</script>

<style scoped>
/* Page container */
.approval-todo-page {
  width: 100%;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eaf6 100%);
  min-height: calc(100vh - 60px);
}

/* Search bar styling */
.search-bar {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 4px 16px rgba(123, 104, 238, 0.08);
  border: 1px solid rgba(123, 104, 238, 0.1);
}

/* Search container main layout */
.search-container {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  width: 100%;
}

/* Search input area */
.search-inputs-wrapper {
  display: flex;
  flex: 1;
  min-width: 0;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

/* Button group area */
.search-buttons {
  display: flex;
  gap: 12px;
  align-items: center;
}

/* Search input styling */
.search-input {
  width: 160px;
  min-width: 140px;
}

.search-input ::v-deep .el-input__inner {
  border-radius: 8px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
  font-size: 14px;
  padding: 0 15px 0 35px;
  height: 36px;
  line-height: 36px;
}

.search-input ::v-deep .el-input__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.search-input ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

.search-input ::v-deep .el-input__prefix {
  left: 10px;
  color: #7B68EE;
}

/* Search select styling */
.search-select {
  width: 160px;
  min-width: 140px;
}

.search-select ::v-deep .el-input__inner {
  border-radius: 8px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
  font-size: 14px;
  padding: 0 15px 0 35px;
  height: 36px;
  line-height: 36px;
}

.search-select ::v-deep .el-input__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.search-select ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

.search-select ::v-deep .el-input__suffix {
  right: 10px;
}

/* Search date styling */
.search-date {
  width: 420px;
  min-width: 300px;
}

.search-date ::v-deep .el-input__inner {
  border-radius: 8px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
}

.search-date ::v-deep .el-input__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.search-date ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

/* Expand/collapse button styling */
.expand-btn {
  color: #7B68EE;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  white-space: nowrap;
  padding: 8px 12px;
  border-radius: 6px;
}

.expand-btn:hover {
  color: #9370DB;
  background: rgba(123, 104, 238, 0.1);
  transform: translateY(-1px);
}

.expand-btn .el-icon-arrow-down {
  margin-left: 4px;
  transition: transform 0.3s ease;
}

.expand-btn .el-icon-arrow-down.rotate-180 {
  transform: rotate(180deg);
}

/* Action button styling */
.action-btn {
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 600;
  transition: all 0.3s ease;
  border: none;
  font-size: 14px;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* Reset button styling */
.reset-btn {
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eaf6 100%);
  color: #606266;
  border: 1px solid #d4d4e8;
}

.reset-btn:hover {
  background: linear-gradient(135deg, #e8eaf6 0%, #d5d8e0 100%);
  color: #409EFF;
  border-color: #409EFF;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.action-btn.el-button--primary {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  color: #fff;
}

.action-btn.el-button--primary:hover {
  background: linear-gradient(135deg, #9370DB 0%, #BA55D3 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(123, 104, 238, 0.3);
}

.search-btn {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  color: #fff;
}

/* Table container */
.table-container {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(123, 104, 238, 0.08);
  overflow: auto;
  border: 1px solid rgba(123, 104, 238, 0.1);
  margin-bottom: 20px;
  max-width: 100%;
  width: 100%;
}

/* Data table basic styling */
.data-table {
  width: 100%;
  min-width: 1200px;
  table-layout: auto;
  border-radius: 8px;
  overflow: hidden;
}

.data-table ::v-deep .el-table__header-wrapper {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
}

.data-table ::v-deep .el-table__header th {
  background: transparent;
  color: #ffffff;
  font-weight: 600;
  font-size: 14px;
  border-color: rgba(255, 255, 255, 0.2);
  padding: 14px 12px;
}

.data-table ::v-deep .el-table__body tr {
  transition: all 0.2s ease;
}

.data-table ::v-deep .el-table__body tr:hover > td {
  background: linear-gradient(135deg, #fafbff 0%, #f5f7ff 100%);
}

.data-table ::v-deep .el-table__body td {
  padding: 12px;
  border-color: #e8e8f0;
  color: #1a1a2e;
  font-size: 14px;
}

.data-table ::v-deep .el-table__body tr:nth-child(even) {
  background: #fafbff;
}

.data-table ::v-deep .el-table__body tr:nth-child(odd) {
  background: #ffffff;
}

/* Table button styling */
.table-btn {
  border-radius: 6px;
  padding: 6px 14px;
  font-size: 12px;
  font-weight: 500;
  border: 1px solid #d4d4e8;
  background: #fff;
  color: #1A1A2E;
  transition: all 0.3s ease;
  margin-right: 6px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.table-btn:hover {
  border-color: #7B68EE;
  color: #7B68EE;
  background: linear-gradient(135deg, #f5f7ff 0%, #e8eaf6 100%);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(123, 104, 238, 0.2);
}

.table-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.transfer-btn:hover {
  border-color: #409EFF;
  color: #409EFF;
  background: rgba(64, 158, 255, 0.1);
}

.detail-btn:hover {
  border-color: #9370DB;
  color: #9370DB;
  background: rgba(147, 112, 219, 0.1);
}

/* Tag styling in table */
.node-tag,
.business-tag,
.status-tag {
  border-radius: 4px;
  padding: 4px 10px;
  font-weight: 500;
  border: none;
  font-size: 12px;
}

.node-tag.el-tag--info {
  background: linear-gradient(135deg, #909399 0%, #606266 100%);
  color: #ffffff;
}

.node-tag.el-tag--warning {
  background: linear-gradient(135deg, #ff9800 0%, #ff6b00 100%);
  color: #ffffff;
}

.node-tag.el-tag--primary {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #ffffff;
}

.node-tag.el-tag--danger {
  background: linear-gradient(135deg, #f56c6c 0%, #ff6b6b 100%);
  color: #ffffff;
}

.business-tag.el-tag--info {
  background: linear-gradient(135deg, #909399 0%, #606266 100%);
  color: #ffffff;
}

.business-tag.el-tag--warning {
  background: linear-gradient(135deg, #ff9800 0%, #ff6b00 100%);
  color: #ffffff;
}

.business-tag.el-tag--primary {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #ffffff;
}

.status-tag.el-tag--warning {
  background: linear-gradient(135deg, #ff9800 0%, #ff6b00 100%);
  color: #ffffff;
}

.status-tag.el-tag--success {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: #ffffff;
}

/* Expire warning styling */
.data-table .expire-warning {
  color: #f56c6c;
  font-weight: 600;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}

/* Empty data and load error styling */
.empty-data,
.load-error {
  text-align: center;
  padding: 60px 0;
  color: #909399;
  font-size: 14px;
}

.empty-icon,
.error-icon {
  font-size: 48px;
  margin-bottom: 16px;
  display: block;
  color: #7B68EE;
}

.empty-text,
.error-text {
  font-size: 15px;
  font-weight: 500;
  color: #606266;
}

.load-error .el-button {
  margin-top: 20px;
}

.retry-btn {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  border: none;
}

.retry-btn:hover {
  background: linear-gradient(135deg, #9370DB 0%, #BA55D3 100%);
  box-shadow: 0 4px 12px rgba(123, 104, 238, 0.3);
}

/* Pagination styling */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding: 16px 20px;
  width: 100%;
  box-sizing: border-box;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(123, 104, 238, 0.06);
}

/* Transfer dialog styling */
.transfer-dialog ::v-deep .el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.transfer-dialog ::v-deep .el-dialog__header {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  color: #ffffff;
  padding: 16px 20px;
}

.transfer-dialog ::v-deep .el-dialog__title {
  color: #ffffff;
  font-weight: 600;
}

.transfer-dialog ::v-deep .el-dialog__body {
  padding: 24px 20px;
  background: #ffffff;
}

.transfer-form ::v-deep .el-form-item__label {
  color: #5a4fcf;
  font-weight: 600;
  font-size: 14px;
}

.readonly-input ::v-deep .el-input__inner {
  background: #f5f7fa;
  color: #909399;
  cursor: not-allowed;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}

.select-input ::v-deep .el-input__inner {
  border-radius: 6px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
}

.select-input ::v-deep .el-input__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.select-input ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

.textarea-input ::v-deep .el-textarea__inner {
  border-radius: 6px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
  font-size: 14px;
  padding: 10px 15px;
  line-height: 1.6;
  resize: vertical;
}

.textarea-input ::v-deep .el-textarea__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.textarea-input ::v-deep .el-textarea__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e8e8f0;
}

.cancel-btn {
  background: #ffffff;
  border: 1px solid #d4d4e8;
  color: #606266;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  border-color: #f56c6c;
  color: #f56c6c;
  background: linear-gradient(135deg, #fef0f0 0%, #fde2e2 100%);
}

.confirm-btn {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  border: none;
  color: #ffffff;
  transition: all 0.3s ease;
}

.confirm-btn:hover {
  background: linear-gradient(135deg, #9370DB 0%, #BA55D3 100%);
  box-shadow: 0 4px 12px rgba(123, 104, 238, 0.3);
  transform: translateY(-1px);
}

/* Responsive design */
@media screen and (max-width: 1200px) {
  .approval-todo-page {
    padding: 16px;
  }

  .search-bar,
  .table-container {
    padding: 16px;
  }

  .search-input,
  .search-select {
    width: 140px;
    min-width: 120px;
  }

  .search-date {
    width: 380px;
    min-width: 280px;
  }
}

@media screen and (max-width: 768px) {
  .approval-todo-page {
    padding: 12px;
  }

  .search-bar,
  .table-container {
    padding: 12px;
  }

  .search-container {
    flex-direction: column;
    align-items: stretch;
  }

  .search-inputs-wrapper {
    width: 100%;
    margin-bottom: 12px;
  }

  .search-input,
  .search-select {
    width: calc(50% - 6px);
    min-width: calc(50% - 6px);
  }

  .search-date {
    width: 100%;
    min-width: unset;
  }

  .search-buttons {
    justify-content: flex-start;
    width: 100%;
  }

  .action-btn {
    flex: 1;
    min-width: 80px;
  }

  .table-btn {
    padding: 4px 8px;
    font-size: 11px;
  }
}

@media screen and (max-width: 480px) {
  .approval-todo-page {
    padding: 8px;
  }

  .search-bar,
  .table-container {
    padding: 8px;
  }

  .search-input,
  .search-select {
    width: 100%;
    min-width: unset;
  }

  .expand-btn {
    width: 100%;
    text-align: center;
  }

  .table-btn {
    padding: 4px 6px;
    font-size: 10px;
    margin-right: 4px;
  }

  .data-table ::v-deep .el-table__header th {
    font-size: 12px;
    padding: 10px 8px;
  }

  .data-table ::v-deep .el-table__body td {
    font-size: 12px;
    padding: 8px;
  }
}

/* Animation */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.search-bar,
.table-container {
  animation: fadeIn 0.4s ease-out;
}

/* Scrollbar styling */
.data-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.data-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar-track {
  background: #f5f7fa;
  border-radius: 4px;
}

.data-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  border-radius: 4px;
}

.data-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #9370DB 0%, #BA55D3 100%);
}
</style>