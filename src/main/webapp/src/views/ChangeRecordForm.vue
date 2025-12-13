<template>
  <el-card>
    <div slot="header" class="clearfix">
      <span>变更记录表单</span>
    </div>
    <el-form ref="form" :model="form" :rules="rules" label-width="0">
      <el-descriptions :column="3" border>
        <!-- 第一行 -->
        <el-descriptions-item label="版本号">
          <el-form-item>
            <el-input v-model="form.version" :disabled="true" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="发版日期">
          <el-form-item prop="releaseDate">
            <el-date-picker v-model="form.releaseDate" type="date" placeholder="选择日期" style="width: 100%" value-format="yyyy-MM-dd" :disabled="!isFieldEditable('releaseDate')" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="分支名称">
          <el-form-item prop="branchName">
            <el-input v-model="form.branchName" :disabled="!isFieldEditable('branchName')" />
          </el-form-item>
        </el-descriptions-item>
        
        <!-- 第二行 -->
        <el-descriptions-item label="当前状态">
          <el-form-item prop="currentStatus">
            <el-select v-model="form.currentStatus" placeholder="选择状态" style="width: 100%" :disabled="!isFieldEditable('currentStatus')">
              <el-option v-for="item in currentStatusOptions" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
            </el-select>
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="缺陷编号">
          <el-form-item prop="defectNumber">
            <el-input v-model="form.defectNumber" :disabled="!isFieldEditable('defectNumber')" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="服务名称">
          <el-form-item prop="serviceName">
            <el-input v-model="form.serviceName" :disabled="!isFieldEditable('serviceName')" />
          </el-form-item>
        </el-descriptions-item>
        
        <!-- 第三行 -->
        <el-descriptions-item label="组别">
          <el-form-item prop="groupName">
            <el-input v-model="form.groupName" :disabled="!isFieldEditable('groupName')" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="开发负责人">
          <el-form-item prop="developer">
            <el-input v-model="form.developer" :disabled="!isFieldEditable('developer')" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="开发类别">
          <el-form-item prop="developType">
            <el-select v-model="form.developType" placeholder="选择开发类别" style="width: 100%" :disabled="!isFieldEditable('developType')">
              <el-option v-for="item in developTypeOptions" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
            </el-select>
          </el-form-item>
        </el-descriptions-item>
        
        <!-- 第四行 -->
        <el-descriptions-item label="是否涉及外围系统">
          <el-form-item prop="involveExternalSystem">
            <el-switch v-model="form.involveExternalSystem" :active-value="1" :inactive-value="0" :disabled="!isFieldEditable('involveExternalSystem')" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="是否跨服务">
          <el-form-item>
            <el-switch v-model="form.crossService" :active-value="1" :inactive-value="0" :disabled="!isFieldEditable('crossService')" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="">
          <!-- 占位，保持3列布局 -->
        </el-descriptions-item>
      </el-descriptions>
      
      <!-- 文本域部分，1列布局 -->
      <el-descriptions :column="1" border>
        <el-descriptions-item label="问题描述">
          <el-form-item prop="problemDescription">
            <el-input v-model="form.problemDescription" type="textarea" rows="3" :disabled="!isFieldEditable('problemDescription')" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="变更描述">
          <el-form-item prop="changeDesc">
            <el-input v-model="form.changeDesc" type="textarea" rows="3" :disabled="!isFieldEditable('changeDesc')" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="问题影响分析">
          <el-form-item prop="impactAnalysis">
            <el-input v-model="form.impactAnalysis" type="textarea" rows="3" :disabled="!isFieldEditable('impactAnalysis')" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="解决方案">
          <el-form-item prop="solution">
            <el-input v-model="form.solution" type="textarea" rows="3" :disabled="!isFieldEditable('solution')" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="代码清单">
          <el-form-item prop="codeList">
            <el-input v-model="form.codeList" type="textarea" rows="3" :disabled="!isFieldEditable('codeList')" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="备注说明">
          <el-form-item>
            <el-input v-model="form.remark" type="textarea" rows="3" :disabled="!isFieldEditable('remark')" />
          </el-form-item>
        </el-descriptions-item>
      </el-descriptions>
    </el-form>
  </el-card>
</template>

<script>
import { getDictItemsByType } from '../api';

