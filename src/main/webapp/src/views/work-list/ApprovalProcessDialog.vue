<template>
  <el-dialog
    :title="title"
    :visible.sync="visible"
    width="700px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @close="handleCancel"
  >
    <el-form ref="approvalForm" :model="form" :rules="rules" label-width="100px">
      <!-- 业务数据详情 -->
      <div class="business-detail">
        <h3>业务数据详情</h3>
        <el-divider></el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="缺陷编号">
              <el-input v-model="businessData.defectNumber" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="服务名称">
              <el-input v-model="businessData.serviceName" readonly />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="组别">
              <el-input v-model="businessData.groupName" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开发负责人">
              <el-input v-model="businessData.developer" readonly />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开发类别">
              <el-input v-model="businessData.developType" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前状态">
              <el-input v-model="businessData.currentStatus" readonly />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="问题描述">
          <el-input v-model="businessData.problemDescription" type="textarea" rows="3" readonly />
        </el-form-item>
        <el-form-item label="变更描述">
          <el-input v-model="businessData.changeDesc" type="textarea" rows="3" readonly />
        </el-form-item>
      </div>

      <!-- 审批处理表单 -->
      <div class="approval-form">
        <h3>审批处理</h3>
        <el-divider></el-divider>
        
        <!-- 审批意见 -->
        <el-form-item label="审批意见" prop="comment">
          <el-input
            v-model="form.comment"
            type="textarea"
            rows="4"
            placeholder="请输入审批意见"
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
            style="width: 100%"
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
            style="width: 100%"
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
      >
        同意
      </el-button>

      <!-- 驳回按钮 -->
      <el-button
        type="danger"
        :loading="submitting"
        @click="handleReject"
        v-if="canReject"
      >
        驳回
      </el-button>

      <!-- 转办按钮 -->
      <el-button
        :loading="submitting"
        @click="handleTransfer"
        v-if="canTransfer"
      >
        转办
      </el-button>

      <!-- 取消按钮 -->
      <el-button @click="handleCancel">
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
.business-detail {
  margin-bottom: 20px;
}

.business-detail h3,
.approval-form h3 {
  margin-bottom: 10px;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>