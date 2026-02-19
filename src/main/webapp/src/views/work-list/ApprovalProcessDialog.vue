<template>
  <el-dialog
    :title="title"
    :visible.sync="visible"
    width="700px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @close="handleCancel"
    class="approval-process-dialog"
  >
    <el-form ref="approvalForm" :model="form" :rules="rules" label-width="100px" class="dialog-form">
      <!-- 业务数据详情 -->
      <div class="business-detail">
        <h3 class="section-title">
          <i class="el-icon-document"></i>
          业务数据详情
        </h3>
        <el-divider class="section-divider"></el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="缺陷编号">
              <el-input v-model="businessData.defectNumber" readonly class="readonly-input" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="服务名称">
              <el-input v-model="businessData.serviceName" readonly class="readonly-input" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="组别">
              <el-input v-model="businessData.groupName" readonly class="readonly-input" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开发负责人">
              <el-input v-model="businessData.developer" readonly class="readonly-input" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开发类别">
              <el-input v-model="businessData.developType" readonly class="readonly-input" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前状态">
              <el-input v-model="businessData.currentStatus" readonly class="readonly-input" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="问题描述">
          <el-input v-model="businessData.problemDescription" type="textarea" rows="3" readonly class="readonly-textarea" />
        </el-form-item>
        <el-form-item label="变更描述">
          <el-input v-model="businessData.changeDesc" type="textarea" rows="3" readonly class="readonly-textarea" />
        </el-form-item>
      </div>

      <!-- 审批处理表单 -->
      <div class="approval-form">
        <h3 class="section-title">
          <i class="el-icon-edit-outline"></i>
          审批处理
        </h3>
        <el-divider class="section-divider"></el-divider>
        
        <!-- 审批意见 -->
        <el-form-item label="审批意见" prop="comment">
          <el-input
            v-model="form.comment"
            type="textarea"
            rows="4"
            placeholder="请输入审批意见"
            class="form-textarea"
          />
        </el-form-item>

        <!-- 驳回节点选择（仅驳回时显示） -->
        <el-form-item
          v-if="isRejecting"
          label="驳回到节点"
          prop="rejectNodeId"
        >
          <el-select
            v-model="form.rejectNodeId"
            placeholder="请选择要驳回的节点"
            class="form-select"
          >
            <el-option
              v-for="node in availableNodes"
              :key="node.id"
              :label="node.nodeName"
              :value="node.id"
            />
          </el-select>
        </el-form-item>

        <!-- 转办人选择（仅转办时显示） -->
        <el-form-item
          v-if="isTransferring"
          label="转办人"
          prop="transferUserId"
        >
          <el-select
            v-model="form.transferUserId"
            placeholder="请选择转办人"
            class="form-select"
          >
            <el-option
              v-for="user in availableUsers"
              :key="user.id"
              :label="user.username"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
      </div>
    </el-form>

    <span slot="footer" class="dialog-footer">
      <!-- 同意按钮 -->
      <el-button
        type="primary"
        :loading="submitting"
        @click="handleApprove"
        v-if="canApprove"
        class="action-btn approve-btn"
      >
        <i class="el-icon-check"></i>
        同意
      </el-button>

      <!-- 驳回按钮 -->
      <el-button
        type="danger"
        :loading="submitting"
        @click="handleReject"
        v-if="canReject"
        class="action-btn reject-btn"
      >
        <i class="el-icon-close"></i>
        驳回
      </el-button>

      <!-- 转办按钮 -->
      <el-button
        :loading="submitting"
        @click="handleTransfer"
        v-if="canTransfer"
        class="action-btn transfer-btn"
      >
        <i class="el-icon-share"></i>
        转办
      </el-button>

      <!-- 取消按钮 -->
      <el-button @click="handleCancel" class="action-btn cancel-btn">
        <i class="el-icon-close"></i>
        取消
      </el-button>
    </span>
  </el-dialog>
</template>

<script>
import { approveTask, rejectTask, transferTask } from '../../api';
// 导入权限混入
import permissionMixin from '../../utils/permissionMixin';

