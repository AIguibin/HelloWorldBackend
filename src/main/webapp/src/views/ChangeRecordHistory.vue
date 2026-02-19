<template>
  <div class="change-record-history">
    <el-card shadow="hover" class="history-card">
      <div slot="header" class="card-header">
        <div class="header-left">
          <h2 class="card-title">
            <i class="el-icon-time"></i>
            变更历史
          </h2>
          <span class="card-subtitle">记录ID：{{ recordId }}</span>
        </div>
        <div class="header-actions">
          <el-button @click="$router.push('/change-records')" class="back-btn">
            <i class="el-icon-back"></i>
            返回列表
          </el-button>
        </div>
      </div>
      <div class="history-wrapper">
        <el-table :data="list" border stripe size="medium" class="history-table">
          <el-table-column prop="operationTime" label="操作时间" width="150" show-overflow-tooltip="true"/>
          <el-table-column prop="operationUserName" label="操作人" width="120" show-overflow-tooltip="true"/>
          <el-table-column prop="operationType" label="操作类型" width="120" />
          <el-table-column prop="version" label="版本号" width="150" show-overflow-tooltip="true"/>
          <el-table-column prop="defectNumber" label="缺陷编号" width="140" />
          <el-table-column prop="groupName" label="组别" width="140" />
          <el-table-column prop="developerName" label="开发负责人" width="100" />
          <el-table-column prop="developType" label="开发类别" width="100" />
          <el-table-column prop="serviceName" label="服务名称" width="140" />
          <el-table-column prop="releaseDate" label="发版日期" width="130" />
          <el-table-column label="当前状态" width="90">
            <template slot-scope="scope">
              <el-tag :type="statusTagType(scope.row.currentStatus)" class="status-tag">{{ getStatusName(scope.row.currentStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="changeDesc" label="变更描述" show-overflow-tooltip="true"/>
          <el-table-column prop="operationDescription" label="操作描述" width="180" show-overflow-tooltip="true"/>
          <el-table-column prop="sourceBranch" label="源分支" width="140" />        
          <el-table-column prop="targetBranch" label="目标分支" width="140" />
          <el-table-column prop="problemDescription" label="问题描述" width="180" show-overflow-tooltip="true"/>
          <el-table-column prop="impactAnalysis" label="问题影响分析" width="180" show-overflow-tooltip="true"/>
          <el-table-column prop="solutionDescription" label="解决方案" width="180" show-overflow-tooltip="true"/>
          <el-table-column prop="codeList" label="代码清单" width="180" show-overflow-tooltip="true"/>
          <el-table-column prop="shellPath" label="脚本清单" width="180" show-overflow-tooltip="true"/>
          <el-table-column prop="configList" label="配置说明" width="180" show-overflow-tooltip="true"/>
          <el-table-column prop="involveExternalSystem" label="涉及外部系统" width="120">
            <template slot-scope="scope">
              <el-tag :type="scope.row.involveExternalSystem ? 'warning' : 'info'" class="info-tag">{{ scope.row.involveExternalSystem ? '是' : '否' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="includeShell" label="是否包含脚本" width="120">
            <template slot-scope="scope">
              <el-tag :type="scope.row.includeShell ? 'warning' : 'info'" class="info-tag">{{ scope.row.includeShell ? '是' : '否' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="crossService" label="是否跨服务" width="120">
            <template slot-scope="scope">
              <el-tag :type="scope.row.crossService ? 'warning' : 'info'" class="info-tag">{{ scope.row.crossService ? '是' : '否' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注说明" show-overflow-tooltip="true" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getChangeRecordHistory } from '../api';

export default {
  name: 'ChangeRecordHistory',
  data() {
    return { list: [], page: 1, pageSize: 10, total: 0 };
  },
  computed: {
    recordId() { return this.$route.params.id; }
  },
  created() { this.fetchList(); },
  methods: {
    async fetchList(toPage) {
      if (toPage) this.page = toPage;
      const data = await getChangeRecordHistory(this.recordId, { page: this.page, size: this.pageSize });
      this.list = data.records || [];
      this.total = data.total || 0;
    },
    statusTagType(s) {
      switch (s) {
        case '01': return 'warning'; // 草稿态
        case '02': return 'warning'; // 已提请
        case '03': return 'primary'; // 审批中
        case '04': return 'success'; // 已合并
        case '05': return 'success'; // 已部署
        case '06': return 'info';    // 测试中
        case '07': return 'success'; // 已评审
        case '08': return 'warning'; // 待投产
        case '09': return 'success'; // 已投产
        default: return '';
      }
    },
    // 获取状态名称
    getStatusName(status) {
      switch (status) {
        case '01': return '草稿态';
        case '02': return '已提请';
        case '03': return '审批中';
        case '04': return '已合并';
        case '05': return '已部署';
        case '06': return '测试中';
        case '07': return '已评审';
        case '08': return '待投产';
        case '09': return '已投产';
        default: return '未知状态';
      }
    },
    formatDateTime(v) {
      if (!v) return '';
      try {
        const s = String(v);
        return s.replace('T', ' ');
      } catch (e) { return String(v); }
    }
  }
};
</script>

<style scoped>
.change-record-history {
  width: 100%;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eaf6 100%);
  min-height: calc(100vh - 60px);
}

.history-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(123, 104, 238, 0.08);
}

/* Card header styling */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0;
}

.card-title i {
  font-size: 20px;
  color: #7B68EE;
}

.card-subtitle {
  font-size: 13px;
  color: #909399;
  font-weight: 500;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 6px;
  border: 1px solid #d4d4e8;
  background: #ffffff;
  color: #5a4fcf;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  border-color: #7B68EE;
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(123, 104, 238, 0.3);
  transform: translateY(-2px);
}

.back-btn:active {
  transform: translateY(0);
}

/* History wrapper */
.history-wrapper {
  padding: 0;
}

/* Table styling */
.history-table {
  border-radius: 8px;
  overflow: hidden;
}

.history-table ::v-deep .el-table__header-wrapper {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
}

.history-table ::v-deep .el-table__header th {
  background: transparent;
  color: #ffffff;
  font-weight: 600;
  font-size: 14px;
  border-color: rgba(255, 255, 255, 0.2);
  padding: 14px 12px;
}

.history-table ::v-deep .el-table__body tr {
  transition: all 0.2s ease;
}

.history-table ::v-deep .el-table__body tr:hover > td {
  background: linear-gradient(135deg, #fafbff 0%, #f5f7ff 100%);
}

.history-table ::v-deep .el-table__body td {
  padding: 12px;
  border-color: #e8e8f0;
  color: #1a1a2e;
  font-size: 14px;
}

.history-table ::v-deep .el-table__body tr:nth-child(even) {
  background: #fafbff;
}

.history-table ::v-deep .el-table__body tr:nth-child(odd) {
  background: #ffffff;
}

/* Tag styling */
.status-tag,
.info-tag {
  border-radius: 4px;
  padding: 4px 12px;
  font-size: 13px;
  font-weight: 500;
  border: none;
}

.status-tag.el-tag--warning {
  background: linear-gradient(135deg, #ff9800 0%, #ff6b00 100%);
  color: #ffffff;
}

.status-tag.el-tag--info {
  background: linear-gradient(135deg, #909399 0%, #606266 100%);
  color: #ffffff;
}

.status-tag.el-tag--primary {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #ffffff;
}

.status-tag.el-tag--success {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: #ffffff;
}

.info-tag.el-tag--warning {
  background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
  color: #ff6b00;
  border: 1px solid #ff9800;
}

.info-tag.el-tag--info {
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eaf6 100%);
  color: #606266;
  border: 1px solid #dcdfe6;
}

/* Card styling */
.change-record-history ::v-deep .el-card__header {
  padding: 20px 24px;
  border-bottom: 1px solid #e8e8f0;
  background: #ffffff;
}

.change-record-history ::v-deep .el-card__body {
  padding: 24px;
}

/* Empty state */
.history-table ::v-deep .el-table__empty-block {
  min-height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.history-table ::v-deep .el-table__empty-text {
  font-size: 14px;
  color: #909399;
}

/* Responsive design */
@media (max-width: 1200px) {
  .change-record-history {
    padding: 16px;
  }

  .change-record-history ::v-deep .el-card__header {
    padding: 16px 20px;
  }

  .change-record-history ::v-deep .el-card__body {
    padding: 20px;
  }

  .card-title {
    font-size: 16px;
  }

  .card-title i {
    font-size: 18px;
  }

  .card-subtitle {
    font-size: 12px;
  }

  .history-table ::v-deep .el-table__header th {
    font-size: 13px;
    padding: 12px 10px;
  }

  .history-table ::v-deep .el-table__body td {
    font-size: 13px;
    padding: 10px;
  }
}

@media (max-width: 768px) {
  .change-record-history {
    padding: 12px;
  }

  .change-record-history ::v-deep .el-card__header {
    padding: 12px 16px;
  }

  .change-record-history ::v-deep .el-card__body {
    padding: 16px;
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .header-actions {
    width: 100%;
  }

  .back-btn {
    width: 100%;
    justify-content: center;
  }

  .card-title {
    font-size: 15px;
  }

  .card-title i {
    font-size: 16px;
  }

  .card-subtitle {
    font-size: 12px;
  }

  .history-table {
    font-size: 12px;
  }

  .history-table ::v-deep .el-table__header th {
    font-size: 12px;
    padding: 10px 8px;
  }

  .history-table ::v-deep .el-table__body td {
    font-size: 12px;
    padding: 8px;
  }

  .status-tag,
  .info-tag {
    font-size: 12px;
    padding: 3px 10px;
  }
}

@media (max-width: 480px) {
  .change-record-history {
    padding: 8px;
  }

  .change-record-history ::v-deep .el-card__header {
    padding: 10px 12px;
  }

  .change-record-history ::v-deep .el-card__body {
    padding: 12px;
  }

  .card-title {
    font-size: 14px;
  }

  .card-title i {
    font-size: 15px;
  }

  .card-subtitle {
    font-size: 11px;
  }

  .history-table ::v-deep .el-table__header th {
    font-size: 11px;
    padding: 8px 6px;
  }

  .history-table ::v-deep .el-table__body td {
    font-size: 11px;
    padding: 6px;
  }

  .back-btn {
    font-size: 13px;
    padding: 6px 12px;
  }

  .back-btn i {
    font-size: 14px;
  }
}

/* Print styles */
@media print {
  .change-record-history {
    background: #ffffff;
    padding: 0;
  }

  .history-card {
    box-shadow: none;
  }

  .back-btn {
    display: none;
  }
}

/* Animation for table rows */
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.history-table ::v-deep .el-table__body tr {
  animation: slideIn 0.3s ease-out;
}

/* Scrollbar styling */
.history-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.history-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar-track {
  background: #f5f7fa;
  border-radius: 4px;
}

.history-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  border-radius: 4px;
}

.history-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #9370DB 0%, #BA55D3 100%);
}
</style>