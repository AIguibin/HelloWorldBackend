<template>
  <el-form ref="form" :model="form" label-width="120px" :rules="rules">
    <el-row :gutter="12">
      <el-col :span="12">
        <el-form-item label="版本号">
          <el-input v-model="form.version"  :disabled="true"/>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="发版日期" prop="releaseDate">
          <el-date-picker v-model="form.releaseDate" type="date" placeholder="选择日期" style="width:100%" value-format="yyyy-MM-dd" />
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="12">
      <el-col :span="12">
        <el-form-item label="分支名称" prop="branchName">
          <el-input v-model="form.branchName"  />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="当前状态" prop="currentStatus">
          <el-select v-model="form.currentStatus" placeholder="选择状态" style="width:100%" >
            <el-option v-for="item in currentStatusOptions" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="12">
      <el-col :span="12">
        <el-form-item label="缺陷编号" prop="defectNumber">
          <el-input v-model="form.defectNumber"  />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="服务名称" prop="serviceName">
          <el-input v-model="form.serviceName"  />
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="12">
      <el-col :span="12">
        <el-form-item label="组别" prop="groupName">
          <el-input v-model="form.groupName"  />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="开发负责人" prop="developer">
          <el-input v-model="form.developer"  />
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="12">
      <el-col :span="12">
        <el-form-item label="开发类别" prop="developType">
          <el-select v-model="form.developType" placeholder="选择开发类别" style="width:100%" >
            <el-option v-for="item in developTypeOptions" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-form-item label="问题描述" prop="problemDescription">
      <el-input v-model="form.problemDescription" type="textarea" rows="3"  />
    </el-form-item>
    <el-form-item label="变更描述" prop="changeDesc">
      <el-input v-model="form.changeDesc" type="textarea" rows="3"  />
    </el-form-item>
    <el-form-item label="问题影响分析" prop="impactAnalysis">
      <el-input v-model="form.impactAnalysis" type="textarea" rows="3"  />
    </el-form-item>
    <el-form-item label="解决方案" prop="solution">
      <el-input v-model="form.solution" type="textarea" rows="3"  />
    </el-form-item>
    <el-form-item label="代码清单" prop="codeList">
      <el-input v-model="form.codeList" type="textarea" rows="3"  />
    </el-form-item>
    <el-row :gutter="12">
      <el-col :span="12">
        <el-form-item label="是否涉及外围系统" prop="involveExternalSystem">
          <el-switch v-model="form.involveExternalSystem" :active-value="1" :inactive-value="0"  />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="是否跨服务">
          <el-switch v-model="form.crossService" :active-value="1" :inactive-value="0"  />
        </el-form-item>
      </el-col>
    </el-row>
    <el-form-item label="备注说明">
      <el-input v-model="form.remark" type="textarea" rows="3"  />
    </el-form-item>

  </el-form>
</template>

<script>
import { getDictItemsByType } from '../api';

export default {
  name: 'ChangeRecordForm',
  props: {
    form: { type: Object, required: true },
    canEdit: { type: Boolean, default: false }
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
      developTypeOptions: []
    }
  },
  mounted() {
    // 加载字典数据
    this.loadDictData();
  },
  methods: {
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
  }
};
</script>

