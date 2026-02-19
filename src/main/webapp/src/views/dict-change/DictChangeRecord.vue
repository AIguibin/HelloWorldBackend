<template>
  <div class="dict-change-record">
    <el-card shadow="hover" class="card-container">
      <template slot="header">
        <div class="card-header">
          <div class="header-left">
            <h2 class="card-title">
              <i class="el-icon-document"></i>
              字典变更记录
            </h2>
            <p class="card-subtitle">管理和查看所有字典变更记录</p>
          </div>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd" class="add-btn">
              <i class="el-icon-plus"></i>
              新增变更申请
            </el-button>
            <el-button @click="toggleAdvancedSearch" class="search-toggle-btn">
              <i :class="['el-icon-arrow-down', { 'rotate-180': showAdvancedSearch }]"></i>
              {{ showAdvancedSearch ? '收起' : '高级查询' }}
            </el-button>
          </div>
        </div>
      </template>
      
      <el-form :model="queryForm" label-width="100px" class="query-form">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="变更单号">
              <el-input v-model="queryForm.changeNo" placeholder="请输入变更单号" prefix-icon="el-icon-search"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="字典类型">
              <el-input v-model="queryForm.dctTp" placeholder="请输入字典类型" prefix-icon="el-icon-search"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="变更类型">
              <el-select v-model="queryForm.changeType" placeholder="请选择变更类型" style="width: 100%">
                <el-option label="新增" value="ADD"></el-option>
                <el-option label="修改" value="MOD"></el-option>
                <el-option label="删除" value="DEL"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="审批状态">
              <el-select v-model="queryForm.approveStatus" placeholder="请选择审批状态" style="width: 100%">
                <el-option label="草稿" value="DRAFT"></el-option>
                <el-option label="待审批" value="PENDING"></el-option>
                <el-option label="已通过" value="APPROVED"></el-option>
                <el-option label="已拒绝" value="REJECTED"></el-option>
                <el-option label="已取消" value="CANCELED"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="6" v-if="showAdvancedSearch">
            <el-form-item label="执行状态">
              <el-select v-model="queryForm.executeStatus" placeholder="请选择执行状态" style="width: 100%">
                <el-option label="待执行" value="PENDING"></el-option>
                <el-option label="执行中" value="EXECUTING"></el-option>
                <el-option label="成功" value="SUCCESS"></el-option>
                <el-option label="失败" value="FAILED"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6" v-if="showAdvancedSearch">
            <el-form-item label="申请人">
              <el-input v-model="queryForm.applyUser" placeholder="请输入申请人" prefix-icon="el-icon-user"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6" v-if="showAdvancedSearch">
            <el-form-item label="申请时间">
              <el-date-picker
                v-model="queryForm.applyTimeRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          
          <el-col :span="6" class="query-buttons">
            <el-form-item>
              <div class="button-group">
                <el-button @click="resetForm" class="reset-btn">
                  <i class="el-icon-refresh"></i>
                  重置
                </el-button>
                <el-button type="primary" @click="handleQuery" class="query-btn">
                  <i class="el-icon-search"></i>
                  查询
                </el-button>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <el-table :data="recordList" stripe class="data-table" border v-loading="loading">
        <el-table-column prop="changeNo" label="变更单号" width="200" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="changeType" label="变更类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="getChangeTypeTagType(scope.row.changeType)" size="small">
              {{ getChangeTypeText(scope.row.changeType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="oldDctTp" label="原字典类型" width="180" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="newDctTp" label="新字典类型" width="180" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="applyUser" label="申请人" width="120">
          <template slot-scope="scope">
            {{ scope.row.applyUserName || scope.row.applyUserNum }}
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.applyTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="approveStatus" label="审批状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getApproveStatusTagType(scope.row.approveStatus)" size="small">
              {{ getApproveStatusText(scope.row.approveStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="executeStatus" label="执行状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getExecuteStatusTagType(scope.row.executeStatus)" size="small">
              {{ getExecuteStatusText(scope.row.executeStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="changeReason" label="变更原因" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="scope">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="handleViewDetail(scope.row)" class="view-btn">
                <i class="el-icon-view"></i>
                查看详情
              </el-button>
              <el-button type="success" size="small" @click="handleEdit(scope.row)" v-if="scope.row.approveStatus === 'DRAFT'" class="edit-btn">
                <i class="el-icon-edit"></i>
                修改
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        class="pagination"
      ></el-pagination>
      
      <el-dialog title="变更详情" :visible.sync="detailDialogVisible" width="80%" class="detail-dialog">
        <div v-if="currentRecord">
          <el-form :model="currentRecord" label-width="120px" class="detail-form">
            <el-form-item label="变更单号">
              {{ currentRecord.changeNo }}
            </el-form-item>
            <el-form-item label="变更类型">
              <el-tag :type="getChangeTypeTagType(currentRecord.changeType)">
                {{ getChangeTypeText(currentRecord.changeType) }}
              </el-tag>
            </el-form-item>
            <el-form-item label="原字典类型">
              <div>{{ currentRecord.oldDctTp }} - {{ currentRecord.oldDctTpNm }}</div>
            </el-form-item>
            <el-form-item label="新字典类型">
              <div>{{ currentRecord.newDctTp }} - {{ currentRecord.newDctTpNm }}</div>
            </el-form-item>
            <el-form-item label="申请人">
              {{ currentRecord.applyUserName || currentRecord.applyUserNum }}
            </el-form-item>
            <el-form-item label="申请时间">
              {{ formatDate(currentRecord.applyTime) }}
            </el-form-item>
            <el-form-item label="审批人">
              {{ currentRecord.approverName || currentRecord.approverNum }}
            </el-form-item>
            <el-form-item label="审批时间">
              {{ formatDate(currentRecord.approveTime) }}
            </el-form-item>
            <el-form-item label="审批意见">
              {{ currentRecord.approveRemark }}
            </el-form-item>
            <el-form-item label="变更原因">
              <el-input v-model="currentRecord.changeReason" type="textarea" :rows="3" disabled></el-input>
            </el-form-item>
            <el-form-item label="变更影响">
              <el-input v-model="currentRecord.changeImpact" type="textarea" :rows="3" disabled></el-input>
            </el-form-item>
          </el-form>
          
          <el-card shadow="never" class="dict-item-card">
            <template slot="header">
              <span>字典项变更</span>
            </template>
            
            <div v-if="currentRecordDetail && currentRecordDetail.dictItemChanges && currentRecordDetail.dictItemChanges.length > 0">
              <el-table :data="currentRecordDetail.dictItemChanges" border class="dict-item-table">
                <el-table-column label="操作类型" width="100">
                  <template slot-scope="scope">
                    <el-tag :type="getOperationTagType(scope.row.changeOperation)" size="small">
                      {{ getOperationText(scope.row.changeOperation) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="oldDctKey" label="原字典键" width="150"></el-table-column>
                <el-table-column prop="newDctKey" label="新字典键" width="150"></el-table-column>
                <el-table-column prop="oldDctValNm" label="原字典值名称" width="180"></el-table-column>
                <el-table-column prop="newDctValNm" label="新字典值名称" width="180"></el-table-column>
                <el-table-column prop="executeStatus" label="执行状态" width="100">
                  <template slot-scope="scope">
                    <el-tag :type="getExecuteStatusTagType(scope.row.executeStatus)" size="small">
                      {{ getExecuteStatusText(scope.row.executeStatus) }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div v-else class="empty-tip">
              暂无字典项变更
            </div>
          </el-card>
        </div>
        
        <span slot="footer" class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { queryChanges, getChangeDetail } from '@/api'

export default {
  name: 'DictChangeRecord',
  data() {
    return {
      queryForm: {
        changeNo: '',
        dctTp: '',
        changeType: '',
        approveStatus: '',
        executeStatus: '',
        applyUser: '',
        applyTimeRange: []
      },
      recordList: [],
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      detailDialogVisible: false,
      currentRecord: null,
      currentRecordDetail: null,
      showAdvancedSearch: false,
      loading: false
    }
  },
  mounted() {
    this.handleQuery()
  },
  methods: {
    handleQuery() {
      const params = {
        page: this.pagination.current,
        size: this.pagination.size,
        changeNo: this.queryForm.changeNo,
        dctTp: this.queryForm.dctTp,
        changeType: this.queryForm.changeType,
        approveStatus: this.queryForm.approveStatus,
        executeStatus: this.queryForm.executeStatus,
        applyUser: this.queryForm.applyUser
      }
      
      if (this.queryForm.applyTimeRange && this.queryForm.applyTimeRange.length === 2) {
        params.startApplyTime = this.queryForm.applyTimeRange[0]
        params.endApplyTime = this.queryForm.applyTimeRange[1]
      }
      
      this.loading = true
      queryChanges(params)
        .then(response => {
          this.recordList = response.data.records || []
          this.pagination.total = response.data.total || 0
        })
        .catch(error => {
          this.$message.error('查询失败: ' + error.message)
        })
        .finally(() => {
          this.loading = false
        })
    },
    
    resetForm() {
      this.queryForm = {
        changeNo: '',
        dctTp: '',
        changeType: '',
        approveStatus: '',
        executeStatus: '',
        applyUser: '',
        applyTimeRange: []
      }
      this.pagination.current = 1
      this.pagination.size = 10
      this.handleQuery()
    },
    
    toggleAdvancedSearch() {
      this.showAdvancedSearch = !this.showAdvancedSearch
    },
    
    handleAdd() {
      this.$router.push('/dict-change/apply')
    },
    
    handleViewDetail(row) {
      this.currentRecord = row
      getChangeDetail(row.uuid)
        .then(response => {
          this.currentRecordDetail = response.data
          this.detailDialogVisible = true
        })
        .catch(error => {
          this.$message.error('加载详情失败: ' + error.message)
        })
    },
    
    handleEdit(row) {
      this.$router.push(`/dict-change/apply?changeId=${row.uuid}`)
    },
    
    handleSizeChange(size) {
      this.pagination.size = size
      this.pagination.current = 1
      this.handleQuery()
    },
    
    handleCurrentChange(current) {
      this.pagination.current = current
      this.handleQuery()
    },
    
    getChangeTypeTagType(changeType) {
      switch (changeType) {
        case 'ADD': return 'success'
        case 'MOD': return 'warning'
        case 'DEL': return 'danger'
        default: return ''
      }
    },
    
    getChangeTypeText(changeType) {
      switch (changeType) {
        case 'ADD': return '新增'
        case 'MOD': return '修改'
        case 'DEL': return '删除'
        default: return changeType
      }
    },
    
    getApproveStatusTagType(status) {
      switch (status) {
        case 'DRAFT': return 'info'
        case 'PENDING': return 'warning'
        case 'APPROVED': return 'success'
        case 'REJECTED': return 'danger'
        case 'CANCELED': return 'danger'
        default: return ''
      }
    },
    
    getApproveStatusText(status) {
      switch (status) {
        case 'DRAFT': return '草稿'
        case 'PENDING': return '待审批'
        case 'APPROVED': return '已通过'
        case 'REJECTED': return '已拒绝'
        case 'CANCELED': return '已取消'
        default: return status
      }
    },
    
    getExecuteStatusTagType(status) {
      switch (status) {
        case 'PENDING': return 'info'
        case 'EXECUTING': return 'warning'
        case 'SUCCESS': return 'success'
        case 'FAILED': return 'danger'
        default: return ''
      }
    },
    
    getExecuteStatusText(status) {
      switch (status) {
        case 'PENDING': return '待执行'
        case 'EXECUTING': return '执行中'
        case 'SUCCESS': return '成功'
        case 'FAILED': return '失败'
        default: return status
      }
    },
    
    getOperationTagType(operation) {
      switch (operation) {
        case 'ADD': return 'success'
        case 'MOD': return 'warning'
        case 'DEL': return 'danger'
        default: return ''
      }
    },
    
    getOperationText(operation) {
      switch (operation) {
        case 'ADD': return '新增'
        case 'MOD': return '修改'
        case 'DEL': return '删除'
        default: return operation
      }
    },
    
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleString()
    }
  }
}
</script>

<style scoped>
.dict-change-record {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eaf6 100%);
  min-height: 100vh;
}

.card-container {
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(123, 104, 238, 0.12);
  border: 1px solid rgba(123, 104, 238, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.card-title {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.card-title i {
  font-size: 28px;
  color: #7B68EE;
}

.card-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
  line-height: 1.5;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.add-btn {
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  border: none;
  color: #fff;
}

.add-btn:hover {
  background: linear-gradient(135deg, #9370DB 0%, #BA55D3 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(123, 104, 238, 0.35);
}

.search-toggle-btn {
  padding: 10px 16px;
  border-radius: 8px;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.3s ease;
  border: 1px solid #e4e7eb;
  color: #4a5568;
}

.search-toggle-btn:hover {
  border-color: #7B68EE;
  color: #7B68EE;
  background: rgba(123, 104, 238, 0.05);
}

.search-toggle-btn .rotate-180 {
  transform: rotate(180deg);
}

.query-form {
  margin-bottom: 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  padding: 20px;
}

.query-form ::v-deep .el-form-item__label {
  font-size: 14px;
  font-weight: 500;
  color: #4a5568;
  padding-right: 12px;
}

.query-form ::v-deep .el-input__inner {
  border-radius: 8px;
  border: 1px solid #e4e7eb;
  transition: all 0.3s ease;
  font-size: 14px;
}

.query-form ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 4px rgba(123, 104, 238, 0.2);
}

.query-form ::v-deep .el-input__prefix {
  color: #7B68EE;
}

.query-form ::v-deep .el-select .el-input__inner {
  border-radius: 8px;
  border: 1px solid #e4e7eb;
  transition: all 0.3s ease;
  font-size: 14px;
}

.query-form ::v-deep .el-select .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 4px rgba(123, 104, 238, 0.2);
}

.button-group {
  display: flex;
  gap: 12px;
}

.reset-btn {
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.3s ease;
  border: 1px solid #e4e7eb;
  color: #4a5568;
}

.reset-btn:hover {
  border-color: #7B68EE;
  color: #7B68EE;
  background: rgba(123, 104, 238, 0.05);
}

.query-btn {
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  border: none;
  color: #fff;
}

.query-btn:hover {
  background: linear-gradient(135deg, #9370DB 0%, #BA55D3 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(123, 104, 238, 0.35);
}

.data-table {
  width: 100%;
  border-radius: 12px;
  overflow: hidden;
}

.data-table ::v-deep .el-table__header th {
  background: linear-gradient(135deg, rgba(123, 104, 238, 0.05) 0%, rgba(147, 112, 219, 0.05) 100%);
  color: #fff;
  font-weight: 600;
  padding: 16px 12px;
}

.data-table ::v-deep .el-table__body td {
  padding: 12px 16px;
  font-size: 14px;
  color: #4a5568;
}

.data-table ::v-deep .el-table__body tr {
  transition: all 0.2s ease;
}

.data-table ::v-deep .el-table__body tr:hover {
  background: linear-gradient(90deg, rgba(123, 104, 238, 0.08) 0%, rgba(147, 112, 219, 0.08) 100%);
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.view-btn,
.edit-btn {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 12px;
  transition: all 0.2s ease;
  border: 1px solid #e4e7eb;
  background: #fff;
  color: #4a5568;
}

.view-btn:hover {
  border-color: #7B68EE;
  color: #7B68EE;
  background: rgba(123, 104, 238, 0.08);
}

.edit-btn {
  border-color: #67c23a;
  background: rgba(103, 194, 58, 0.08);
  color: #67c23a;
}

.edit-btn:hover {
  border-color: #529b2e;
  background: rgba(82, 196, 26, 0.12);
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.detail-dialog ::v-deep .el-dialog {
  border-radius: 16px;
}

.detail-dialog ::v-deep .el-dialog__header {
  background: linear-gradient(135deg, rgba(123, 104, 238, 0.05) 0%, rgba(147, 112, 219, 0.05) 100%);
  padding: 20px 24px;
}

.detail-dialog ::v-deep .el-dialog__title {
  color: #fff;
  font-weight: 600;
  font-size: 18px;
}

.detail-form {
  margin-bottom: 20px;
}

.detail-form ::v-deep .el-form-item__label {
  font-size: 14px;
  font-weight: 500;
  color: #4a5568;
  padding-right: 12px;
}

.dict-item-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  margin-top: 20px;
}

.dict-item-card ::v-deep .el-card__header {
  background: rgba(123, 104, 238, 0.05);
  padding: 16px 20px;
}

.dict-item-card ::v-deep .el-card__header span {
  color: #1a1a2e;
  font-weight: 600;
}

.dict-item-table {
  border-radius: 8px;
}

.dict-item-table ::v-deep .el-table__header th {
  background: rgba(123, 104, 238, 0.03);
  color: #1a1a2e;
  font-weight: 600;
  padding: 12px 10px;
}

.dict-item-table ::v-deep .el-table__body td {
  padding: 10px 12px;
  font-size: 13px;
  color: #4a5568;
}

.empty-tip {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
  font-size: 14px;
}

.dialog-footer {
  text-align: right;
}

@media screen and (max-width: 1200px) {
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-actions {
    width: 100%;
    justify-content: flex-start;
  }

  .query-form ::v-deep .el-col {
    margin-bottom: 12px;
  }
}

@media screen and (max-width: 768px) {
  .dict-change-record {
    padding: 16px;
  }

  .card-title {
    font-size: 20px;
  }

  .card-title i {
    font-size: 24px;
  }

  .query-form ::v-deep .el-col {
    margin-bottom: 8px;
  }

  .button-group {
    flex-direction: column;
    width: 100%;
  }

  .reset-btn,
  .query-btn {
    width: 100%;
  }
}
</style>
