<template>
  <div class="change-record-form">
    <el-form ref="form" :model="form" :rules="rules" label-width="0" class="form-wrapper">
      <el-descriptions :column="3" border class="descriptions-section">
        <template slot="title">
          <div class="section-title">
            <i class="el-icon-document"></i>
            <span>基本信息</span>
          </div>
        </template>
        <!-- 第一行 -->
        <el-descriptions-item label="版本号">
          <el-form-item>
            <el-input v-model="form.version" :disabled="true" class="readonly-input" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="发版日期">
          <el-form-item prop="releaseDate">
            <el-date-picker v-model="form.releaseDate" type="date" placeholder="选择日期" style="width: 100%"
              value-format="yyyy-MM-dd" class="date-picker-input" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="源分支">
          <el-form-item prop="sourceBranch">
            <el-input v-model="form.sourceBranch" class="text-input" />
          </el-form-item>
        </el-descriptions-item>
        
        <!-- 新增行：目标分支 -->
        <el-descriptions-item label="目标分支">
          <el-form-item prop="targetBranch">
            <el-input v-model="form.targetBranch" class="text-input" />
          </el-form-item>
        </el-descriptions-item>

        <!-- 第二行 -->
        <el-descriptions-item label="当前状态">
          <el-form-item prop="currentStatus">
            <el-select v-model="form.currentStatus" placeholder="选择状态" style="width: 100%" class="select-input">
              <el-option v-for="item in currentStatusOptions" :key="item.dictValue" :label="item.dictLabel"
                :value="item.dictValue" />
            </el-select>
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="缺陷编号">
          <el-form-item prop="defectNumber">
            <el-input v-model="form.defectNumber" class="text-input" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="服务名称">
          <el-form-item prop="serviceName">
            <el-input v-model="form.serviceName" class="text-input" />
          </el-form-item>
        </el-descriptions-item>

        <!-- 第三行 -->
        <el-descriptions-item label="组别">
          <el-form-item prop="groupName">
            <el-input v-model="form.groupName" class="text-input" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="开发负责人编号">
          <el-form-item prop="developerNum">
            <el-input v-model="form.developerNum" class="text-input" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="开发负责人姓名">
          <el-form-item prop="developerName">
            <el-input v-model="form.developerName" class="text-input" />
          </el-form-item>
        </el-descriptions-item>
        
        <!-- 新增行：开发类别 -->
        <el-descriptions-item label="开发类别">
          <el-form-item prop="developType">
            <el-select v-model="form.developType" placeholder="选择开发类别" style="width: 100%" class="select-input">
              <el-option v-for="item in developTypeOptions" :key="item.dictValue" :label="item.dictLabel"
                :value="item.dictValue" />
            </el-select>
          </el-form-item>
        </el-descriptions-item>

        <!-- 第四行 -->
        <el-descriptions-item label="是否涉及外围系统">
          <el-form-item prop="involveExternalSystem">
            <el-switch v-model="form.involveExternalSystem" :active-value="1" :inactive-value="0" class="switch-input" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="是否跨服务">
          <el-form-item>
            <el-switch v-model="form.crossService" :active-value="1" :inactive-value="0" class="switch-input" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="是否包含脚本">
          <el-form-item>
            <el-switch v-model="form.includeShell" :active-value="1" :inactive-value="0" class="switch-input" />
          </el-form-item>
        </el-descriptions-item>
        
        <!-- 新增行：脚本路径 -->
        <el-descriptions-item label="脚本清单">
          <el-form-item prop="shellPath">
            <el-input v-model="form.shellPath" type="textarea" rows="2" class="textarea-input" />
          </el-form-item>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 文本域部分，1列布局 -->
      <el-descriptions :label-style="{
        width: '150px',
        minWidth: '100px',
      }" :column="1" border class="descriptions-section">
        <template slot="title">
          <div class="section-title">
            <i class="el-icon-edit-outline"></i>
            <span>详细信息</span>
          </div>
        </template>
        <el-descriptions-item label="问题描述">
          <el-form-item prop="problemDescription">
            <el-input v-model="form.problemDescription" type="textarea" rows="3" class="textarea-input" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="变更描述">
          <el-form-item prop="changeDesc">
            <el-input v-model="form.changeDesc" type="textarea" rows="3" class="textarea-input" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="问题影响分析">
          <el-form-item prop="impactAnalysis">
            <el-input v-model="form.impactAnalysis" type="textarea" rows="3" class="textarea-input" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="解决方案">
          <el-form-item prop="solutionDescription">
            <el-input v-model="form.solutionDescription" type="textarea" rows="3" class="textarea-input" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="代码清单">
          <el-form-item prop="codeList">
            <el-input v-model="form.codeList" type="textarea" rows="3" class="textarea-input" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="配置说明">
          <el-form-item prop="configList">
            <el-input v-model="form.configList" type="textarea" rows="3" class="textarea-input" />
          </el-form-item>
        </el-descriptions-item>
        <el-descriptions-item label="备注说明">
          <el-form-item>
            <el-input v-model="form.remark" type="textarea" rows="3" class="textarea-input" />
          </el-form-item>
        </el-descriptions-item>
      </el-descriptions>
    </el-form>
  </div>
