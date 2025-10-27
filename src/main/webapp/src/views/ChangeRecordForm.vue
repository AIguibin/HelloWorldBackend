<template>
  <el-form :model="form" label-width="120px">
    <el-row :gutter="12">
      <el-col :span="12">
        <el-form-item label="版本号">
          <el-input v-model="form.version"  />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="发版日期">
          <el-date-picker v-model="form.releaseDate" type="date" placeholder="选择日期" style="width:100%"  />
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="12">
      <el-col :span="12">
        <el-form-item label="缺陷编号">
          <el-input v-model="form.defectNumber"  />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="服务名称">
          <el-input v-model="form.serviceName"  />
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="12">
      <el-col :span="12">
        <el-form-item label="组别">
          <el-input v-model="form.groupName"  />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="开发负责人">
          <el-input v-model="form.developer"  />
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="12">
      <el-col :span="12">
        <el-form-item label="分支名称">
          <el-input v-model="form.branchName"  />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="当前状态">
          <el-select v-model="form.currentStatus" placeholder="选择状态" style="width:100%" >
            <el-option label="待审批" value="待审批" />
            <el-option label="待评审" value="待评审" />
            <el-option label="待合版" value="待合版" />
            <el-option label="已合版" value="已合版" />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="12">
      <el-col :span="12">
        <el-form-item label="涉及外围系统">
          <el-switch v-model="form.involveExternalSystem" :active-value="1" :inactive-value="0"  />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="是否跨服务">
          <el-switch v-model="form.crossService" :active-value="1" :inactive-value="0"  />
        </el-form-item>
      </el-col>
    </el-row>

    <el-form-item label="问题描述">
      <el-input v-model="form.problemDescription" type="textarea" rows="3"  />
    </el-form-item>
    <el-form-item label="问题影响分析">
      <el-input v-model="form.impactAnalysis" type="textarea" rows="3"  />
    </el-form-item>
    <el-form-item label="解决方案">
      <el-input v-model="form.solution" type="textarea" rows="3"  />
    </el-form-item>
    <el-form-item label="代码清单">
      <el-input v-model="form.codeList" type="textarea" rows="3"  />
    </el-form-item>
    <el-form-item label="备注说明">
      <el-input v-model="form.remark" type="textarea" rows="3"  />
    </el-form-item>
    <el-form-item label="变更描述">
      <el-input v-model="form.changeDesc" type="textarea" rows="3"  />
    </el-form-item>
  </el-form>
</template>

<script>
export default {
  name: 'ChangeRecordForm',
  props: {
    form: { type: Object, required: true },
    canEdit: { type: Boolean, default: false }
  }
};
</script>