export default {
  name: 'ApprovalProcessDialog',
  mixins: [permissionMixin],
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    task: {
      type: Object,
      default: () => ({})
    },
    businessData: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      title: '审批处理',
      form: {
        comment: '',
        rejectNodeId: null,
        transferUserId: null,
        action: ''
      },
      submitting: false,
      isRejecting: false,
      isTransferring: false,
      availableNodes: [
        { id: 1, nodeName: '提交节点' },
        { id: 2, nodeName: '技术评审' },
        { id: 3, nodeName: '项目经理审批' }
      ],
      availableUsers: [
        { id: 'user1', username: '张三' },
        { id: 'user2', username: '李四' },
        { id: 'user3', username: '王五' }
      ],
      rules: {
        comment: [
          {
            required: true,
            message: '请输入审批意见',
            trigger: 'blur',
            validator: (rule, value, callback) => {
              // 只有驳回时必须填写意见
              if (this.isRejecting && !value.trim()) {
                callback(new Error('驳回时必须填写审批意见'));
              } else {
                callback();
              }
            }
          }
        ],
        rejectNodeId: [
          {
            required: true,
            message: '请选择要驳回的节点',
            trigger: 'change',
            validator: (rule, value, callback) => {
              if (this.isRejecting && !value) {
                callback(new Error('请选择要驳回的节点'));
              } else {
                callback();
              }
            }
          }
        ],
        transferUserId: [
          {
            required: true,
            message: '请选择转办人',
            trigger: 'change',
            validator: (rule, value, callback) => {
              if (this.isTransferring && !value) {
                callback(new Error('请选择转办人'));
              } else {
                callback();
              }
            }
          },
          {
            validator: (rule, value, callback) => {
              // 转办人不能是当前用户
              if (this.isTransferring && value && value === this.getCurrentUserNum()) {
                callback(new Error('转办人不能是当前用户'));
              } else {
                callback();
              }
            },
            trigger: 'change'
          }
        ]
      }
    };
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.resetForm();
      }
    }
  },
  computed: {
    // 根据任务状态和用户权限显示不同的操作按钮
    showApproveButton() {
      return this.task.status === 'PENDING' && this.hasPermission('approval:task:approve');
    },
    showRejectButton() {
      return this.task.status === 'PENDING' && this.hasPermission('approval:task:reject');
    },
    showTransferButton() {
      return this.task.status === 'PENDING' && this.hasPermission('approval:task:transfer');
    },
    // 根据用户权限决定操作按钮的可见性
    canApprove() {
      return this.hasPermission('approval:task:approve');
    },
    canReject() {
      return this.hasPermission('approval:task:reject');
    },
    canTransfer() {
      return this.hasPermission('approval:task:transfer');
    }
  },
  methods: {
    resetForm() {
      this.form = {
        comment: '',
        rejectNodeId: null,
        transferUserId: null,
        action: ''
      };
      this.isRejecting = false;
      this.isTransferring = false;
      if (this.$refs.approvalForm) {
        this.$refs.approvalForm.resetFields();
      }
    },

    async handleApprove() {
      this.form.action = 'APPROVE';
      this.isRejecting = false;
      this.isTransferring = false;
      await this.submitApproval();
    },

    async handleReject() {
      this.form.action = 'REJECT';
      this.isRejecting = true;
      this.isTransferring = false;
      await this.submitApproval();
    },

    async handleTransfer() {
      this.form.action = 'TRANSFER';
      this.isRejecting = false;
      this.isTransferring = true;
      await this.submitApproval();
    },

    async submitApproval() {
      this.$refs.approvalForm.validate(async (valid) => {
        if (valid) {
          this.submitting = true;
          try {
            // 构造请求参数
            const params = {
              comment: this.form.comment
            };

            let result;
            switch (this.form.action) {
              case 'APPROVE':
                // 调用同意API，传递正确的参数格式
                result = await approveTask(this.task.id, this.form.comment);
                break;
              case 'REJECT':
                // 调用驳回API，传递正确的参数格式
                result = await rejectTask(this.task.id, this.form.comment);
                break;
              case 'TRANSFER':
                // 调用转办API，传递正确的参数格式
                result = await transferTask(this.task.id, this.form.transferUserId, this.form.comment);
                break;
              default:
                break;
            }

            if (result && result.success) {
              this.$message.success('操作成功');
              this.$emit('success');
              this.handleCancel();
            } else {
              this.$message.error((result && result.message) || '操作失败');
            }
          } catch (error) {
            this.$message.error('操作失败：' + (error.message || ''));
          } finally {
            this.submitting = false;
          }
        }
      });
    },

    handleCancel() {
      this.$emit('update:visible', false);
      this.$emit('cancel');
      this.resetForm();
    }
  }
};
</script>

<style scoped>
/* Dialog styling */
.approval-process-dialog ::v-deep .el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.approval-process-dialog ::v-deep .el-dialog__header {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  color: #ffffff;
  padding: 16px 20px;
}

