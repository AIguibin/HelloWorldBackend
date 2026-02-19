<template>
  <div class="dict-change-approve">
    <el-card shadow="never" class="card-container">
      <template slot="header">
        <div class="card-header">
          <span>字典变更审批</span>
        </div>
      </template>
      
      <!-- 待审批列表 -->
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
        <el-table-column prop="changeReason" label="变更原因"></el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="scope">
            <el-button type="primary" size="small" @click="handleApprove(scope.row)">
              审批
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 审批对话框 -->
      <el-dialog
        title="变更审批"
        :visible.sync="approveDialogVisible"
        width="80%"
      >
        <div v-if="currentChange">
          <!-- 变更基本信息 -->
          <el-form :model="currentChange" label-width="120px">
            <el-form-item label="变更单号">
              {{ currentChange.changeNo }}
            </el-form-item>
            <el-form-item label="变更类型">
              <el-tag :type="getChangeTypeTagType(currentChange.changeType)">
                {{ getChangeTypeText(currentChange.changeType) }}
              </el-tag>
            </el-form-item>
            <el-form-item label="原字典类型">
              <div>{{ currentChange.oldDctTp }} - {{ currentChange.oldDctTpNm }}</div>
            </el-form-item>
            <el-form-item label="新字典类型">
              <div>{{ currentChange.newDctTp }} - {{ currentChange.newDctTpNm }}</div>
            </el-form-item>
            <el-form-item label="申请人">
              {{ currentChange.applyUserName || currentChange.applyUserNum }}
            </el-form-item>
            <el-form-item label="申请时间">
              {{ formatDate(currentChange.applyTime) }}
            </el-form-item>
            <el-form-item label="变更原因">
              <el-input
                v-model="currentChange.changeReason"
                type="textarea"
                :rows="3"
                disabled
              ></el-input>
            </el-form-item>
            <el-form-item label="变更影响">
              <el-input
                v-model="currentChange.changeImpact"
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
            
            <div v-if="currentChangeDetail && currentChangeDetail.dictItemChanges && currentChangeDetail.dictItemChanges.length > 0">
              <el-table :data="currentChangeDetail.dictItemChanges" style="width: 100%">
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
              </el-table>
            </div>
            <div v-else class="empty-tip">
              暂无字典项变更
            </div>
          </el-card>
          
          <!-- 审批意见 -->
          <el-form :model="approveForm" :rules="approveRules" ref="approveForm" label-width="120px" class="mt-20">
            <el-form-item label="审批结果" prop="approveResult">
              <el-radio-group v-model="approveForm.approveResult">
                <el-radio label="APPROVE">通过</el-radio>
                <el-radio label="REJECT">拒绝</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="审批意见" prop="approveRemark">
              <el-input
                v-model="approveForm.approveRemark"
                type="textarea"
                :rows="3"
                placeholder="请输入审批意见"
              ></el-input>
            </el-form-item>
          </el-form>
        </div>
        
        <span slot="footer" class="dialog-footer">
          <el-button @click="approveDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitApproval">提交审批</el-button>
        </span>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { getPendingApprove, getChangeDetail, approveChange } from '@/api'

export default {
  name: 'DictChangeApprove',
  data() {
    return {
      pendingList: [],
      approveDialogVisible: false,
      currentChange: null,
      currentChangeDetail: null,
      approveForm: {
        approveResult: 'APPROVE',
        approveRemark: ''
      },
      approveRules: {
        approveResult: [
          { required: true, message: '请选择审批结果', trigger: 'change' }
        ],
        approveRemark: [
          { required: true, message: '请输入审批意见', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.loadPendingList()
  },
  methods: {
    // 加载待审批列表
    loadPendingList() {
      getPendingApprove()
        .then(data => {
          this.pendingList = data
        })
        .catch(error => {
          this.$message.error('加载失败: ' + error.message)
        })
    },
    
    // 点击行
    handleRowClick(row) {
      this.handleApprove(row)
    },
    
    // 审批
    handleApprove(row) {
      this.currentChange = row
      this.approveForm.approveResult = 'APPROVE'
      this.approveForm.approveRemark = ''
      
      // 加载变更详情
      getChangeDetail(row.id)
        .then(data => {
          this.currentChangeDetail = data
          this.approveDialogVisible = true
        })
        .catch(error => {
          this.$message.error('加载详情失败: ' + error.message)
        })
    },
    
    // 提交审批
    submitApproval() {
      this.$refs.approveForm.validate((valid) => {
        if (valid) {
          if (this.currentChange) {
            const approveDTO = {
              approveResult: this.approveForm.approveResult === 'APPROVE',
              approveRemark: this.approveForm.approveRemark
            }
            
            approveChange(this.currentChange.id, approveDTO)
              .then(response => {
                this.$message.success('审批成功')
                this.approveDialogVisible = false
                this.loadPendingList()
              })
              .catch(error => {
                this.$message.error('审批失败: ' + error.message)
              })
          }
        } else {
          return false
        }
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
.dict-change-approve {
  padding: 20px;
}

.card-container {
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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