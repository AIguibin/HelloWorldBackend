<template>
  <div class="dict-change-record">
    <el-card shadow="never" class="card-container">
      <template slot="header">
        <div class="card-header">
          <span>字典变更记录</span>
        </div>
      </template>
      
      <!-- 查询条件 -->
      <el-form :model="queryForm" label-width="100px" class="query-form">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="变更单号">
              <el-input v-model="queryForm.changeNo" placeholder="请输入变更单号"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="字典类型">
              <el-input v-model="queryForm.dctTp" placeholder="请输入字典类型"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="变更类型">
              <el-select v-model="queryForm.changeType" placeholder="请选择变更类型">
                <el-option label="新增" value="ADD"></el-option>
                <el-option label="修改" value="MOD"></el-option>
                <el-option label="删除" value="DEL"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="审批状态">
              <el-select v-model="queryForm.approveStatus" placeholder="请选择审批状态">
                <el-option label="草稿" value="DRAFT"></el-option>
                <el-option label="待审批" value="PENDING"></el-option>
                <el-option label="已通过" value="APPROVED"></el-option>
                <el-option label="已拒绝" value="REJECTED"></el-option>
                <el-option label="已取消" value="CANCELED"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="执行状态">
              <el-select v-model="queryForm.executeStatus" placeholder="请选择执行状态">
                <el-option label="待执行" value="PENDING"></el-option>
                <el-option label="执行中" value="EXECUTING"></el-option>
                <el-option label="成功" value="SUCCESS"></el-option>
                <el-option label="失败" value="FAILED"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="申请人">
              <el-input v-model="queryForm.applyUser" placeholder="请输入申请人"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
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
              <el-button type="primary" @click="handleQuery">查询</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <!-- 变更记录列表 -->
      <el-table :data="recordList" style="width: 100%" @row-click="handleRowClick">
        <el-table-column prop="changeNo" label="变更单号" width="200"></el-table-column>
        <el-table-column prop="changeType" label="变更类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="getChangeTypeTagType(scope.row.changeType)">
              {{ getChangeTypeText(scope.row.changeType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="oldDctTp" label="原字典类型" width="180"></el-table-column>
        <el-table-column prop="newDctTp" label="新字典类型" width="180"></el-table-column>
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
            <el-tag :type="getApproveStatusTagType(scope.row.approveStatus)">
              {{ getApproveStatusText(scope.row.approveStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="executeStatus" label="执行状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getExecuteStatusTagType(scope.row.executeStatus)">
              {{ getExecuteStatusText(scope.row.executeStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="changeReason" label="变更原因"></el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        class="mt-20"
      ></el-pagination>
      
      <!-- 详情对话框 -->
      <el-dialog
        title="变更详情"
        :visible.sync="detailDialogVisible"
        width="80%"
      >
        <div v-if="currentRecord">
          <!-- 变更基本信息 -->
          <el-form :model="currentRecord" label-width="120px">
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
              <el-input
                v-model="currentRecord.changeReason"
                type="textarea"
                :rows="3"
                disabled
              ></el-input>
            </el-form-item>
            <el-form-item label="变更影响">
              <el-input
                v-model="currentRecord.changeImpact"
                type="textarea"
                :rows="3"
                disabled
              ></el-input>
            </el-form-item>
          </el-form>
          
          <!-- 字典项变更 -->
          <el-card shadow="never" class="mt-20">
            <template slot="header">
              <span>字典项变更</span>
            </template>
            
            <div v-if="currentRecordDetail && currentRecordDetail.dictItemChanges && currentRecordDetail.dictItemChanges.length > 0">
              <el-table :data="currentRecordDetail.dictItemChanges" style="width: 100%">
                <el-table-column label="操作类型" width="100">
                  <template slot-scope="scope">
                    <el-tag :type="getOperationTagType(scope.row.changeOperation)">
                      {{ getOperationText(scope.row.changeOperation) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="oldDctKey" label="原字典键" width="150"></el-table-column>
                <el-table-column prop="newDctKey" label="新字典键" width="150"></el-table-column>
                <el-table-column prop="oldDctValNm" label="原字典值名称" width="180"></el-table-column>
                <el-table-column prop="newDctValNm" label="新字典值名称" width="180"></el-table-column>
                <el-table-column prop="oldDctVal" label="原字典值" width="150"></el-table-column>
                <el-table-column prop="newDctVal" label="新字典值" width="150"></el-table-column>
                <el-table-column prop="executeStatus" label="执行状态" width="100">
                  <template slot-scope="scope">
                    <el-tag :type="getExecuteStatusTagType(scope.row.executeStatus)">
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
      currentRecordDetail: null
    }
  },
  mounted() {
    this.handleQuery()
  },
  methods: {
    // 查询
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
      
      this.$axios.get('/api/dict/change/list', { params })
        .then(response => {
          this.recordList = response.data.records
          this.pagination.total = response.data.total
        })
        .catch(error => {
          this.$message.error('查询失败: ' + error.message)
        })
    },
    
    // 重置
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
    
    // 分页大小变化
    handleSizeChange(size) {
      this.pagination.size = size
      this.handleQuery()
    },
    
    // 当前页码变化
    handleCurrentChange(current) {
      this.pagination.current = current
      this.handleQuery()
    },
    
    // 点击行
    handleRowClick(row) {
      this.currentRecord = row
      
      // 加载变更详情
      this.$axios.get(`/api/dict/change/detail/${row.uuid}`)
        .then(response => {
          this.currentRecordDetail = response.data
          this.detailDialogVisible = true
        })
        .catch(error => {
          this.$message.error('加载详情失败: ' + error.message)
        })
    },
    
    // 获取变更类型标签类型
    getChangeTypeTagType(changeType) {
      switch (changeType) {
        case 'ADD': return 'success'
        case 'MOD': return 'warning'
        case 'DEL': return 'danger'
        default: return ''
      }
    },
    
    // 获取变更类型文本
    getChangeTypeText(changeType) {
      switch (changeType) {
        case 'ADD': return '新增'
        case 'MOD': return '修改'
        case 'DEL': return '删除'
        default: return changeType
      }
    },
    
    // 获取审批状态标签类型
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
    
    // 获取审批状态文本
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
    
    // 获取执行状态标签类型
    getExecuteStatusTagType(status) {
      switch (status) {
        case 'PENDING': return 'info'
        case 'EXECUTING': return 'warning'
        case 'SUCCESS': return 'success'
        case 'FAILED': return 'danger'
        default: return ''
      }
    },
    
    // 获取执行状态文本
    getExecuteStatusText(status) {
      switch (status) {
        case 'PENDING': return '待执行'
        case 'EXECUTING': return '执行中'
        case 'SUCCESS': return '成功'
        case 'FAILED': return '失败'
        default: return status
      }
    },
    
    // 获取操作类型标签类型
    getOperationTagType(operation) {
      switch (operation) {
        case 'ADD': return 'success'
        case 'MOD': return 'warning'
        case 'DEL': return 'danger'
        default: return ''
      }
    },
    
    // 获取操作类型文本
    getOperationText(operation) {
      switch (operation) {
        case 'ADD': return '新增'
        case 'MOD': return '修改'
        case 'DEL': return '删除'
        default: return operation
      }
    },
    
    // 格式化日期
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
}

.card-container {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.query-form {
  margin-bottom: 20px;
}

.query-buttons {
  display: flex;
  align-items: flex-end;
}

.empty-tip {
  padding: 20px;
  text-align: center;
  color: #999;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.mt-20 {
  margin-top: 20px;
}
</style>