.approval-process-dialog ::v-deep .el-dialog__title {
  color: #ffffff;
  font-weight: 600;
  font-size: 16px;
}

.approval-process-dialog ::v-deep .el-dialog__body {
  padding: 24px 20px;
  background: #ffffff;
}

/* Form styling */
.dialog-form {
  margin: 0;
}

.dialog-form ::v-deep .el-form-item__label {
  color: #5a4fcf;
  font-weight: 600;
  font-size: 14px;
}

/* Section styling */
.business-detail,
.approval-form {
  margin-bottom: 24px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  padding-bottom: 12px;
  border-bottom: 2px solid #e8e8f0;
}

.section-title i {
  font-size: 18px;
  color: #7B68EE;
}

.section-divider {
  margin: 0;
}

/* Readonly input styling */
.readonly-input ::v-deep .el-input__inner {
  background: #f5f7fa;
  color: #909399;
  cursor: not-allowed;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
  font-size: 14px;
  padding: 0 15px;
  height: 36px;
  line-height: 36px;
}

.readonly-textarea ::v-deep .el-textarea__inner {
  background: #f5f7fa;
  color: #909399;
  cursor: not-allowed;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
  font-size: 14px;
  padding: 10px 15px;
  line-height: 1.6;
  resize: none;
}

/* Form textarea styling */
.form-textarea ::v-deep .el-textarea__inner {
  border-radius: 6px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
  font-size: 14px;
  padding: 10px 15px;
  line-height: 1.6;
  resize: vertical;
}

.form-textarea ::v-deep .el-textarea__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.form-textarea ::v-deep .el-textarea__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

/* Form select styling */
.form-select ::v-deep .el-input__inner {
  border-radius: 6px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
  font-size: 14px;
  padding: 0 15px;
  height: 36px;
  line-height: 36px;
}

.form-select ::v-deep .el-input__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.form-select ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

.form-select ::v-deep .el-input__suffix {
  right: 10px;
}

/* Dialog footer */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e8e8f0;
}

/* Action button styling */
.action-btn {
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 600;
  transition: all 0.3s ease;
  border: none;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.approve-btn {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: #fff;
}

.approve-btn:hover {
  background: linear-gradient(135deg, #85ce61 0%, #a0d868 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
}

.reject-btn {
  background: linear-gradient(135deg, #f56c6c 0%, #ff6b6b 100%);
  color: #fff;
}

.reject-btn:hover {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8a80 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.3);
}

.transfer-btn {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #fff;
}

.transfer-btn:hover {
  background: linear-gradient(135deg, #66b1ff 0%, #8cc5ff 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
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

/* Form validation error styling */
.dialog-form ::v-deep .el-form-item.is-error .el-input__inner,
.dialog-form ::v-deep .el-form-item.is-error .el-textarea__inner {
  border-color: #f56c6c;
  animation: shake 0.3s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

.dialog-form ::v-deep .el-form-item__error {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
  padding-left: 4px;
}

/* Divider styling */
.approval-process-dialog ::v-deep .el-divider--horizontal {
  background-color: #e8e8f0;
}

/* Responsive design */
@media screen and (max-width: 768px) {
  .approval-process-dialog ::v-deep .el-dialog {
    width: 90% !important;
  }

  .approval-process-dialog ::v-deep .el-dialog__body {
    padding: 20px 16px;
  }

  .dialog-footer {
    flex-wrap: wrap;
  }

  .action-btn {
    flex: 1;
    min-width: 80px;
    justify-content: center;
  }
}

@media screen and (max-width: 480px) {
  .approval-process-dialog ::v-deep .el-dialog {
    width: 95% !important;
  }

  .approval-process-dialog ::v-deep .el-dialog__body {
    padding: 16px 12px;
  }

  .section-title {
    font-size: 14px;
  }

  .section-title i {
    font-size: 16px;
  }

  .readonly-input ::v-deep .el-input__inner,
  .form-textarea ::v-deep .el-textarea__inner,
  .form-select ::v-deep .el-input__inner {
    height: 32px;
    line-height: 32px;
    font-size: 13px;
  }

  .readonly-textarea ::v-deep .el-textarea__inner {
    font-size: 13px;
  }

  .action-btn {
    font-size: 13px;
    padding: 8px 16px;
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

.business-detail,
.approval-form {
  animation: fadeIn 0.4s ease-out;
}
</style>