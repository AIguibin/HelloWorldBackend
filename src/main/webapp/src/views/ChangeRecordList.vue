<template>
  <div class="change-record-page">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <div class="search-container">
        <!-- 搜索输入区域 -->
        <div class="search-inputs-wrapper">
          <!-- 默认显示的前3个搜索项 -->
          <el-input v-model="search.groupName" placeholder="组名" class="search-input" />
          <el-input v-model="search.developerName" placeholder="开发负责人" class="search-input" />
          <el-input v-model="search.serviceName" placeholder="服务名称" class="search-input" />
          <el-input v-model="search.defectNumber" placeholder="缺陷编号" class="search-input" />
          <el-select v-model="search.developType" placeholder="开发类别" class="search-select">
            <el-option v-for="item in developTypeOptions" :key="item.dictValue" :label="item.dictLabel"
              :value="item.dictValue" />
          </el-select>
          <el-select v-model="search.currentStatus" placeholder="当前状态" class="search-select">
            <el-option v-for="item in currentStatusOptions" :key="item.dictValue" :label="item.dictLabel"
              :value="item.dictValue" />
          </el-select>
          <!-- 点击展开后显示的搜索项 -->
          <template v-if="isSearchExpanded">

            <el-date-picker v-model="searchRange" type="datetimerange" range-separator="至" start-placeholder="开始时间"
              end-placeholder="结束时间" class="search-date" />
            <el-select v-model="exportFormat" placeholder="导出格式" class="search-select">
              <el-option label="Excel (XLSX)" value="xlsx" />
              <el-option label="CSV" value="csv" />
            </el-select>
          </template>

          <!-- 展开/收起按钮 -->
          <el-button @click="toggleSearchExpanded" type="text" class="expand-btn">
            {{ isSearchExpanded ? '收起' : '展开' }}
            <i :class="['el-icon-arrow-down', { 'rotate-180': isSearchExpanded }]"></i>
          </el-button>
        </div>

        <!-- 按钮组区域 -->
        <div class="search-buttons">
          <el-button @click="resetSearch" class="action-btn reset-btn">重置</el-button>
          <el-button type="primary" @click="fetchList(1)" class="action-btn">查询</el-button>
          <el-button @click="openCreate" class="action-btn create-btn">新增</el-button>
          <el-button @click="onExport" class="action-btn export-btn">下载</el-button>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table :data="list" stripe class="data-table" :fit="true" border>
        <el-table-column width="280" label="操作" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" @click="openEdit(scope.row)" class="table-btn">编辑</el-button>
            <el-button size="mini" @click="openHistory(scope.row)" class="table-btn history-btn">历史</el-button>
            <el-button size="mini" @click="goDetail(scope.row)" class="table-btn detail-btn">详情</el-button>
            <el-button v-if="canSubmitApproval(scope.row)" size="mini" type="primary" @click="submitApproval(scope.row)" class="table-btn approve-btn">提交审批</el-button>
          </template>
        </el-table-column>
        <!-- 序号（原ID） -->
        <!-- <el-table-column prop="id" label="序号" width="80" /> -->
        <el-table-column prop="version" label="版本号" width="160" :show-overflow-tooltip="true" />
        <!-- 当前状态与发版日期 -->
        <el-table-column prop="currentStatus" label="当前状态" width="120">
          <template slot-scope="scope">
            <el-tag :type="statusTagType(scope.row.currentStatus)">{{ getDictLabel('CURRENT_STATUS',
              scope.row.currentStatus) || '待审批' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="releaseDate" label="发版日期" width="140" :show-overflow-tooltip="true" />
        <!-- 基本信息 -->
        <el-table-column prop="defectNumber" label="缺陷编号" width="140" :show-overflow-tooltip="true" />
        <el-table-column prop="groupName" label="组名" width="140" :show-overflow-tooltip="true" />
        <el-table-column prop="developerName" label="开发负责人" width="140" :show-overflow-tooltip="true" />
        <el-table-column prop="developType" label="开发类别" width="140" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            {{ getDictLabel('DEVELOP_TYPE', scope.row.developType) }}
          </template>
        </el-table-column>
        <el-table-column prop="sourceBranch" label="源分支" width="140" :show-overflow-tooltip="true" />
        <el-table-column prop="targetBranch" label="目标分支" width="140" :show-overflow-tooltip="true" />
        <el-table-column prop="serviceName" label="服务名称" width="160" :show-overflow-tooltip="true" />
        <!-- 问题与方案 -->
        <el-table-column prop="problemDescription" width="220" label="问题描述" :show-overflow-tooltip="true" />
        <el-table-column prop="changeDesc" width="220" label="变更描述" :show-overflow-tooltip="true" />
        <el-table-column prop="impactAnalysis" width="220" label="影响分析" :show-overflow-tooltip="true" />
        <el-table-column prop="solutionDescription" label="解决方案" :show-overflow-tooltip="true" />
        <el-table-column prop="codeList" width="220" label="代码清单" :show-overflow-tooltip="true" />
        <el-table-column prop="shellPath" width="220" label="脚本清单" :show-overflow-tooltip="true" />
        <el-table-column prop="configList" width="220" label="配置说明" :show-overflow-tooltip="true" />
        <!-- 影响范围 -->
        <el-table-column prop="involveExternalSystem" label="涉及外部系统" width="120" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <el-tag :type="scope.row.involveExternalSystem ? 'warning' : 'info'">{{ scope.row.involveExternalSystem ?
              '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="crossService" label="是否跨服务" width="120" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <el-tag :type="scope.row.crossService ? 'warning' : 'info'">{{ scope.row.crossService ? '是' : '否'
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="includeShell" label="是否包含脚本" width="120" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <el-tag :type="scope.row.includeShell ? 'warning' : 'info'">{{ scope.row.includeShell ? '是' : '否'
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注说明" :show-overflow-tooltip="true" />
      </el-table>
    </div>

    <div class="pagination-container">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="page"
        :page-sizes="[5, 10, 20, 30]" :page-size="pageSize" layout="total,sizes,prev,pager,next,jumper" :total="total">
      </el-pagination>
    </div>

    <!-- 表单弹窗 -->
    <el-dialog :visible.sync="showForm" title="变更记录" width="90%">
      <change-record-form ref="changeRecordForm" :form="form" />
      <div style="text-align:right; margin-top:12px;">
        <el-button @click="showForm = false">取 消</el-button>
        <el-button type="primary" @click="onSubmit">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 历史记录弹窗 -->
    <el-dialog :visible.sync="showHistory" title="历史记录" width="90%">
      <el-table :data="historyList" border stripe>
        <el-table-column prop="operationType" label="操作类型" width="120" />
        <el-table-column prop="operationUser" label="操作人" width="120" />
        <el-table-column prop="operationTime" label="操作时间" width="180">
          <template slot-scope="scope">{{ formatDateTime(scope.row.operationTime) }}</template>
        </el-table-column>
        <el-table-column prop="operationDescription" label="操作描述" />
        <el-table-column prop="currentStatus" label="当前状态" width="120">
          <template slot-scope="scope">
            <el-tag :type="statusTagType(scope.row.currentStatus)">{{ scope.row.currentStatus || '待审批' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="releaseDate" label="发版日期" width="140" />
        <el-table-column prop="defectNumber" label="缺陷编号" width="140" />
        <el-table-column prop="groupName" label="组名" width="140" />
        <el-table-column prop="developerName" label="开发负责人" width="140" />
        <el-table-column prop="sourceBranch" label="源分支" width="140" />
        <el-table-column prop="targetBranch" label="目标分支" width="140" />
        <el-table-column prop="serviceName" label="服务名称" width="160" />
        <el-table-column prop="problemDescription" label="问题描述" />
        <el-table-column prop="impactAnalysis" label="问题影响分析" />
        <el-table-column prop="solutionDescription" label="解决方案" />
        <el-table-column prop="involveExternalSystem" label="涉及外部系统" width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.involveExternalSystem ? 'warning' : 'info'">{{ scope.row.involveExternalSystem ?
              '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="crossService" label="是否跨服务" width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.crossService ? 'warning' : 'info'">{{ scope.row.crossService ? '是' : '否'
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="codeList" label="代码清单" />
        <el-table-column prop="remark" label="备注说明" />
        <el-table-column prop="version" label="版本号" width="160" />
        <el-table-column prop="changeDesc" label="变更描述" />
        <el-table-column prop="createTime" label="原始创建时间" width="180">
          <template slot-scope="scope">{{ formatDateTime(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column prop="updateTime" label="原始更新时间" width="180">
          <template slot-scope="scope">{{ formatDateTime(scope.row.updateTime) }}</template>
        </el-table-column>
        <el-table-column prop="createUser" label="原始创建人" width="140" />
        <el-table-column prop="updateUser" label="原始更新人" width="140" />
        <el-table-column prop="isDeleted" label="原始删除标志" width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isDeleted ? 'danger' : 'success'">{{ scope.row.isDeleted ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { listChangeRecords, createChangeRecord, updateChangeRecord, deleteChangeRecord, exportChangeRecords, getDictItemsByType, submitApproval } from '../api';
import ChangeRecordForm from './ChangeRecordForm.vue';

export default {
  name: 'ChangeRecordList',
  components: { ChangeRecordForm },
  data() {
    return {
      list: [],
      page: 1,
      pageSize: 10,
      total: 0,
      // 新增：控制搜索项展开/收起的状态
      isSearchExpanded: false,
      // 移除 isReleased 搜索项，保留其他
      search: { groupName: '', developerName: '', serviceName: '', defectNumber: '', currentStatus: '', developType: '' },
      searchRange: [],
      // 新增：导出相关字段，避免未定义导致渲染异常
      downloadStatus: '已合版',
      exportFormat: 'xlsx',
      showForm: false,
      editTarget: null,
      form: {
        currentStatus: '待审批',
        releaseDate: '',
        defectNumber: '',
        groupName: '',
        developerNum: '',
        developerName: '',
        sourceBranch: '',
        targetBranch: '',
        serviceName: '',
        problemDescription: '',
        impactAnalysis: '',
        solutionDescription: '',
        includeShell: 0,
        crossService: 0,
        codeList: '',
        shellPath: '',
        configList: '',
        remark: '',
        version: '',
        changeDesc: ''
      },
      showHistory: false,
      historyList: [],
      historyTarget: null,
      // 字典选项
      currentStatusOptions: [],
      developTypeOptions: []
    };
  },
  mounted() {
    this.fetchList();
    this.loadDictData();
  },
  computed: {
    canEdit() {
      try {
        const raw = localStorage.getItem('user');
        const u = JSON.parse(raw || '{}');
        const rawId = (u && (u.userNum || u.userName)) || '';
        const id = String(rawId).toUpperCase();
        const white = ['ADMIN', 'BG001', 'BG002'];
        return white.includes(id);
      } catch (e) {
        return false;
      }
    },
  },
  methods: {
    // 根据字典类型和编码获取字典标签
    getDictLabel(dictType, dictValue) {
      if (!dictType || !dictValue) return '';

      let dictOptions = [];
      if (dictType === 'CURRENT_STATUS') {
        dictOptions = this.currentStatusOptions;
      } else if (dictType === 'DEVELOP_TYPE') {
        dictOptions = this.developTypeOptions;
      }

      const dictItem = dictOptions.find(item => item.dictValue === dictValue);
      return dictItem ? dictItem.dictLabel : dictValue;
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
    },
    async fetchList(page = 1) {
      this.page = page;
      const params = {
        page: this.page,
        size: this.pageSize,
        groupName: this.search.groupName || undefined,
        developer: this.search.developer || undefined,
        serviceName: this.search.serviceName || undefined,
        defectNumber: this.search.defectNumber || undefined,
        developType: this.search.developType || undefined,
        currentStatus: this.search.currentStatus || undefined
      };
      if (this.searchRange && this.searchRange.length === 2) {
        params.startTime = this.formatDate(this.searchRange[0]);
        params.endTime = this.formatDate(this.searchRange[1]);
      }
      try {
        const data = await listChangeRecords(params);
        this.list = data.records || [];
        this.total = data.total || 0;
      } catch (e) {
        this.$message && this.$message.error('列表数据加载失败');
      }
    },
    formatDate(d) {
      if (!d) return undefined;
      const t = new Date(d);
      const yyyy = t.getFullYear();
      const mm = String(t.getMonth() + 1).padStart(2, '0');
      const dd = String(t.getDate()).padStart(2, '0');
      return `${yyyy}-${mm}-${dd} 00:00:00`;
    },
    formatDateTimeParam(d) {
      if (!d) return undefined;
      const t = new Date(d);
      const yyyy = t.getFullYear();
      const mm = String(t.getMonth() + 1).padStart(2, '0');
      const dd = String(t.getDate()).padStart(2, '0');
      const HH = String(t.getHours()).padStart(2, '0');
      const MM = String(t.getMinutes()).padStart(2, '0');
      const SS = String(t.getSeconds()).padStart(2, '0');
      return `${yyyy}-${mm}-${dd} ${HH}:${MM}:${SS}`;
    },
    openCreate() {
      this.editTarget = null;
      this.form = {
        currentStatus: '待审批',
        releaseDate: '',
        defectNumber: '',
        groupName: '',
        developerNum: '',
        developerName: '',
        sourceBranch: '',
        targetBranch: '',
        serviceName: '',
        problemDescription: '',
        impactAnalysis: '',
        solutionDescription: '',
        includeShell: 0,
        crossService: 0,
        codeList: '',
        shellPath: '',
        configList: '',
        remark: '',
        version: '',
        changeDesc: ''
      };
      this.showForm = true;
    },
    openEdit(row) {
      this.editTarget = row;
      this.form = { ...row };
      this.showForm = true;
    },
    async onSubmit() {
      this.$refs['changeRecordForm'].$refs.form.validate(async (valid) => {
        if (valid) {
          if (this.editTarget) {
            await updateChangeRecord(this.editTarget.id, this.form);
          } else {
            await createChangeRecord(this.form);
          }
          this.showForm = false;
          this.fetchList(this.page);
        } else {
          this.$message && this.$message.error('请检查必填项');
        }
      });
    },
    async onDelete(row) {
      await deleteChangeRecord(row.id);
      this.fetchList(this.page);
    },
    statusTagType(s) {
      switch (s) {
        case '01': case '待审批': return 'warning';
        case '02': case '待评审': return 'info';
        case '03': case '待合版': return 'primary';
        case '04': case '已合版': return 'success';
        default: return '';
      }
    },
    // 修复：模板引用的导航与登录方法
    openHistory(row) {
      if (!row || !row.id) return;
      this.$router.push(`/change-records/${row.id}/history`);
    },
    goDetail(row) {
      if (!row || !row.id) return;
      this.$router.push(`/change-records/${row.id}`);
    },
    async onDownload() {
      try {
        const params = { ...this.search, limit: 1000 };
        if (this.searchRange && this.searchRange.length === 2) {
          params.startTime = this.formatDateTimeParam(this.searchRange[0]);
          params.endTime = this.formatDateTimeParam(this.searchRange[1]);
        }
        const response = await exportChangeRecords(params);
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = window.URL.createObjectURL(response.data);
        a.setAttribute('download', 'code_script_change_record_.xlsx');
        a.download = 'code_script_change_record_.xlsx';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);

        window.URL.revokeObjectURL(a.href);
      } catch (e) {
        console.error(e);
        this.$message && this.$message.error('Excel导出失败');
      }
    },
    formatDateTime(v) {
      if (!v) return '';
      try {
        const s = String(v);
        return s.replace('T', ' ');
      } catch (e) { return String(v); }
    },

    onExport() {
      // 记录导出操作的参数
      console.log('导出操作开始:', {
        format: this.exportFormat,
        selectedStatus: this.search.currentStatus,
        dateRange: this.searchRange
      });
      if (this.exportFormat === 'csv') {
        this.onDownloadCsv();
      } else {
        this.onDownload();
      }
    },

    onDownloadCsv() {
      this.fetchListRaw(1000).then(rows => {
        const headers = ['序号', '当前状态', '发版日期', '缺陷编号', '组别', '开发负责人', '源分支', '目标分支', '服务名称', '问题描述', '影响分析', '解决方案', '涉及外部系统', '跨服务', '是否包含脚本', '代码清单', '脚本清单', '配置说明', '备注', '版本号', '变更描述', '创建时间', '更新时间', '创建人', '更新人', '删除标志'];
        const escape = v => {
          if (v == null) return '';
          const s = String(v).replace(/\r?\n/g, ' ');
          return /[,\"]/.test(s) ? ('"' + s.replace(/\"/g, '""') + '"') : s;
        };
        const lines = [headers.join(',')];
        (rows || []).forEach(cr => {
          const line = [
            cr.id,
            cr.currentStatus,
            cr.releaseDate,
            cr.defectNumber,
            cr.groupName,
            cr.developerName,
            cr.sourceBranch,
            cr.targetBranch,
            cr.serviceName,
            cr.problemDescription,
            cr.impactAnalysis,
            cr.solutionDescription,
            cr.involveExternalSystem,
            cr.crossService,
            cr.includeShell,
            cr.codeList,
            cr.shellPath,
            cr.configList,
            cr.remark,
            cr.version,
            cr.changeDesc,
            cr.createdTime,
            cr.updatedTime,
            cr.createdBy,
            cr.updatedBy,
            cr.isDeleted
          ].map(escape).join(',');
          lines.push(line);
        });
        // 加入UTF-8 BOM以防止Excel中文乱码
        const csv = '\uFEFF' + lines.join('\n');
        const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        const today = new Date();
        const yyyy = today.getFullYear();
        const mm = String(today.getMonth() + 1).padStart(2, '0');
        const dd = String(today.getDate()).padStart(2, '0');
        a.href = url;
        a.download = `code_script_change_record_${yyyy}${mm}${dd}.csv`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      })
        .catch(() => { this.$message && this.$message.error('CSV导出失败'); });
    },
    // 新增：按筛选条件拉取原始数据（最多limit条）用于CSV
    async fetchListRaw(limit = 1000) {
      // 记录CSV导出使用的状态参数
      console.log('CSV导出使用的状态参数:', this.search.currentStatus || undefined);
      const params = {
        page: 1,
        size: limit,
        groupName: this.search.groupName || undefined,
        developer: this.search.developer || undefined,
        serviceName: this.search.serviceName || undefined,
        defectNumber: this.search.defectNumber || undefined,
        developType: this.search.developType || undefined,
        currentStatus: this.search.currentStatus || undefined
      };
      if (this.searchRange && this.searchRange.length === 2) {
        params.startTime = this.formatDateTimeParam(this.searchRange[0]);
        params.endTime = this.formatDateTimeParam(this.searchRange[1]);
      }
      return listChangeRecords(params).then(data => data.records || []);
    },
    
    // 新增：判断是否可以提交审批
    canSubmitApproval(row) {
      // 仅当记录状态为"待审批"且用户是创建人时显示
      try {
        const raw = localStorage.getItem('user');
        const u = JSON.parse(raw || '{}');
        const rawId = (u && (u.usernumb || u.userName)) || '';
        const id = String(rawId).toUpperCase();
        return row.currentStatus === '待审批' && (row.createdBy || '').toUpperCase() === id;
      } catch (e) {
        return false;
      }
    },
    
    // 新增：提交审批
    async submitApproval(row) {
      try {
        // 调用提交审批API
        await submitApproval(row.id);
        this.$message.success('提交审批成功');
        this.fetchList(this.page);
      } catch (e) {
        this.$message.error('提交审批失败');
      }
    },
    //分页
    handleSizeChange(val) {
      this.page = 1; //page 第几页
      this.pageSize = val; //查多少条
      this.fetchList(this.page);
    },
    handleCurrentChange(val) {
      this.page = val;
      this.fetchList(this.page);
    },
    // 新增：重置搜索条件方法
    resetSearch() {
      this.search = {
        groupName: '',
        developer: '',
        serviceName: '',
        defectNumber: '',
        currentStatus: '',
        developType: ''
      };
      this.searchRange = [];
      this.exportFormat = 'xlsx';
    },

    // 新增：切换搜索项展开/收起状态
    toggleSearchExpanded() {
      this.isSearchExpanded = !this.isSearchExpanded;
    },
  }
};
</script>

<style scoped>
.change-record-page {
  padding: 0;
  /* max-width: 1400px; */
  margin: 0 auto;
  padding: 0 8px;
}

/* 搜索栏样式 */
.search-bar {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

/* 新增：搜索容器主布局 */
.search-container {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  width: 100%;
}

/* 搜索输入区域 */
.search-inputs-wrapper {
  display: flex;
  flex: 1;
  min-width: 0;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}

/* 按钮组区域 */
.search-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

.search-input {
  width: 140px;
  min-width: 120px;
}

.search-input ::v-deep .el-input__inner {
  border-radius: 8px;
  border: 1px solid #EAEAEA;
  transition: all 0.2s ease;
  font-size: 14px;
}

.search-input ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

.search-date {
  width: 400px;
  min-width: 280px;
}

.search-date ::v-deep .el-input__inner {
  border-radius: 8px;
  border: 1px solid #EAEAEA;
  transition: all 0.2s ease;
}

.search-date ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

.search-select {
  width: 140px;
  min-width: 120px;
}

.search-select ::v-deep .el-input__inner {
  border-radius: 8px;
  border: 1px solid #EAEAEA;
  transition: all 0.2s ease;
}

.search-select ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

/* 展开/收起按钮样式 */
.expand-btn {
  color: #7B68EE;
  font-size: 14px;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.expand-btn:hover {
  color: #9370DB;
  background: rgba(123, 104, 238, 0.1);
}

.expand-btn .el-icon-arrow-down {
  margin-left: 4px;
  transition: transform 0.2s ease;
}

.expand-btn .el-icon-arrow-down.rotate-180 {
  transform: rotate(180deg);
}

.action-btn {
  border-radius: 8px;
  padding: 10px 16px;
  font-weight: 500;
  transition: all 0.2s ease;
  border: none;
  font-size: 14px;
  white-space: nowrap;
}

/* 重置按钮样式 */
.reset-btn {
  background: #f0f0f0;
  color: #606266;
}

.reset-btn:hover {
  background: #e6e6e6;
  color: #409EFF;
}

.action-btn.el-button--primary {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  color: #fff;
}

.action-btn.el-button--primary:hover {
  background: linear-gradient(135deg, #9370DB 0%, #BA55D3 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(123, 104, 238, 0.3);
}

.create-btn {
  background: #2196F3;
  color: #fff;
}

.create-btn:hover {
  background: #1976D2;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.3);
}

.export-btn {
  background: #ffffff;
  color: #1A1A2E;
  border: 1px solid #EAEAEA;
}

.export-btn:hover {
  background: rgba(255, 255, 255, 0.9);
  border-color: #7B68EE;
  color: #7B68EE;
}

/* 表格容器 */
.table-container {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.5);
}

/* 响应式设计 */
@media screen and (max-width: 1200px) {
  .search-container {
    flex-direction: column;
    align-items: stretch;
  }

  .search-inputs-wrapper {
    width: 100%;
    margin-bottom: 12px;
  }

  .search-buttons {
    justify-content: flex-start;
  }
}

@media screen and (max-width: 768px) {

  .search-input,
  .search-select {
    width: calc(50% - 4px);
    min-width: calc(50% - 4px);
  }

  .search-date {
    width: 100%;
    min-width: unset;
  }

  .search-buttons {
    flex-wrap: wrap;
  }

  .action-btn {
    flex: 1;
    min-width: 80px;
  }
}

@media screen and (max-width: 480px) {

  .search-input,
  .search-select {
    width: 100%;
    min-width: unset;
  }

  .expand-btn {
    width: 100%;
    text-align: center;
  }
}

/* 表格容器 */
.table-container {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  overflow: auto;
  border: 1px solid rgba(255, 255, 255, 0.5);
  margin-bottom: 20px;
  max-width: 100%;
  width: 100%;
}

/* 数据表格基础样式 */
.data-table {
  width: 100%;
  min-width: 1200px;
  table-layout: auto;
}

/* 17英寸屏幕优化 (1920x1080) */
@media screen and (min-width: 1920px) {
  .data-table {
    min-width: 100%;
    table-layout: fixed;
  }
  
  /* 精确调整各列宽度以适应17英寸屏幕 */
  .data-table ::v-deep .el-table-column--fixed-right {
    width: 240px !important;
  }
  
  /* 基础信息列 */
  .data-table ::v-deep .el-table-column:nth-child(2) {
    width: 160px !important;
  }
  .data-table ::v-deep .el-table-column:nth-child(3) {
    width: 120px !important;
  }
  .data-table ::v-deep .el-table-column:nth-child(4) {
    width: 140px !important;
  }
  .data-table ::v-deep .el-table-column:nth-child(5) {
    width: 140px !important;
  }
  .data-table ::v-deep .el-table-column:nth-child(6) {
    width: 140px !important;
  }
  .data-table ::v-deep .el-table-column:nth-child(7) {
    width: 140px !important;
  }
  .data-table ::v-deep .el-table-column:nth-child(8) {
    width: 140px !important;
  }
  .data-table ::v-deep .el-table-column:nth-child(9) {
    width: 160px !important;
  }
  
  /* 问题与方案列 */
  .data-table ::v-deep .el-table-column:nth-child(10) {
    width: 220px !important;
  }
  .data-table ::v-deep .el-table-column:nth-child(11) {
    width: 220px !important;
  }
  .data-table ::v-deep .el-table-column:nth-child(12) {
    width: 220px !important;
  }
  .data-table ::v-deep .el-table-column:nth-child(13) {
    width: 220px !important;
  }
  
  /* 影响范围列 */
  .data-table ::v-deep .el-table-column:nth-child(14) {
    width: 120px !important;
  }
  .data-table ::v-deep .el-table-column:nth-child(15) {
    width: 120px !important;
  }
  .data-table ::v-deep .el-table-column:nth-child(16) {
    width: 120px !important;
  }
}

/* 10-17英寸设备 (平板和小型桌面) */
@media screen and (min-width: 1024px) and (max-width: 1919px) {
  .data-table {
    min-width: 100%;
    table-layout: auto;
  }
  
  /* 智能列宽调整 - 缩小部分列宽度 */
  .data-table ::v-deep .el-table-column:nth-child(2) {
    width: 140px !important;
  }
  .data-table ::v-deep .el-table-column:nth-child(10),
  .data-table ::v-deep .el-table-column:nth-child(11),
  .data-table ::v-deep .el-table-column:nth-child(12),
  .data-table ::v-deep .el-table-column:nth-child(13) {
    width: 180px !important;
  }
}

/* 10英寸及以下设备 (手机) */
@media screen and (max-width: 1023px) {
  .table-container {
    padding: 12px;
    border-radius: 8px;
    margin-bottom: 16px;
  }
  
  .data-table {
    min-width: 1200px;
    table-layout: fixed;
  }
  
  /* 固定首列 */
  .data-table ::v-deep .el-table__body-wrapper {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: thin;
    scrollbar-color: #c1c1c1 #f0f0f0;
  }
  
  /* 滚动条样式优化 */
  .data-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar {
    height: 8px;
  }
  
  .data-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar-track {
    background: #f0f0f0;
    border-radius: 4px;
  }
  
  .data-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 4px;
  }
  
  .data-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
  }
  
  /* 调整列宽以适应移动设备 */
  .data-table ::v-deep .el-table-column:nth-child(2) {
    width: 120px !important;
    min-width: 120px;
  }
  
  .data-table ::v-deep .el-table-column:nth-child(3),
  .data-table ::v-deep .el-table-column:nth-child(4),
  .data-table ::v-deep .el-table-column:nth-child(5),
  .data-table ::v-deep .el-table-column:nth-child(6),
  .data-table ::v-deep .el-table-column:nth-child(7),
  .data-table ::v-deep .el-table-column:nth-child(8),
  .data-table ::v-deep .el-table-column:nth-child(9) {
    width: 100px !important;
    min-width: 100px;
  }
  
  .data-table ::v-deep .el-table-column:nth-child(10),
  .data-table ::v-deep .el-table-column:nth-child(11),
  .data-table ::v-deep .el-table-column:nth-child(12),
  .data-table ::v-deep .el-table-column:nth-child(13) {
    width: 160px !important;
    min-width: 160px;
  }
  
  /* 操作列固定在右侧 */
  .data-table ::v-deep .el-table-column--fixed-right {
    width: 180px !important;
    z-index: 10;
  }
  
  /* 调整表格字体大小 */
  .data-table ::v-deep .el-table__header th,
  .data-table ::v-deep .el-table__body td {
    font-size: 13px !important;
  }
  
  /* 调整表格行高 */
  .data-table ::v-deep .el-table__body td {
    padding: 8px 10px !important;
  }
  
  .data-table ::v-deep .el-table__header th {
    padding: 12px 10px !important;
  }
}

/* 触控设备优化 */
@media (hover: none) and (pointer: coarse) {
  .data-table ::v-deep .el-table__body-wrapper {
    scrollbar-width: none;
  }
  
  .data-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar {
    display: none;
  }
  
  /* 优化触摸交互 */
  .table-btn {
    padding: 8px 16px;
    font-size: 13px;
    margin-right: 4px;
  }
  
  /* 增大点击区域 */
  .data-table ::v-deep .el-button--mini {
    padding: 8px 16px;
  }
}

/* 确保表格内容清晰可读 */
.data-table ::v-deep .el-table__body td {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 400;
  line-height: 1.4;
}

.data-table ::v-deep .el-table__header th {
  font-weight: 600;
  text-align: center;
  background: #ffffff;
}

.data-table ::v-deep .el-table__body tr {
  transition: all 0.2s ease;
}

.data-table ::v-deep .el-table__body tr:hover {
  background: linear-gradient(90deg,
      rgba(255, 182, 193, 0.1) 0%,
      rgba(221, 160, 221, 0.1) 100%) !important;
}

.data-table ::v-deep .el-table__row {
  border-bottom: 1px solid #EAEAEA;
}

.data-table ::v-deep .el-table__row:nth-child(even) {
  background-color: #fafafa;
}

/* 表格边框和分隔线清晰显示 */
.data-table ::v-deep .el-table {
  border: 1px solid #EAEAEA;
  border-radius: 12px;
  overflow: hidden;
}

.data-table ::v-deep .el-table__inner-wrapper {
  border-radius: 12px;
  overflow: hidden;
}

.data-table ::v-deep .el-table__header-wrapper {
  border-bottom: 2px solid #EAEAEA;
}

.data-table ::v-deep .el-table__header th {
  border-right: 1px solid #EAEAEA;
}

.data-table ::v-deep .el-table__body td {
  border-right: 1px solid #F5F5F5;
}

/* 优化列宽调整后的对齐 */
.data-table ::v-deep .el-table th.gutter {
  display: table-cell !important;
}

.data-table ::v-deep .el-table colgroup.gutter {
  display: table-cell !important;
}

/* 确保表格和搜索栏宽度匹配 */
.search-bar,
.table-container,
.pagination-container {
  width: 100%;
  box-sizing: border-box;
}

/* 表格按钮 */
.table-btn {
  border-radius: 6px;
  padding: 6px 12px;
  font-size: 12px;
  border: 1px solid #EAEAEA;
  background: #fff;
  color: #1A1A2E;
  transition: all 0.2s ease;
  margin-right: 6px;
}

.table-btn:hover {
  border-color: #7B68EE;
  color: #7B68EE;
  background: linear-gradient(90deg,
      rgba(255, 182, 193, 0.15) 0%,
      rgba(221, 160, 221, 0.15) 100%);
}

.history-btn:hover {
  border-color: #87CEEB;
  color: #87CEEB;
  background: rgba(173, 216, 230, 0.15);
}

.detail-btn:hover {
  border-color: #9370DB;
  color: #9370DB;
  background: rgba(221, 160, 221, 0.15);
}

/* 分页样式 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding: 16px 0;
  width: 100%;
  box-sizing: border-box;
}

/* 响应式分页样式 */
@media screen and (max-width: 1023px) {
  .pagination-container {
    margin-top: 16px;
    padding: 12px 0;
  }
  
  .pagination-container ::v-deep .el-pagination {
    justify-content: center;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .pagination-container ::v-deep .el-pagination .el-pager {
    flex-wrap: wrap;
    justify-content: center;
    margin: 0;
  }
  
  .pagination-container ::v-deep .el-pagination .el-pager li {
    margin: 2px;
    padding: 5px 10px;
  }
}

/* 弹窗样式 */
.form-dialog ::v-deep .el-dialog {
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.form-dialog ::v-deep .el-dialog__header {
  background: linear-gradient(135deg,
      rgba(255, 182, 193, 0.3) 0%,
      rgba(255, 218, 185, 0.25) 25%,
      rgba(173, 216, 230, 0.3) 50%,
      rgba(221, 160, 221, 0.25) 75%,
      rgba(255, 182, 193, 0.3) 100%);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  padding: 20px 24px;
  border-radius: 12px 12px 0 0;
}

.form-dialog ::v-deep .el-dialog__title {
  color: #5a5a5a;
  font-weight: 600;
  font-size: 16px;
}

.form-dialog ::v-deep .el-dialog__headerbtn .el-dialog__close {
  color: #666;
  font-size: 20px;
}

.form-dialog ::v-deep .el-dialog__body {
  padding: 24px;
}

.form-dialog ::v-deep .el-button {
  border-radius: 8px;
  padding: 10px 20px;
  font-size: 14px;
  transition: all 0.2s ease;
}

.form-dialog ::v-deep .el-button--primary {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  border: none;
}

.form-dialog ::v-deep .el-button--primary:hover {
  background: linear-gradient(135deg, #9370DB 0%, #BA55D3 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(123, 104, 238, 0.3);
}

/* Tag标签样式优化 */
.data-table ::v-deep .el-tag {
  border-radius: 6px;
  border: none;
  padding: 4px 12px;
  font-weight: 500;
}

.data-table ::v-deep .el-tag--success {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.data-table ::v-deep .el-tag--warning {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.data-table ::v-deep .el-tag--info {
  background: rgba(144, 147, 153, 0.1);
  color: #909399;
}
</style>