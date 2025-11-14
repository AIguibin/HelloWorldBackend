<template>
  <div style="padding:16px;">
    <el-card>
      <div slot="header" class="clearfix">
        <span>变更历史（记录ID：{{ recordId }}）</span>
        <el-button style="float:right;" @click="$router.push('/change-records')">返回列表</el-button>
      </div>
      <div class="history-wrapper">
        <el-table :data="list" border stripe size="mini">
          <el-table-column prop="operationTime" label="操作时间" width="150" show-overflow-tooltip="true"/>
          <el-table-column prop="operationUser" label="操作人" width="120" show-overflow-tooltip="true"/>
          <el-table-column prop="operationType" label="操作类型" width="120" />
          <el-table-column prop="version" label="版本号" width="150" show-overflow-tooltip="true"/>
          <el-table-column prop="defectNumber" label="缺陷编号" width="140" />
          <el-table-column prop="groupName" label="组别" width="140" />
          <el-table-column prop="developer" label="开发负责人" width="100" />
          <el-table-column prop="developType" label="开发类别" width="100" />
          <el-table-column prop="serviceName" label="服务名称" width="140" />
          <el-table-column prop="releaseDate" label="发版日期" width="130" />
          <el-table-column label="当前状态" width="90">
            <template slot-scope="scope">
              <el-tag :type="statusTagType(scope.row.currentStatus)">{{ scope.row.currentStatus || '未知' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="changeDesc" label="变更描述" show-overflow-tooltip="true"/>
        <el-table-column prop="operationDescription" label="操作描述" width="180" show-overflow-tooltip="true"/>
        <el-table-column prop="branchName" label="分支名称" width="140" />
        <el-table-column prop="problemDescription" label="问题描述" width="180" show-overflow-tooltip="true"/>
        <el-table-column prop="impactAnalysis" label="问题影响分析" width="180" show-overflow-tooltip="true"/>
        <el-table-column prop="solution" label="解决方案" width="180" show-overflow-tooltip="true"/>
        <el-table-column prop="codeList" label="代码清单" width="180" show-overflow-tooltip="true"/>
        <el-table-column prop="createTime" label="原始创建时间" width="180">
          <template slot-scope="scope">{{ formatDateTime(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column prop="updateTime" label="原始更新时间" width="180" show-overflow-tooltip="true">
          <template slot-scope="scope">{{ formatDateTime(scope.row.updateTime) }}</template>
        </el-table-column>
        <el-table-column prop="createUser" label="原始创建人" width="140" show-overflow-tooltip="true"/>
        <el-table-column prop="updateUser" label="原始更新人" width="140" show-overflow-tooltip="true"/>
        <el-table-column prop="involveExternalSystem" label="涉及外部系统" width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.involveExternalSystem ? 'warning' : 'info'">{{ scope.row.involveExternalSystem ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="crossService" label="是否跨服务" width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.crossService ? 'warning' : 'info'">{{ scope.row.crossService ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注说明" show-overflow-tooltip="true" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getChangeRecordHistory } from '../api';

export default {
  name: 'ChangeRecordHistory',
  data() {
    return { list: [], page: 1, pageSize: 10, total: 0 };
  },
  computed: {
    recordId() { return this.$route.params.id; }
  },
  created() { this.fetchList(); },
  methods: {
    async fetchList(toPage) {
      if (toPage) this.page = toPage;
      const data = await getChangeRecordHistory(this.recordId, { page: this.page, size: this.pageSize });
      this.list = data.records || [];
      this.total = data.total || 0;
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