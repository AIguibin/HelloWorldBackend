<template>
  <div class="approval-form-container">
    <el-card shadow="hover" title="审批申请表单">
      <el-form :model="formData" :rules="formRules" ref="approvalForm" label-width="120px">
        <!-- 业务类型选择 -->
        <el-form-item label="业务类型" prop="businessType">
          <el-select v-model="formData.businessType" placeholder="请选择业务类型" @change="handleBusinessTypeChange">
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
          <el-input v-model="formData.title" placeholder="请输入申请标题"></el-input>
        </el-form-item>

        <el-form-item label="申请原因" prop="reason">
          <el-input v-model="formData.reason" type="textarea" :rows="4" placeholder="请输入申请原因"></el-input>
        </el-form-item>

        <!-- 动态表单字段 -->
        <template v-if="currentBusinessType">
          <!-- 根据业务类型动态渲染不同的表单字段 -->
          <el-form-item v-if="currentBusinessType.typeCode === 'CHANGE_RECORD'" label="变更记录编码">
            <el-input v-model="formData.changeRecordCode" placeholder="请输入变更记录编码"></el-input>
          </el-form-item>
          
          <el-form-item v-if="currentBusinessType.typeCode === 'RELEASE'" label="发版版本号">
            <el-input v-model="formData.version" placeholder="请输入发版版本号"></el-input>
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
          >
            <i class="el-icon-plus"></i>
          </el-upload>
          <el-dialog :visible.sync="dialogVisible">
            <img width="100%" :src="dialogImageUrl" alt="">
          </el-dialog>
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="submitForm">提交审批</el-button>
          <el-button @click="saveDraft">保存草稿</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'ApprovalForm',
  data() {
    return {
      formData: {
        businessType: '',
        title: '',
        reason: '',
        changeRecordCode: '',
        version: ''
      },
      formRules: {
        businessType: [{ required: true, message: '请选择业务类型', trigger: 'change' }],
        title: [{ required: true, message: '请输入申请标题', trigger: 'blur' }],
        reason: [{ required: true, message: '请输入申请原因', trigger: 'blur' }]
      },
      businessTypes: [],
      currentBusinessType: null,
      fileList: [],
      dialogImageUrl: '',
      dialogVisible: false
    }
  },
  mounted() {
    this.loadBusinessTypes()
  },
  methods: {
    // 加载业务类型
    loadBusinessTypes() {
      // 调用API获取业务类型列表
      this.$http.get('/api/business-types')
        .then(response => {
          this.businessTypes = response.data.data
        })
        .catch(error => {
          this.$message.error('加载业务类型失败')
          console.error('加载业务类型失败:', error)
        })
    },

    // 业务类型变化处理
    handleBusinessTypeChange(value) {
      this.currentBusinessType = this.businessTypes.find(type => type.typeCode === value)
      // 重置动态字段
      this.formData.changeRecordCode = ''
      this.formData.version = ''
    },

    // 提交表单
    submitForm() {
      this.$refs.approvalForm.validate((valid) => {
        if (valid) {
          // 调用API提交审批
          this.$http.post('/api/approval-requests', this.formData)
            .then(response => {
              this.$message.success('提交审批成功')
              this.$router.push('/work-list/todo')
            })
            .catch(error => {
              this.$message.error('提交审批失败')
              console.error('提交审批失败:', error)
            })
        } else {
          return false
        }
      })
    },

    // 保存草稿
    saveDraft() {
      // 调用API保存草稿
      this.$http.post('/api/approval-requests/draft', this.formData)
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
      this.$refs.approvalForm.resetFields()
      this.fileList = []
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
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 120px);
}

.approval-form-container .el-card {
  margin-bottom: 20px;
}
</style>
