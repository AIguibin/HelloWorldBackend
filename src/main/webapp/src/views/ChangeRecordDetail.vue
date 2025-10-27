<template>
  <div class="detail-wrapper">
    <el-card>
      <div slot="header" class="clearfix">
        <span>变更记录详情</span>
        <el-button style="float:right;" @click="$router.push('/change-records')">返回列表</el-button>
      </div>
      <el-descriptions :title="detail.defectNumber" :column="2" border>
        <el-descriptions-item label="版本号">{{ detail.version }}</el-descriptions-item>
        <el-descriptions-item label="服务名称">{{ detail.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="组别">{{ detail.groupName }}</el-descriptions-item>
        <el-descriptions-item label="开发负责人">{{ detail.developer }}</el-descriptions-item>
        <el-descriptions-item label="发版日期">{{ detail.releaseDate }}</el-descriptions-item>
        <el-descriptions-item label="当前状态">
          <el-tag :type="statusTagType(detail.currentStatus)">{{ detail.currentStatus || '未知' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="问题描述">{{ detail.problemDescription }}</el-descriptions-item>
        <el-descriptions-item label="影响分析">{{ detail.impactAnalysis }}</el-descriptions-item>
        <el-descriptions-item label="解决方案">{{ detail.solution }}</el-descriptions-item>
        <el-descriptions-item label="涉外系统">
          <el-tag :type="detail.involveExternalSystem ? 'warning' : 'info'">{{ detail.involveExternalSystem ? '是' : '否' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="跨服务">
          <el-tag :type="detail.crossService ? 'warning' : 'info'">{{ detail.crossService ? '是' : '否' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="代码清单">{{ detail.codeList }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ detail.remark }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(detail.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDateTime(detail.updateTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script>
import { getChangeRecordDetail } from '../api';

export default {
  name: 'ChangeRecordDetail',
  data() {
    return { detail: {} };
  },
  created() {
    const id = this.$route.params.id;
    this.fetchDetail(id);
  },
  methods: {
    async fetchDetail(id) {
      try {
        const data = await getChangeRecordDetail(id);
        this.detail = data || {};
      } catch (e) {}
    },
    statusTagType(s) {
      switch (s) {
        case '待审批': return 'warning';
        case '待评审': return 'info';
        case '待合版': return 'primary';
        case '已合版': return 'success';
        default: return '';
      }
    },
    formatDateTime(v) {
      if (!v) return '';
      try {
        const s = String(v);
        return s.replace('T', ' ');
      } catch (e) { return String(v); }
    }
  }
};
</script>