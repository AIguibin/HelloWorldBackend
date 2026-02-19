<template>
  <div class="change-record-detail">
    <el-card shadow="hover" class="detail-card">
      <div slot="header" class="card-header">
        <div class="header-left">
          <h2 class="card-title">
            <i class="el-icon-document"></i>
            变更记录详情
          </h2>
          <span class="card-subtitle">{{ detail.defectNumber || '-' }}</span>
        </div>
        <div class="header-actions">
          <el-button @click="$router.push('/change-records')" class="back-btn">
            <i class="el-icon-back"></i>
            返回列表
          </el-button>
        </div>
      </div>
      
      <div class="detail-content">
        <el-descriptions :column="3" border class="descriptions-section">
          <template slot="title">
            <div class="section-title">
              <i class="el-icon-info"></i>
              <span>基本信息</span>
            </div>
          </template>
          <el-descriptions-item label="版本号">{{ detail.version || '-' }}</el-descriptions-item>
          <el-descriptions-item label="服务名称">{{ detail.serviceName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="组别">{{ detail.groupName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="开发负责人">{{ detail.developerName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="发版日期">{{ detail.releaseDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="statusTagType(detail.currentStatus)" class="status-tag">{{ detail.currentStatus || '未知' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="涉外系统">
            <el-tag :type="detail.involveExternalSystem ? 'warning' : 'info'" class="info-tag">{{ detail.involveExternalSystem ? '是' : '否' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="跨服务">
            <el-tag :type="detail.crossService ? 'warning' : 'info'" class="info-tag">{{ detail.crossService ? '是' : '否' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="是否包含脚本">
            <el-tag :type="detail.includeShell ? 'warning' : 'info'" class="info-tag">{{ detail.includeShell ? '是' : '否' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="源分支">{{ detail.sourceBranch || '-' }}</el-descriptions-item>
          <el-descriptions-item label="目标分支">{{ detail.targetBranch || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(detail.createdTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDateTime(detail.updatedTime) }}</el-descriptions-item>
        </el-descriptions>
        
        <el-descriptions :column="1" border class="descriptions-section">
          <template slot="title">
            <div class="section-title">
              <i class="el-icon-edit-outline"></i>
              <span>详细信息</span>
            </div>
          </template>
          <el-descriptions-item label="问题描述">{{ detail.problemDescription || '-' }}</el-descriptions-item>
          <el-descriptions-item label="影响分析">{{ detail.impactAnalysis || '-' }}</el-descriptions-item>
          <el-descriptions-item label="解决方案">{{ detail.solutionDescription || '-' }}</el-descriptions-item>
          <el-descriptions-item label="代码清单">{{ detail.codeList || '-' }}</el-descriptions-item>
          <el-descriptions-item label="脚本清单">{{ detail.shellPath || '-' }}</el-descriptions-item>
          <el-descriptions-item label="配置说明">{{ detail.configList || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ detail.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getChangeRecordDetail } from '../api';

export default {
  name: 'ChangeRecordDetail',
  data() {
    return { detail: {} };
  },
  created() {
    const id = this.$route.params.id;
    this.fetchDetail(id);
  },
  methods: {
    async fetchDetail(id) {
      try {
        const data = await getChangeRecordDetail(id);
        this.detail = data || {};
      } catch (e) {}
    },
    statusTagType(s) {
      switch (s) {
        case '01': case '待审批': return 'warning';
        case '02': case '待评审': return 'info';
        case '03': case '待合版': return 'primary';
        case '04': case '已合版': return 'success';
        default: return '';
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
.change-record-detail {
  width: 100%;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eaf6 100%);
  min-height: calc(100vh - 60px);
}

.detail-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(123, 104, 238, 0.08);
  margin-bottom: 20px;
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

/* Detail content */
.detail-content {
  padding: 0;
}

.descriptions-section {
  margin-bottom: 24px;
  border-radius: 8px;
  overflow: hidden;
}

.descriptions-section:last-child {
  margin-bottom: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  padding: 16px 20px;
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  color: #ffffff;
  margin: -1px -1px 0 -1px;
}

.section-title i {
  font-size: 18px;
}

/* Descriptions styling */
.descriptions-section ::v-deep .el-descriptions {
  border: none;
}

.descriptions-section ::v-deep .el-descriptions__header {
  margin-bottom: 0;
}

.descriptions-section ::v-deep .el-descriptions__body {
  background: #ffffff;
}

.descriptions-section ::v-deep .el-descriptions-item__label {
  background: linear-gradient(135deg, #f8f9ff 0%, #f0f2ff 100%);
  color: #5a4fcf;
  font-weight: 600;
  padding: 16px 20px;
  border-color: #e8e8f0;
  font-size: 14px;
}

.descriptions-section ::v-deep .el-descriptions-item__content {
  padding: 14px 20px;
  border-color: #e8e8f0;
  color: #1a1a2e;
  font-size: 14px;
  line-height: 1.6;
}

.descriptions-section ::v-deep .el-descriptions__border {
  border-color: #e8e8f0;
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
.change-record-detail ::v-deep .el-card__header {
  padding: 20px 24px;
  border-bottom: 1px solid #e8e8f0;
  background: #ffffff;
}

.change-record-detail ::v-deep .el-card__body {
  padding: 24px;
}

/* Empty state styling */
.descriptions-section ::v-deep .el-descriptions-item__content:empty::before {
  content: '-';
  color: #c0c4cc;
}

/* Responsive design */
@media (max-width: 1200px) {
  .change-record-detail {
    padding: 16px;
  }

  .detail-card {
    margin-bottom: 16px;
  }

  .change-record-detail ::v-deep .el-card__header {
    padding: 16px 20px;
  }

  .change-record-detail ::v-deep .el-card__body {
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

  .descriptions-section ::v-deep .el-descriptions-item__label {
    padding: 12px 16px;
    font-size: 13px;
  }

  .descriptions-section ::v-deep .el-descriptions-item__content {
    padding: 10px 16px;
    font-size: 13px;
  }

  .section-title {
    font-size: 14px;
    padding: 12px 16px;
  }

  .section-title i {
    font-size: 16px;
  }
}

@media (max-width: 768px) {
  .change-record-detail {
    padding: 12px;
  }

  .detail-card {
    margin-bottom: 12px;
    border-radius: 8px;
  }

  .change-record-detail ::v-deep .el-card__header {
    padding: 12px 16px;
  }

  .change-record-detail ::v-deep .el-card__body {
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

  .descriptions-section ::v-deep .el-descriptions-item__label {
    padding: 10px 12px;
    font-size: 12px;
  }

  .descriptions-section ::v-deep .el-descriptions-item__content {
    padding: 8px 12px;
    font-size: 12px;
  }

  .section-title {
    font-size: 13px;
    padding: 10px 12px;
  }

  .section-title i {
    font-size: 14px;
  }

  .status-tag,
  .info-tag {
    font-size: 12px;
    padding: 3px 10px;
  }
}

@media (max-width: 480px) {
  .change-record-detail {
    padding: 8px;
  }

  .detail-card {
    margin-bottom: 8px;
    border-radius: 6px;
  }

  .change-record-detail ::v-deep .el-card__header {
    padding: 10px 12px;
  }

  .change-record-detail ::v-deep .el-card__body {
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

  .descriptions-section ::v-deep .el-descriptions-item__label {
    padding: 8px 10px;
    font-size: 11px;
  }

  .descriptions-section ::v-deep .el-descriptions-item__content {
    padding: 6px 10px;
    font-size: 11px;
  }

  .section-title {
    font-size: 12px;
    padding: 8px 10px;
  }

  .section-title i {
    font-size: 13px;
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
  .change-record-detail {
    background: #ffffff;
    padding: 0;
  }

  .detail-card {
    box-shadow: none;
    margin-bottom: 0;
  }

  .section-title {
    background: #7B68EE !important;
    color: #ffffff !important;
    -webkit-print-color-adjust: exact;
    print-color-adjust: exact;
  }

  .back-btn {
    display: none;
  }
}

/* Animation for content loading */
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

.detail-content {
  animation: fadeIn 0.4s ease-out;
}

/* Hover effects on descriptions items */
.descriptions-section ::v-deep .el-descriptions-item:hover .el-descriptions-item__content {
  background: linear-gradient(135deg, #fafbff 0%, #f5f7ff 100%);
  transition: background 0.2s ease;
}
</style>