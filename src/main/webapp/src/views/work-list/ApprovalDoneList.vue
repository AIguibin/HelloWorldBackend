<template>
  <div class="approval-done-page">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <div class="search-container">
        <!-- 搜索输入区域 -->
        <div class="search-inputs-wrapper">
          <!-- 默认显示的搜索项 -->
          <el-input v-model="search.assignee" placeholder="审批人" class="search-input" />
          <el-select v-model="search.taskStatus" placeholder="任务状态" class="search-select">
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
          <el-input v-model="search.businessCode" placeholder="业务编码" class="search-input" />
          <el-input v-model="search.businessTitle" placeholder="业务标题" class="search-input" />
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
          <el-button @click="resetSearch" class="action-btn reset-btn">重置</el-button>
          <el-button type="primary" @click="fetchList(1)" class="action-btn">查询</el-button>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table :data="list" stripe class="data-table" :fit="true" border v-loading="loading">
        <el-table-column width="180" label="操作" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleDetail(scope.row)" class="table-btn detail-btn" v-permission="'approval:task:detail'">查看详情</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="taskId" label="任务ID" width="160" :show-overflow-tooltip="true" />
        <el-table-column prop="businessCode" label="业务编码" width="140" />
        <el-table-column prop="businessTitle" label="业务标题" width="200" :show-overflow-tooltip="true" />
        <el-table-column prop="currentNode" label="当前节点" width="120" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <el-tag :type="getNodeTagType(scope.row.currentNode)">{{ scope.row.currentNode || '未分配' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assignTime" label="接收时间" width="180">
          <template slot-scope="scope">{{ formatDateTime(scope.row.assignTime) }}</template>
        </el-table-column>
        <el-table-column prop="completeTime" label="完成时间" width="180">
          <template slot-scope="scope">{{ formatDateTime(scope.row.completeTime) }}</template>
        </el-table-column>
        <el-table-column prop="assignee" label="审批人" width="140" :show-overflow-tooltip="true" />
        <el-table-column prop="businessType" label="业务类型" width="140" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <el-tag :type="getBusinessTypeTagType(scope.row.businessType)">{{ scope.row.businessType || '变更记录' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approvalResult" label="审批结果" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.approvalResult === 'APPROVED' ? 'success' : 'danger'">
              {{ scope.row.approvalResult === 'APPROVED' ? '已通过' : scope.row.approvalResult === 'REJECTED' ? '已拒绝' : '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="changeDesc" label="变更描述" show-overflow-tooltip="true"/>
        <el-table-column prop="operationDescription" label="操作描述" width="180" show-overflow-tooltip="true"/>
        <!-- 空数据提示 -->
        <template slot="empty">
          <div class="empty-data">
            <i class="el-icon-info"></i>
            <span>暂无已办任务</span>
          </div>
        </template>
        <!-- 加载失败提示 -->
        <template v-if="loadError" slot="empty">
          <div class="load-error">
            <i class="el-icon-error"></i>
            <span>{{ errorMessage }}</span>
            <el-button type="primary" size="small" @click="fetchList">重新加载</el-button>
          </div>
        </template>
      </el-table>
    </div>

    <div class="pagination-container">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="page" :page-sizes="[5, 10, 20, 30]" :page-size="pageSize" layout="total,sizes,prev,pager,next,jumper" :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
// 导入API函数
import { getApprovalProcessedTasks } from '../../api';
// 导入权限混入
import permissionMixin from '../../utils/permissionMixin';

export default {
  name: 'ApprovalDoneList',
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
      loading: false, // 加载状态
      loadError: false, // 加载错误状态
      errorMessage: '' // 错误信息
    };
  },
  mounted() {
    this.fetchList();
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
    // 获取已办任务列表
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
        // 使用正确的API函数调用，传递当前用户编号作为operatorNum
        const data = await getApprovalProcessedTasks(currentUserNum, params);
        this.list = data.records || [];
        this.total = data.total || 0;
      } catch (e) {
        this.loadError = true;
        this.errorMessage = '已办任务列表加载失败';
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
    // 查看详情
    handleDetail(row) {
      // 跳转到详情页面或打开详情对话框
      this.$message && this.$message.info('查看详情功能待实现');
    }
  }
};
</script>

<style scoped>
/* 复用现有样式 */
.approval-done-page {
  padding: 0;
  margin: 0 auto;
  padding: 0 8px;
}

/* 搜索栏样式 */
.search-bar {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

/* 搜索容器主布局 */
.search-container {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  width: 100%;
}

/* 搜索输入区域 */
.search-inputs-wrapper {
  display: flex;
  flex: 1;
  min-width: 0;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}

/* 按钮组区域 */
.search-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

.search-input {
  width: 140px;
  min-width: 120px;
}

.search-input ::v-deep .el-input__inner {
  border-radius: 8px;
  border: 1px solid #EAEAEA;
  transition: all 0.2s ease;
  font-size: 14px;
}

.search-input ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

.search-date {
  width: 400px;
  min-width: 280px;
}

.search-date ::v-deep .el-input__inner {
  border-radius: 8px;
  border: 1px solid #EAEAEA;
  transition: all 0.2s ease;
}

.search-date ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

.search-select {
  width: 140px;
  min-width: 120px;
}

.search-select ::v-deep .el-input__inner {
  border-radius: 8px;
  border: 1px solid #EAEAEA;
  transition: all 0.2s ease;
}

.search-select ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

/* 展开/收起按钮样式 */
.expand-btn {
  color: #7B68EE;
  font-size: 14px;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.expand-btn:hover {
  color: #9370DB;
  background: rgba(123, 104, 238, 0.1);
}

.expand-btn .el-icon-arrow-down {
  margin-left: 4px;
  transition: transform 0.2s ease;
}

.expand-btn .el-icon-arrow-down.rotate-180 {
  transform: rotate(180deg);
}

.action-btn {
  border-radius: 8px;
  padding: 10px 16px;
  font-weight: 500;
  transition: all 0.2s ease;
  border: none;
  font-size: 14px;
  white-space: nowrap;
}

/* 重置按钮样式 */
.reset-btn {
  background: #f0f0f0;
  color: #606266;
}

.reset-btn:hover {
  background: #e6e6e6;
  color: #409EFF;
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

/* 表格容器 */
.table-container {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  overflow: auto;
  border: 1px solid rgba(255, 255, 255, 0.5);
  margin-bottom: 20px;
  max-width: 100%;
  width: 100%;
}

/* 数据表格基础样式 */
.data-table {
  width: 100%;
  min-width: 1200px;
  table-layout: auto;
}

/* 表格按钮 */
.table-btn {
  border-radius: 6px;
  padding: 6px 12px;
  font-size: 12px;
  border: 1px solid #EAEAEA;
  background: #fff;
  color: #1A1A2E;
  transition: all 0.2s ease;
  margin-right: 6px;
}

.table-btn:hover {
  border-color: #7B68EE;
  color: #7B68EE;
  background: linear-gradient(90deg, rgba(255, 182, 193, 0.15) 0%, rgba(221, 160, 221, 0.15) 100%);
}

.detail-btn:hover {
  border-color: #9370DB;
  color: #9370DB;
  background: rgba(221, 160, 221, 0.15);
}

/* 分页样式 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding: 16px 0;
  width: 100%;
  box-sizing: border-box;
}

/* 响应式设计 */
@media screen and (max-width: 1200px) {
  .search-container {
    flex-direction: column;
    align-items: stretch;
  }

  .search-inputs-wrapper {
    width: 100%;
    margin-bottom: 12px;
  }

  .search-buttons {
    justify-content: flex-start;
  }
}

@media screen and (max-width: 768px) {
  .search-input,
  .search-select {
    width: calc(50% - 4px);
    min-width: calc(50% - 4px);
  }

  .search-date {
    width: 100%;
    min-width: unset;
  }

  .search-buttons {
    flex-wrap: wrap;
  }

  .action-btn {
    flex: 1;
    min-width: 80px;
  }
}

@media screen and (max-width: 480px) {
  .search-input,
  .search-select {
    width: 100%;
    min-width: unset;
  }

  .expand-btn {
    width: 100%;
    text-align: center;
  }
}

/* 确保表格内容清晰可读 */
.data-table ::v-deep .el-table__body td {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 400;
  line-height: 1.4;
}

.data-table ::v-deep .el-table__header th {
  font-weight: 600;
  text-align: center;
  background: #ffffff;
}

.data-table ::v-deep .el-table__body tr {
  transition: all 0.2s ease;
}

.data-table ::v-deep .el-table__body tr:hover {
  background: linear-gradient(90deg, rgba(255, 182, 193, 0.1) 0%, rgba(221, 160, 221, 0.1) 100%) !important;
}

.data-table ::v-deep .el-table__row {
  border-bottom: 1px solid #EAEAEA;
}

.data-table ::v-deep .el-table__row:nth-child(even) {
  background-color: #fafafa;
}

/* 表格边框和分隔线清晰显示 */
.data-table ::v-deep .el-table {
  border: 1px solid #EAEAEA;
  border-radius: 12px;
  overflow: hidden;
}

.data-table ::v-deep .el-table__inner-wrapper {
  border-radius: 12px;
  overflow: hidden;
}

.data-table ::v-deep .el-table__header-wrapper {
  border-bottom: 2px solid #EAEAEA;
}

.data-table ::v-deep .el-table__header th {
  border-right: 1px solid #EAEAEA;
}

.data-table ::v-deep .el-table__body td {
  border-right: 1px solid #F5F5F5;
}

/* Tag标签样式优化 */
.data-table ::v-deep .el-tag {
  border-radius: 6px;
  border: none;
  padding: 4px 12px;
  font-weight: 500;
}

.data-table ::v-deep .el-tag--success {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.data-table ::v-deep .el-tag--warning {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.data-table ::v-deep .el-tag--info {
  background: rgba(144, 147, 153, 0.1);
  color: #909399;
}

.data-table ::v-deep .el-tag--danger {
  background: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
}

.data-table ::v-deep .el-tag--primary {
  background: rgba(64, 158, 255, 0.1);
  color: #409EFF;
}

/* 空数据和加载错误提示样式 */
.empty-data, .load-error {
  text-align: center;
  padding: 40px 0;
  color: #909399;
  font-size: 14px;
}

.empty-data i, .load-error i {
  font-size: 32px;
  margin-bottom: 16px;
  display: block;
}

.load-error .el-button {
  margin-top: 16px;
}
</style>