export default {
  name: 'ChangeRecordForm',
  props: {
    form: { type: Object, required: true },
    canEdit: { type: Boolean, default: false },
    approvalInfo: {
      type: Object,
      default: () => ({
        approvalStatus: '',
        currentNodeId: '',
        currentApproverId: '',
        isCurrentApprover: false,
        isHistoricalApprover: false,
        approvalLog: []
      })
    }
  },
  data(){
    return {
      rules: {
        releaseDate: [
            {
              required: true,
              message: "请选择发版日期",
              trigger: "change",
            },
          ],
          branchName: [
            {
              required: true,
              message: "请输入分支名称",
              trigger: "blur",
            },
          ],
          currentStatus: [
            {
              required: true,
              message: "请选择当前状态",
              trigger: "change",
            },
          ],
          defectNumber: [
            {
              required: true,
              message: "请输入缺陷编号",
              trigger: "blur",
            },
          ],
          groupName: [
            {
              required: true,
              message: "请输入组别",
              trigger: "blur",
            },
          ],
          developer: [
            {
              required: true,
              message: "请输入开发负责人",
              trigger: "blur",
            },
          ],
          developType: [
            {
              required: true,
              message: "请选择开发类别",
              trigger: "change",
            },
          ],
          problemDescription: [
            {
              required: true,
              message: "请输入问题描述",
              trigger: "blur",
            },
          ],
          changeDesc: [
            {
              required: true,
              message: "请输入变更描述",
              trigger: "blur",
            },
          ],
          impactAnalysis: [
            {
              required: true,
              message: "请输入问题影响分析",
              trigger: "blur",
            },
          ],
          solution: [
            {
              required: true,
              message: "请输入解决方案",
              trigger: "blur",
            },
          ],
          codeList: [
            {
              required: true,
              message: "请输入代码清单",
              trigger: "blur",
            },
          ],
      },
      // 字典选项
      currentStatusOptions: [],
      developTypeOptions: [],
      // 定义所有表单字段
      allFields: [
        'releaseDate', 'branchName', 'currentStatus', 'defectNumber',
        'serviceName', 'groupName', 'developer', 'developType',
        'problemDescription', 'changeDesc', 'impactAnalysis', 'solution',
        'codeList', 'involveExternalSystem', 'crossService', 'remark'
      ],
      // 审批相关字段
      approvalFields: [
        'currentStatus', 'approvalRemark', 'approvalAction'
      ]
    }
  },
  computed: {
    // 判断是否处于审批中状态
    isApprovalInProgress() {
      // 审批中状态判断
      const currentStatus = this.form.currentStatus || '';
      return currentStatus.includes('审批中') || currentStatus === '已提交审批';
    },
    
    // 获取当前用户信息
    currentUser() {
      try {
        const raw = localStorage.getItem('user');
        return raw ? JSON.parse(raw) : { id: '', username: '', usernumb: '' };
      } catch (e) {
        return { id: '', username: '', usernumb: '' };
      }
    },
    
    // 获取当前用户角色
    userRole() {
      const user = this.currentUser;
      const rawId = (user && (user.usernumb || user.username)) || '';
      const id = String(rawId).toUpperCase();
      
      // 管理员角色判断
      const adminUsers = ['R0001', 'BG001', 'BG002'];
      if (adminUsers.includes(id)) {
        return 'R0001';
      }
      
      // 审批者角色判断
      const approvalInfo = this.approvalInfo || {};
      if (approvalInfo.isCurrentApprover) {
        return 'CURRENT_APPROVER';
      }
      if (approvalInfo.isHistoricalApprover) {
        return 'HISTORICAL_APPROVER';
      }
      
      // 创建者角色判断
      const createdBy = this.form.createdBy || '';
      if (createdBy.toUpperCase() === id) {
        return 'CREATOR';
      }
      
      // 默认角色
      return 'VIEWER';
    },
    
    // 判断是否为草稿状态
    isDraftStatus() {
      const currentStatus = this.form.currentStatus || '';
      return currentStatus === '待审批' || currentStatus === '草稿' || currentStatus === '01';
    },
    
    // 获取可编辑字段列表
    editableFields() {
      return this.getEditableFields();
    },
    
    // 扩展现有canEdit逻辑，保持兼容性
    enhancedCanEdit() {
      // 管理员始终可编辑
      if (this.userRole === 'R0001') {
        return true;
      }
      
      // 原有canEdit逻辑
      if (!this.canEdit) {
        return false;
      }
      
      // 审批中状态下的特殊处理
      if (this.isApprovalInProgress) {
        return this.userRole === 'CURRENT_APPROVER';
      }
      
      // 草稿状态下创建者可编辑
      if (this.isDraftStatus && this.userRole === 'CREATOR') {
        return true;
      }
      
      return false;
    }
  },
  methods: {
    // 获取当前用户ID
    getCurrentUserId() {
      const user = this.currentUser;
      return (user.usernumb || user.username || '').toUpperCase();
    },
    
    // 审批权限检查函数：判断用户是否可以执行特定审批任务
    hasApprovalPermission(taskType) {
      const { userRole, approvalInfo } = this;
      
      // 管理员拥有所有权限
      if (userRole === 'R0001') {
        return true;
      }
      
      // 审批中状态检查
      const isApprovalActive = this.isApprovalInProgress;
      
      switch (taskType) {
        case 'APPROVE':
        case 'REJECT':
        case 'TRANSFER':
          // 当前审批人可以执行审批操作
          return isApprovalActive && userRole === 'CURRENT_APPROVER';
        case 'SUBMIT':
          // 创建者可以提交审批
          return !isApprovalActive && userRole === 'CREATOR' && this.isDraftStatus;
        default:
          return false;
      }
    },
    
    // 字段权限检查函数：返回可编辑字段列表
    getEditableFields() {
      const { userRole, isApprovalInProgress, isDraftStatus } = this;
      
      // 管理员可编辑所有字段
      if (userRole === 'R0001') {
        return [...this.allFields, ...this.approvalFields];
      }
      
      // 当前审批人只能编辑审批相关字段
      if (isApprovalInProgress && userRole === 'CURRENT_APPROVER') {
        return this.approvalFields;
      }
      
      // 草稿状态下创建者可编辑所有字段
      if (isDraftStatus && userRole === 'CREATOR') {
        return this.allFields;
      }
      
      // 历史审批人和普通查看者只能查看
      return [];
    },
    
    // 判断特定字段是否可编辑
    isFieldEditable(fieldName) {
      // 版本号始终不可编辑
      if (fieldName === 'version') {
        return false;
      }
      
      // 检查字段是否在可编辑列表中
      return this.editableFields.includes(fieldName);
    },
    
    async loadDictData() {
      try {
        // 加载当前状态字典
        const statusRes = await getDictItemsByType('CURRENT_STATUS');
        this.currentStatusOptions = statusRes.data || [];
        
        // 加载开发类别字典
        const developRes = await getDictItemsByType('DEVELOP_TYPE');
        this.developTypeOptions = developRes.data || [];
      } catch (error) {
        console.error('加载字典数据失败:', error);
      }
    }
  },
  mounted() {
    // 加载字典数据
    this.loadDictData();
  }
};
</script>

