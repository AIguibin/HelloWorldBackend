<template>
  <div class="change-record-page">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <div class="search-inputs">
        <el-input v-model="search.groupName" placeholder="组名" class="search-input" />
        <el-input v-model="search.developer" placeholder="开发负责人" class="search-input" />
        <el-input v-model="search.serviceName" placeholder="服务名称" class="search-input" />
        <el-input v-model="search.defectNumber" placeholder="缺陷编号" class="search-input" />
        <el-date-picker 
          v-model="searchRange" 
          type="datetimerange" 
          range-separator="至" 
          start-placeholder="开始时间" 
          end-placeholder="结束时间" 
          class="search-date"
        />
        <el-select v-model="search.currentStatus" placeholder="当前状态" class="search-select">
          <el-option label="待审批" value="待审批" />
          <el-option label="待评审" value="待评审" />
          <el-option label="待合版" value="待合版" />
          <el-option label="已合版" value="已合版" />
        </el-select>
        <el-select v-model="search.developType" placeholder="开发类别" class="search-select">
          <el-option label="前端" value="前端" />
          <el-option label="后端" value="后端" />
          <el-option label="脚本" value="脚本" />
        </el-select>
        <el-select v-model="exportFormat" placeholder="导出格式" class="search-select">
          <el-option label="Excel (XLSX)" value="xlsx" />
          <el-option label="CSV" value="csv" />
        </el-select>
      </div>
      <div class="search-buttons">
        <el-button type="primary" @click="fetchList(1)" class="action-btn">查询</el-button>
        <el-button @click="openCreate" class="action-btn create-btn">新增</el-button>
        <el-button @click="onExport" class="action-btn export-btn">下载</el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table :data="list" stripe class="data-table">
      <el-table-column width="240" label="操作" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" @click="openEdit(scope.row)" class="table-btn">编辑</el-button>
            <el-button size="mini" @click="openHistory(scope.row)" class="table-btn history-btn">历史</el-button>
            <el-button size="mini" @click="goDetail(scope.row)" class="table-btn detail-btn">详情</el-button>
          </template>
      </el-table-column>
      <!-- 序号（原ID） -->
      <!-- <el-table-column prop="id" label="序号" width="80" /> -->
      <el-table-column prop="version" label="版本号" width="160" show-overflow-tooltip="true"/>
      <!-- 当前状态与发版日期 -->
      <el-table-column prop="currentStatus" label="当前状态" width="120">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.currentStatus)">{{ scope.row.currentStatus || '待审批' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="releaseDate" label="发版日期" width="140" show-overflow-tooltip="true"/>
      <!-- 基本信息 -->
      <el-table-column prop="defectNumber" label="缺陷编号" width="140" show-overflow-tooltip="true"/>
      <el-table-column prop="groupName" label="组名" width="140" show-overflow-tooltip="true"/>
      <el-table-column prop="developer" label="开发负责人" width="140" show-overflow-tooltip="true"/>
      <el-table-column prop="developType" label="开发类别" width="140" show-overflow-tooltip="true"/>
      <el-table-column prop="branchName" label="分支名称" width="140" show-overflow-tooltip="true"/>
      <el-table-column prop="serviceName" label="服务名称" width="160" show-overflow-tooltip="true"/>
      <!-- 问题与方案 -->
      <el-table-column prop="problemDescription" width="220" label="问题描述" show-overflow-tooltip="true"/>
      <el-table-column prop="changeDesc" width="220" label="变更描述" show-overflow-tooltip="true"/>
      <el-table-column prop="impactAnalysis" width="220" label="影响分析" show-overflow-tooltip="true"/>
      <el-table-column prop="solution" label="解决方案" show-overflow-tooltip="true"/>
      <el-table-column prop="codeList" width="220" label="代码清单" show-overflow-tooltip="true"/>
      <!-- 影响范围 -->
      <el-table-column prop="involveExternalSystem" label="涉及外部系统" width="120" show-overflow-tooltip="true">
        <template slot-scope="scope">
          <el-tag :type="scope.row.involveExternalSystem ? 'warning' : 'info'">{{ scope.row.involveExternalSystem ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="crossService" label="是否跨服务" width="120" show-overflow-tooltip="true">
        <template slot-scope="scope">
          <el-tag :type="scope.row.crossService ? 'warning' : 'info'">{{ scope.row.crossService ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注说明" show-overflow-tooltip="true"/>
    </el-table>
    </div>

    <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="page"
          :page-sizes="[5, 10, 20, 30]"
          :page-size="pageSize"
          layout="total,sizes,prev,pager,next,jumper"
          :total="total"
        >
        </el-pagination>
    </div>

    <!-- 表单弹窗 -->
    <el-dialog :visible.sync="showForm" title="变更记录" width="60%" class="form-dialog">
      <change-record-form ref="changeRecordForm" :form="form"/>
      <div style="text-align:right; margin-top:12px;">
        <el-button @click="showForm=false">取 消</el-button>
        <el-button type="primary" @click="onSubmit" >确 定</el-button>
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
        <el-table-column prop="developer" label="开发负责人" width="140" />
        <el-table-column prop="branchName" label="分支名称" width="140" />
        <el-table-column prop="serviceName" label="服务名称" width="160" />
        <el-table-column prop="problemDescription" label="问题描述" />
        <el-table-column prop="impactAnalysis" label="问题影响分析" />
        <el-table-column prop="solution" label="解决方案" />
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
import { listChangeRecords, createChangeRecord, updateChangeRecord, deleteChangeRecord, exportChangeRecords } from '../api';
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
      // 移除 isReleased 搜索项，保留其他
      search: { groupName: '', developer: '', serviceName: '', defectNumber: '',currentStatus:'',developType:'' },
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
        developer: '',
        branchName: '',
        serviceName: '',
        problemDescription: '',
        impactAnalysis: '',
        solution: '',
        involveExternalSystem: 0,
        crossService: 0,
        codeList: '',
        remark: '',
        version: '',
        changeDesc: ''
      },
      showHistory: false,
      historyList: [],
      historyTarget: null
    };
  },
  created() {
    this.fetchList();
  },
  computed: {
    canEdit() {
      try {
        const raw = localStorage.getItem('user');
        const u = JSON.parse(raw || '{}');
        const rawId = (u && (u.usernumb || u.username)) || '';
        const id = String(rawId).toUpperCase();
        const white = ['ADMIN', 'BG001', 'BG002'];
        return white.includes(id);
      } catch (e) {
        return false;
      }
    },
  },
  methods: {
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
        developer: '',
        branchName: '',
        serviceName: '',
        problemDescription: '',
        impactAnalysis: '',
        solution: '',
        involveExternalSystem: 0,
        crossService: 0,
        codeList: '',
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
      this.$refs['changeRecordForm'].$refs.form.validate(async(valid) => {
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
        case '待审批': return 'warning';
        case '待评审': return 'info';
        case '待合版': return 'primary';
        case '已合版': return 'success';
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
        // 使用表单选择的状态参数，而非固定默认值
        const statusParam = this.search.currentStatus || undefined;
        console.log('Excel导出使用的状态参数:', statusParam);
        const params = { currentStatus: statusParam, limit: 1000 };
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
          const headers = ['序号','当前状态','发版日期','缺陷编号','组别','开发负责人','分支名称','服务名称','问题描述','影响分析','解决方案','涉及外部系统','跨服务','代码清单','备注','版本号','变更描述','创建时间','更新时间','创建人','更新人','删除标志'];
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
              cr.developer,
              cr.branchName,
              cr.serviceName,
              cr.problemDescription,
              cr.impactAnalysis,
              cr.solution,
              cr.involveExternalSystem,
              cr.crossService,
              cr.codeList,
              cr.remark,
              cr.version,
              cr.changeDesc,
              cr.createTime,
              cr.updateTime,
              cr.createUser,
              cr.updateUser,
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
  }
};
</script>

<style scoped>
.change-record-page {
  padding: 0;
}

/* 搜索栏样式 */
.search-bar {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.search-inputs {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}

.search-input {
  width: 140px;
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

.search-buttons {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.action-btn {
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 500;
  transition: all 0.2s ease;
  border: none;
  font-size: 14px;
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

.data-table {
  width: 100%;
}

.data-table ::v-deep .el-table__header {
  background: #ffffff;
}

.data-table ::v-deep .el-table__header th {
  background: rgba(255, 255, 255, 0.9);
  color: #5a5a5a;
  font-weight: 600;
  border-bottom: 2px solid rgba(173, 216, 230, 0.3);
  padding: 16px 0;
  font-size: 14px;
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
}

.pagination-container ::v-deep .el-pagination {
  display: flex;
  align-items: center;
}

.pagination-container ::v-deep .el-pagination .el-pager li {
  border-radius: 6px;
  margin: 0 4px;
  transition: all 0.3s ease;
}

.pagination-container ::v-deep .el-pagination .el-pager li.active {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  color: #fff;
}

.pagination-container ::v-deep .el-pagination .btn-prev,
.pagination-container ::v-deep .el-pagination .btn-next {
  border-radius: 6px;
  transition: all 0.2s ease;
}

.pagination-container ::v-deep .el-pagination .btn-prev:hover,
.pagination-container ::v-deep .el-pagination .btn-next:hover {
  color: #7B68EE;
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