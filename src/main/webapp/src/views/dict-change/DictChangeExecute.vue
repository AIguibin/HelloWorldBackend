<template>
  <div class="dict-change-execute">
    <el-card shadow="never" class="card-container">
      <template slot="header">
        <div class="card-header">
          <span>字典变更执行</span>
        </div>
      </template>
      
      <!-- 待执行列表 -->
      <el-table :data="pendingList" style="width: 100%" @row-click="handleRowClick">
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
        <el-table-column prop="approver" label="审批人" width="120">
          <template slot-scope="scope">
            {{ scope.row.approverName || scope.row.approverNum }}
          </template>
        </el-table-column>
        <el-table-column prop="approveTime" label="审批时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.approveTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="scope">
            <el-button type="primary" size="small" @click="handleExecute(scope.row)">
              执行
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 执行结果对话框 -->
      <el-dialog
        title="执行结果"
        :visible.sync="resultDialogVisible"
        width="60%"
      >
        <div v-if="executeResult">
          <el-alert
            :title="executeResult.success ? '执行成功' : '执行失败'"
            :type="executeResult.success ? 'success' : 'error'"
            show-icon
            :description="executeResult.message"
            class="mb-20"
          ></el-alert>
          
          <el-button type="primary" @click="resultDialogVisible = false">确定</el-button>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { getPendingExecute, executeChange } from '@/api'

export default {
  name: 'DictChangeExecute',
  data() {
    return {
      pendingList: [],
      resultDialogVisible: false,
      executeResult: null
    }
  },
  mounted() {
    this.loadPendingList()
  },
  methods: {
    // 加载待执行列表
    loadPendingList() {
      getPendingExecute()
        .then(response => {
          this.pendingList = response.data
        })
        .catch(error => {
          this.$message.error('加载失败: ' + error.message)
        })
    },
    
    // 点击行
    handleRowClick(row) {
      this.handleExecute(row)
    },
    
    // 执行变更
    handleExecute(row) {
      this.$confirm('确定要执行此变更吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 执行API
        executeChange(row.id)
          .then(response => {
            this.executeResult = response.data
            this.resultDialogVisible = true
            this.loadPendingList()
          })
          .catch(error => {
            this.executeResult = { success: false, message: error.message }
            this.resultDialogVisible = true
          })
      }).catch(() => {
        this.$message.info('已取消执行')
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
    
    // 格式化日期
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleString()
    }
  }
}
</script>

<style scoped>
.dict-change-execute {
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

.mb-20 {
  margin-bottom: 20px;
}
</style>