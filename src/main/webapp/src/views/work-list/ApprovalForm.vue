<template>
  <div class="approval-form-container">
    <form-validator ref="validator" :rules="formRules" />
    <el-card shadow="hover" class="approval-card">
      <template slot="header">
        <div class="card-header">
          <h2 class="card-title">
            <i class="el-icon-edit-outline"></i>
            审批申请表单
          </h2>
        </div>
      </template>
      <el-form :model="formData" ref="approvalForm" label-width="140px" class="approval-form">
        <!-- 业务类型选择 -->
        <el-form-item label="业务类型" prop="businessType">
          <el-select v-model="formData.businessType" placeholder="请选择业务类型" @change="handleBusinessTypeChange" :disabled="isBusinessTypeDisabled" class="select-input">
            <el-option
              v-for="type in businessTypes"
              :key="type.typeCode"
              :label="type.typeName"
              :value="type.typeCode"
            ></el-option>
          </el-select>
        </el-form-item>

        <!-- 基本信息 -->
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入申请标题" class="text-input"></el-input>
        </el-form-item>

        <el-form-item label="申请原因" prop="reason">
          <el-input v-model="formData.reason" type="textarea" :rows="4" placeholder="请输入申请原因" class="textarea-input"></el-input>
        </el-form-item>

            <!-- 动态表单字段 -->
        <template v-if="currentBusinessType && dynamicFields.length > 0">
          <!-- 根据业务类型动态渲染不同的表单字段 -->
          <el-form-item
            v-for="field in dynamicFields"
            :key="field.fieldName"
            :label="field.fieldLabel"
            :prop="field.fieldName"
          >
            <el-input
              v-if="field.fieldType === 'input'"
              v-model="formData[field.fieldName]"
              :placeholder="field.props.placeholder"
              :clearable="field.props.clearable"
              :type="field.props.type || 'text'"
              class="text-input"
            ></el-input>
            
            <el-select
              v-else-if="field.fieldType === 'select'"
              v-model="formData[field.fieldName]"
              :placeholder="field.props.placeholder"
              :clearable="field.props.clearable"
              class="select-input"
            >
              <el-option
                v-for="option in field.options"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              ></el-option>
            </el-select>
            
            <el-input
              v-else-if="field.fieldType === 'textarea'"
              v-model="formData[field.fieldName]"
              :type="'textarea'"
              :placeholder="field.props.placeholder"
              :rows="field.props.rows || 4"
              :clearable="field.props.clearable"
              class="textarea-input"
            ></el-input>
            
            <el-date-picker
              v-else-if="field.fieldType === 'date' || field.fieldType === 'datetime'"
              v-model="formData[field.fieldName]"
              :type="field.fieldType"
              :placeholder="field.props.placeholder"
              :format="field.props.format || (field.fieldType === 'datetime' ? 'yyyy-MM-dd HH:mm:ss' : 'yyyy-MM-dd')"
              :value-format="field.props.valueFormat || (field.fieldType === 'datetime' ? 'yyyy-MM-dd HH:mm:ss' : 'yyyy-MM-dd')"
              class="date-picker-input"
            ></el-date-picker>
          </el-form-item>
        </template>

        <!-- 附件上传 -->
        <el-form-item label="附件">
          <el-upload
            action=""
            :on-preview="handlePreview"
            :on-remove="handleRemove"
            list-type="picture-card"
            :file-list="fileList"
            :auto-upload="false"
            class="upload-component"
          >
            <i class="el-icon-plus upload-icon"></i>
          </el-upload>
          <el-dialog :visible.sync="dialogVisible" class="preview-dialog">
            <img width="100%" :src="dialogImageUrl" alt="">
          </el-dialog>
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item class="form-actions">
          <el-button type="primary" @click="submitForm" class="submit-btn">
            <i class="el-icon-check"></i>
            提交审批
          </el-button>
          <el-button @click="saveDraft" class="draft-btn">
            <i class="el-icon-document"></i>
            保存草稿
          </el-button>
          <el-button @click="resetForm" class="reset-btn">
            <i class="el-icon-refresh-left"></i>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import FormValidator from '@/components/FormValidator.vue';
import ApprovalService from '@/services/ApprovalService';
import eventBus from '@/utils/eventBus';
import { getBusinessTypes } from '../../api';

