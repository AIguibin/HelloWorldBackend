<template>
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
          <el-date-picker v-model="form.releaseDate" type="date" placeholder="选择日期" style="width: 100%"
            value-format="yyyy-MM-dd"  />
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label="源分支">
        <el-form-item prop="sourceBranch">
          <el-input v-model="form.sourceBranch"  />
        </el-form-item>
      </el-descriptions-item>
      
      <!-- 新增行：目标分支 -->
      <el-descriptions-item label="目标分支">
        <el-form-item prop="targetBranch">
          <el-input v-model="form.targetBranch"  />
        </el-form-item>
      </el-descriptions-item>

      <!-- 第二行 -->
      <el-descriptions-item label="当前状态">
        <el-form-item prop="currentStatus">
          <el-select v-model="form.currentStatus" placeholder="选择状态" style="width: 100%"
            >
            <el-option v-for="item in currentStatusOptions" :key="item.dictValue" :label="item.dictLabel"
              :value="item.dictValue" />
          </el-select>
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label="缺陷编号">
        <el-form-item prop="defectNumber">
          <el-input v-model="form.defectNumber"  />
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label="服务名称">
        <el-form-item prop="serviceName">
          <el-input v-model="form.serviceName"  />
        </el-form-item>
      </el-descriptions-item>

      <!-- 第三行 -->
      <el-descriptions-item label="组别">
        <el-form-item prop="groupName">
          <el-input v-model="form.groupName"  />
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label="开发负责人编号">
        <el-form-item prop="developerNum">
          <el-input v-model="form.developerNum"  />
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label="开发负责人姓名">
        <el-form-item prop="developerName">
          <el-input v-model="form.developerName"  />
        </el-form-item>
      </el-descriptions-item>
      
      <!-- 新增行：开发类别 -->
      <el-descriptions-item label="开发类别">
        <el-form-item prop="developType">
          <el-select v-model="form.developType" placeholder="选择开发类别" style="width: 100%"
            >
            <el-option v-for="item in developTypeOptions" :key="item.dictValue" :label="item.dictLabel"
              :value="item.dictValue" />
          </el-select>
        </el-form-item>
      </el-descriptions-item>

      <!-- 第四行 -->
      <el-descriptions-item label="是否涉及外围系统">
        <el-form-item prop="involveExternalSystem">
          <el-switch v-model="form.involveExternalSystem" :active-value="1" :inactive-value="0"
             />
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label="是否跨服务">
        <el-form-item>
          <el-switch v-model="form.crossService" :active-value="1" :inactive-value="0"
             />
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label="是否包含脚本">
        <el-form-item>
          <el-switch v-model="form.includeShell" :active-value="1" :inactive-value="0"
             />
        </el-form-item>
      </el-descriptions-item>
      
      <!-- 新增行：脚本路径 -->
      <el-descriptions-item label="脚本清单">
        <el-form-item prop="shellPath">
          <el-input v-model="form.shellPath" type="textarea" rows="2"  />
        </el-form-item>
      </el-descriptions-item>
    </el-descriptions>

    <!-- 文本域部分，1列布局 -->
    <el-descriptions :label-style="{
      width: '150px',
      minWidth: '100px',
    }" :column="1" border>
      <el-descriptions-item label="问题描述">
        <el-form-item prop="problemDescription">
          <el-input v-model="form.problemDescription" type="textarea" rows="3"
             />
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label="变更描述">
        <el-form-item prop="changeDesc">
          <el-input v-model="form.changeDesc" type="textarea" rows="3"  />
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label="问题影响分析">
        <el-form-item prop="impactAnalysis">
          <el-input v-model="form.impactAnalysis" type="textarea" rows="3"
             />
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label="解决方案">
        <el-form-item prop="solutionDescription">
          <el-input v-model="form.solutionDescription" type="textarea" rows="3"  />
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label="代码清单">
        <el-form-item prop="codeList">
          <el-input v-model="form.codeList" type="textarea" rows="3"  />
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label="配置说明">
        <el-form-item prop="configList">
          <el-input v-model="form.configList" type="textarea" rows="3"  />
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label="备注说明">
        <el-form-item>
          <el-input v-model="form.remark" type="textarea" rows="3"  />
        </el-form-item>
      </el-descriptions-item>
    </el-descriptions>
  </el-form>
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