</template>

<script>
import { getDictItemsByType } from '../api';

export default {
  name: 'ChangeRecordForm',
  props: {
    form: { type: Object, required: true },
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
  data() {
    return {
      rules: {
        releaseDate: [
          {
            required: true,
            message: "请选择发版日期",
            trigger: "change",
          },
        ],
        sourceBranch: [
          {
            required: true,
            message: "请输入源分支",
            trigger: "blur",
          },
        ],
        targetBranch: [
          {
            required: true,
            message: "请输入目标分支",
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
        developerNum: [
          {
            required: true,
            message: "请输入开发负责人编号",
            trigger: "blur",
          },
        ],
        developerName: [
          {
            required: true,
            message: "请输入开发负责人姓名",
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
        solutionDescription: [
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
      developTypeOptions: []
    }
  },
  methods: {
    async loadDictData() {
      try {
        // 加载当前状态字典
        const statusRes = await getDictItemsByType('CURRENT_STATUS');
        this.currentStatusOptions = statusRes || [];

        // 加载开发类别字典
        const developRes = await getDictItemsByType('DEVELOP_TYPE');
        this.developTypeOptions = developRes || [];
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

<style scoped>
.change-record-form {
  width: 100%;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eaf6 100%);
  min-height: calc(100vh - 60px);
}

.form-wrapper {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(123, 104, 238, 0.08);
  padding: 24px;
  margin-bottom: 20px;
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
  color: #7B68EE;
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
}

.descriptions-section ::v-deep .el-descriptions-item__content {
  padding: 12px 20px;
  border-color: #e8e8f0;
}

.descriptions-section ::v-deep .el-descriptions__border {
  border-color: #e8e8f0;
}

/* Form item styling */
.descriptions-section ::v-deep .el-form-item {
  margin-bottom: 0;
}

/* Text input styling */
.text-input ::v-deep .el-input__inner {
  border-radius: 6px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
  font-size: 14px;
  padding: 0 15px;
  height: 36px;
  line-height: 36px;
}

.text-input ::v-deep .el-input__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.text-input ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
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

/* Date picker styling */
.date-picker-input ::v-deep .el-input__inner {
  border-radius: 6px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
  font-size: 14px;
  padding: 0 15px;
  height: 36px;
  line-height: 36px;
}

.date-picker-input ::v-deep .el-input__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.date-picker-input ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

.date-picker-input ::v-deep .el-input__prefix {
  left: 10px;
}

.date-picker-input ::v-deep .el-input__suffix {
  right: 10px;
}

/* Select input styling */
.select-input ::v-deep .el-input__inner {
  border-radius: 6px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
  font-size: 14px;
  padding: 0 15px;
  height: 36px;
  line-height: 36px;
}

.select-input ::v-deep .el-input__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.select-input ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

.select-input ::v-deep .el-input__suffix {
  right: 10px;
}

/* Switch styling */
.switch-input ::v-deep .el-switch {
  height: 24px;
}

.switch-input ::v-deep .el-switch__core {
  border-radius: 12px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
}

.switch-input ::v-deep .el-switch.is-checked .el-switch__core {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  border-color: #7B68EE;
}

.switch-input ::v-deep .el-switch__core:after {
  border-radius: 50%;
  transition: all 0.3s ease;
}

.switch-input ::v-deep .el-switch.is-checked .el-switch__core:after {
  background: #ffffff;
}

/* Textarea styling */
.textarea-input ::v-deep .el-textarea__inner {
  border-radius: 6px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
  font-size: 14px;
  padding: 10px 15px;
  line-height: 1.6;
  resize: vertical;
}

.textarea-input ::v-deep .el-textarea__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.textarea-input ::v-deep .el-textarea__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

/* Form validation error styling */
.descriptions-section ::v-deep .el-form-item.is-error .el-input__inner,
.descriptions-section ::v-deep .el-form-item.is-error .el-textarea__inner {
  border-color: #f56c6c;
  animation: shake 0.3s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

.descriptions-section ::v-deep .el-form-item__error {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
  padding-left: 4px;
}

/* Responsive design */
@media (max-width: 1200px) {
  .change-record-form {
    padding: 16px;
  }

  .form-wrapper {
    padding: 20px;
  }

  .descriptions-section ::v-deep .el-descriptions-item__label {
    padding: 12px 16px;
  }

  .descriptions-section ::v-deep .el-descriptions-item__content {
    padding: 10px 16px;
  }
}

@media (max-width: 768px) {
  .change-record-form {
    padding: 12px;
  }

  .form-wrapper {
    padding: 16px;
  }

  .section-title {
    font-size: 14px;
    padding: 12px 16px;
  }

  .section-title i {
    font-size: 16px;
  }

  .descriptions-section ::v-deep .el-descriptions-item__label {
    padding: 10px 12px;
    font-size: 13px;
  }

  .descriptions-section ::v-deep .el-descriptions-item__content {
    padding: 8px 12px;
  }

  .text-input ::v-deep .el-input__inner,
  .readonly-input ::v-deep .el-input__inner,
  .date-picker-input ::v-deep .el-input__inner,
  .select-input ::v-deep .el-input__inner {
    height: 32px;
    line-height: 32px;
    font-size: 13px;
  }

  .textarea-input ::v-deep .el-textarea__inner {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .change-record-form {
    padding: 8px;
  }

  .form-wrapper {
    padding: 12px;
    border-radius: 8px;
  }

  .section-title {
    font-size: 13px;
    padding: 10px 12px;
  }

  .descriptions-section ::v-deep .el-descriptions-item__label {
    padding: 8px 10px;
    font-size: 12px;
  }

  .descriptions-section ::v-deep .el-descriptions-item__content {
    padding: 6px 10px;
  }

  .text-input ::v-deep .el-input__inner,
  .readonly-input ::v-deep .el-input__inner,
  .date-picker-input ::v-deep .el-input__inner,
  .select-input ::v-deep .el-input__inner {
    height: 30px;
    line-height: 30px;
    font-size: 12px;
    padding: 0 12px;
  }

  .textarea-input ::v-deep .el-textarea__inner {
    font-size: 12px;
    padding: 8px 12px;
  }
}

/* Print styles */
@media print {
  .change-record-form {
    background: #ffffff;
    padding: 0;
  }

  .form-wrapper {
    box-shadow: none;
    padding: 0;
  }

  .section-title {
    background: #7B68EE !important;
    color: #ffffff !important;
    -webkit-print-color-adjust: exact;
    print-color-adjust: exact;
  }
}
</style>