export default {
  name: 'ApprovalForm',
  components: {
    FormValidator
  },
  data() {
    return {
      formData: {
        businessType: '',
        title: '',
        reason: '',
        changeRecordCode: '',
        changeRecordId: '',
        version: '',
        releaseDate: '',
        dbName: '',
        sqlScript: '',
        configName: '',
        configType: ''
      },
      formRules: {
        businessType: { required: true },
        title: { required: true, maxLength: 100 },
        reason: { required: true, maxLength: 500 }
      },
      businessTypes: [],
      currentBusinessType: null,
      // 动态表单字段配置
      dynamicFields: [],
      fileList: [],
      dialogImageUrl: '',
      dialogVisible: false,
      // 业务类型是否禁用
      isBusinessTypeDisabled: false
    }
  },
  mounted() {
    this.loadBusinessTypes()
    // 读取路由参数，设置默认值
    this.initFromRouteParams()
  },
  methods: {
    // 加载业务类型
    loadBusinessTypes() {
      // 调用API获取业务类型列表
      getBusinessTypes()
        .then(response => {
          this.businessTypes = response || [] // 修复：直接使用response，因为响应拦截器已处理
          // 业务类型加载完成后，再次初始化路由参数，确保业务类型已加载
          this.initFromRouteParams()
        })
        .catch(error => {
          this.$message.error('加载业务类型失败')
          console.error('加载业务类型失败:', error)
          this.businessTypes = []
        })
    },
    
    // 从路由参数初始化表单数据
    initFromRouteParams() {
      // 获取路由参数
      const { businessId, businessType, businessCode } = this.$route.query
      
      // 设置业务类型是否禁用：如果有路由参数businessType，则禁用，否则启用
      this.isBusinessTypeDisabled = !!businessType
      
      // 设置表单数据
      if (businessType) {
        this.formData.businessType = businessType
        // 如果业务类型已加载，触发业务类型变化处理
        if (this.businessTypes.length > 0) {
          this.handleBusinessTypeChange(businessType)
        }
      }
      
      if (businessId) {
        // 根据业务类型设置对应的ID字段
        if (businessType === 'CHANGE_RECORD') {
          this.formData.changeRecordId = businessId
        }
      }
      
      if (businessCode) {
        // 根据业务类型设置对应的编码字段
        if (businessType === 'CHANGE_RECORD') {
          this.formData.changeRecordCode = businessCode
        }
      }
    },

    // 业务类型变化处理
    handleBusinessTypeChange(value) {
      this.currentBusinessType = this.businessTypes.find(type => type.typeCode === value)
      // 重置动态字段
      this.resetDynamicFields()
      // 加载动态表单字段
      this.loadDynamicFields()
    },

    // 重置动态字段
    resetDynamicFields() {
      // 重置所有动态字段
      Object.keys(this.formData).forEach(key => {
        if (!['businessType', 'title', 'reason'].includes(key)) {
          this.formData[key] = ''
        }
      })
    },

    // 加载动态表单字段
    loadDynamicFields() {
      if (!this.currentBusinessType) {
        this.dynamicFields = []
        return
      }

      // 根据业务类型加载动态表单字段
      switch (this.currentBusinessType.typeCode) {
        case 'CHANGE_RECORD':
          this.dynamicFields = [
            {
              fieldName: 'changeRecordCode',
              fieldLabel: '变更记录编码',
              fieldType: 'input',
              props: {
                type: 'input',
                placeholder: '请输入变更记录编码',
                clearable: true
              },
              rules: {
                required: true,
                maxLength: 50
              }
            },
            {
              fieldName: 'changeRecordId',
              fieldLabel: '变更记录ID',
              fieldType: 'input',
              props: {
                type: 'number',
                placeholder: '请输入变更记录ID',
                clearable: true
              },
              rules: {
                required: true,
                pattern: /^\d+$/,
                message: '请输入有效的数字ID'
              }
            }
          ]
          break
        case 'RELEASE':
          this.dynamicFields = [
            {
              fieldName: 'version',
              fieldLabel: '发版版本号',
              fieldType: 'input',
              props: {
                placeholder: '请输入发版版本号',
                clearable: true
              },
              rules: {
                required: true,
                maxLength: 50,
                pattern: /^\d+\.\d+\.\d+$/, message: '请输入有效的版本号格式，如1.0.0'
              }
            },
            {
              fieldName: 'releaseDate',
              fieldLabel: '计划发版日期',
              fieldType: 'date',
              props: {
                type: 'date',
                placeholder: '请选择计划发版日期',
                format: 'yyyy-MM-dd',
                valueFormat: 'yyyy-MM-dd'
              },
              rules: {
                required: true
              }
            }
          ]
          break
        case 'DB_CHANGE':
          this.dynamicFields = [
            {
              fieldName: 'dbName',
              fieldLabel: '数据库名称',
              fieldType: 'input',
              props: {
                placeholder: '请输入数据库名称',
                clearable: true
              },
              rules: {
                required: true,
                maxLength: 50
              }
            },
            {
              fieldName: 'sqlScript',
              fieldLabel: 'SQL脚本',
              fieldType: 'textarea',
              props: {
                type: 'textarea',
                placeholder: '请输入SQL脚本',
                rows: 6,
                clearable: true
              },
              rules: {
                required: true,
                minLength: 10
              }
            }
          ]
          break
        case 'CONFIG_CHANGE':
          this.dynamicFields = [
            {
              fieldName: 'configName',
              fieldLabel: '配置名称',
              fieldType: 'input',
              props: {
                placeholder: '请输入配置名称',
                clearable: true
              },
              rules: {
                required: true,
                maxLength: 100
              }
            },
            {
              fieldName: 'configType',
              fieldLabel: '配置类型',
              fieldType: 'select',
              props: {
                placeholder: '请选择配置类型',
                clearable: true
              },
              options: [
                { label: '系统配置', value: 'SYSTEM' },
                { label: '应用配置', value: 'APPLICATION' },
                { label: '环境配置', value: 'ENVIRONMENT' }
              ],
              rules: {
                required: true
              }
            }
          ]
          break
        default:
          this.dynamicFields = []
      }

      // 更新表单规则
      this.updateFormRules()
    },

    // 更新表单规则
    updateFormRules() {
      // 重置表单规则
      this.formRules = {
        businessType: { required: true },
        title: { required: true, maxLength: 100 },
        reason: { required: true, maxLength: 500 }
      }

      // 添加动态字段规则
      this.dynamicFields.forEach(field => {
        if (field.rules) {
          this.formRules[field.fieldName] = field.rules
        }
      })
    },

    // 获取字段组件
    getFieldComponent(fieldType) {
      return this.fieldComponents[fieldType] || 'el-input'
    },

    // 提交表单
    submitForm() {
      // 使用通用表单验证组件进行验证
      console.log('当前业务类型:', this.currentBusinessType.typeCode);
      const validationResult = this.$refs.validator.validate(this.formData)
      
      if (validationResult.valid) {
        // 调用统一审批服务提交审批
        ApprovalService.triggerApproval({
          businessId: this.formData.changeRecordId || this.formData.version,
          businessType: this.formData.businessType,
          userId: this.$store.state.user.userNum || localStorage.getItem('userNum') || '',
          metadata: {
            title: this.formData.title,
            reason: this.formData.reason,
            ...this.formData
          }
        })
        .then(response => {
          this.$message.success('提交审批成功')
          
          // 发布审批触发事件
          eventBus.emit(eventBus.events.APPROVAL_TRIGGERED, {
            businessId: this.formData.changeRecordId || this.formData.version,
            businessType: this.formData.businessType,
            userId: this.$store.state.user.userNum || localStorage.getItem('userNum') || '',
            metadata: {
              title: this.formData.title,
              reason: this.formData.reason,
              ...this.formData
            }
          });
          
          this.$router.push('/work-list/todo')
        })
        .catch(error => {
          this.$message.error('提交审批失败')
          console.error('提交审批失败:', error)
        })
      } else {
        // 显示验证错误
        const errorMessage = this.$refs.validator.getErrorString(validationResult.errors, '<br>')
        this.$message.error({
          message: errorMessage,
          dangerouslyUseHTMLString: true
        })
      }
    },

    // 保存草稿
    saveDraft() {
      // 调用API保存草稿
      this.$http.post('/api/approval/draft', this.formData)
        .then(response => {
          this.$message.success('保存草稿成功')
        })
        .catch(error => {
          this.$message.error('保存草稿失败')
          console.error('保存草稿失败:', error)
        })
    },

    // 重置表单
    resetForm() {
      // 重置表单数据
      this.formData = {
        businessType: '',
        title: '',
        reason: '',
        changeRecordCode: '',
        changeRecordId: '',
        version: '',
        releaseDate: '',
        dbName: '',
        sqlScript: '',
        configName: '',
        configType: ''
      }
      // 重置动态字段
      this.dynamicFields = []
      // 重置文件列表
      this.fileList = []
      // 重置当前业务类型
      this.currentBusinessType = null
    },

    // 预览附件
    handlePreview(file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    },

    // 删除附件
    handleRemove(file, fileList) {
      this.fileList = fileList
    }
  }
}
</script>

<style scoped>
.approval-form-container {
  width: 100%;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eaf6 100%);
  min-height: calc(100vh - 60px);
}

.approval-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(123, 104, 238, 0.08);
  margin-bottom: 20px;
}

/* Card header styling */
.card-header {
  padding: 0;
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

/* Form styling */
.approval-form {
  padding: 20px;
}

.approval-form ::v-deep .el-form-item__label {
  color: #5a4fcf;
  font-weight: 600;
  font-size: 14px;
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

/* Upload component styling */
.upload-component ::v-deep .el-upload {
  width: 100%;
}

.upload-component ::v-deep .el-upload-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.upload-component ::v-deep .el-upload-list--picture-card {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.upload-component ::v-deep .el-upload-list__item {
  border-radius: 8px;
  border: 2px solid #e8e8f0;
  transition: all 0.3s ease;
}

.upload-component ::v-deep .el-upload-list__item:hover {
  border-color: #9370DB;
  box-shadow: 0 4px 12px rgba(147, 112, 219, 0.2);
}

.upload-component ::v-deep .el-upload--picture-card {
  border: 2px dashed #d4d4e8;
  border-radius: 8px;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #fafbff 0%, #f5f7ff 100%);
}

.upload-component ::v-deep .el-upload--picture-card:hover {
  border-color: #7B68EE;
  background: linear-gradient(135deg, #f5f7ff 0%, #e8eaf6 100%);
}

.upload-icon {
  font-size: 28px;
  color: #7B68EE;
}

/* Preview dialog styling */
.preview-dialog ::v-deep .el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.preview-dialog ::v-deep .el-dialog__header {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  color: #ffffff;
  padding: 16px 20px;
}

.preview-dialog ::v-deep .el-dialog__title {
  color: #ffffff;
  font-weight: 600;
}

.preview-dialog ::v-deep .el-dialog__body {
  padding: 20px;
  background: #ffffff;
}

/* Form actions */
.form-actions {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 2px solid #e8e8f0;
}

.form-actions ::v-deep .el-form-item__content {
  display: flex;
  gap: 12px;
  justify-content: center;
}

/* Button styling */
.submit-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  border: none;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #9370DB 0%, #BA55D3 100%);
  box-shadow: 0 4px 12px rgba(123, 104, 238, 0.4);
  transform: translateY(-2px);
}

.submit-btn:active {
  transform: translateY(0);
}

.draft-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  border: 1px solid #d4d4e8;
  background: #ffffff;
  color: #5a4fcf;
  transition: all 0.3s ease;
}

.draft-btn:hover {
  border-color: #9370DB;
  background: linear-gradient(135deg, #f5f7ff 0%, #e8eaf6 100%);
  color: #7B68EE;
  box-shadow: 0 4px 12px rgba(147, 112, 219, 0.2);
  transform: translateY(-2px);
}

.draft-btn:active {
  transform: translateY(0);
}

.reset-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  border: 1px solid #d4d4e8;
  background: #ffffff;
  color: #909399;
  transition: all 0.3s ease;
}

.reset-btn:hover {
  border-color: #f56c6c;
  background: linear-gradient(135deg, #fef0f0 0%, #fde2e2 100%);
  color: #f56c6c;
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.2);
  transform: translateY(-2px);
}

.reset-btn:active {
  transform: translateY(0);
}

/* Card styling */
.approval-form-container ::v-deep .el-card__header {
  padding: 20px 24px;
  border-bottom: 1px solid #e8e8f0;
  background: #ffffff;
}

.approval-form-container ::v-deep .el-card__body {
  padding: 0;
}

/* Form validation error styling */
.approval-form ::v-deep .el-form-item.is-error .el-input__inner,
.approval-form ::v-deep .el-form-item.is-error .el-textarea__inner {
  border-color: #f56c6c;
  animation: shake 0.3s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

.approval-form ::v-deep .el-form-item__error {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
  padding-left: 4px;
}

/* Responsive design */
@media (max-width: 1200px) {
  .approval-form-container {
    padding: 16px;
  }

  .approval-form {
    padding: 16px;
  }

  .card-title {
    font-size: 16px;
  }

  .card-title i {
    font-size: 18px;
  }
}

@media (max-width: 768px) {
  .approval-form-container {
    padding: 12px;
  }

  .approval-form {
    padding: 12px;
  }

  .approval-form ::v-deep .el-form-item__label {
    font-size: 13px;
  }

  .card-title {
    font-size: 15px;
  }

  .card-title i {
    font-size: 16px;
  }

  .form-actions ::v-deep .el-form-item__content {
    flex-direction: column;
  }

  .submit-btn,
  .draft-btn,
  .reset-btn {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .approval-form-container {
    padding: 8px;
  }

  .approval-form {
    padding: 8px;
  }

  .card-title {
    font-size: 14px;
  }

  .card-title i {
    font-size: 15px;
  }

  .text-input ::v-deep .el-input__inner,
  .select-input ::v-deep .el-input__inner,
  .date-picker-input ::v-deep .el-input__inner {
    height: 32px;
    line-height: 32px;
    font-size: 13px;
  }

  .textarea-input ::v-deep .el-textarea__inner {
    font-size: 13px;
  }

  .submit-btn,
  .draft-btn,
  .reset-btn {
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

.approval-card {
  animation: fadeIn 0.4s ease-out;
}
</style>
