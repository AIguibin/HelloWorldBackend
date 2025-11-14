<template>
  <div style="padding:16px;">
    <!-- 搜索栏 -->
    <div style="margin-bottom:12px; display:flex; gap:8px; align-items:center;">
      <el-input v-model="search.groupName" placeholder="组名" style="width:120px;" />
      <el-input v-model="search.developer" placeholder="开发负责人" style="width:120px;" />
      <el-input v-model="search.serviceName" placeholder="服务名称" style="width:120px;" />
      <el-input v-model="search.defectNumber" placeholder="缺陷编号" style="width:120px;" />
      <!-- 已移除旧的发布状态过滤（isReleased） -->
      <el-date-picker v-model="searchRange" type="datetimerange" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" style="width:400px;" />
      <el-select v-model="search.currentStatus" placeholder="当前状态" style="width:120px;">
        <el-option label="待审批" value="待审批" />
        <el-option label="待评审" value="待评审" />
        <el-option label="待合版" value="待合版" />
        <el-option label="已合版" value="已合版" />
      </el-select>
      <el-select v-model="search.developType" placeholder="开发类别" style="width:120px;">
        <el-option label="前端" value="前端" />
        <el-option label="后端" value="后端" />
        <el-option label="脚本" value="脚本" />
      </el-select>
      <el-select v-model="exportFormat" placeholder="导出格式" style="width: 120px;">
        <el-option label="Excel (XLSX)" value="xlsx" />
        <el-option label="CSV" value="csv" />
      </el-select>
      <el-button type="primary" @click="fetchList(1)">查询</el-button>
      <el-button type="success" @click="openCreate">新增</el-button>
      <el-button type="info" plain @click="onExport">下载</el-button>
      <div style="flex:1"></div>
      <el-button type="warning" @click="$router.push('/change-password')">修改密码</el-button>
      <el-button type="danger" plain @click="logout">退出登录</el-button>
      <el-button type="primary" plain @click="goLogin">返回登录</el-button>
    </div>

    <!-- 修复：移除嵌套的表格与表格内操作栏，仅保留单个列表表格 -->
    <el-table :data="list" border stripe :header-cell-style="headerCellStyle">
      <el-table-column width="210">
          <template slot="header">
            <span>操作</span>
          </template>
          <template slot-scope="scope">
            <el-button size="mini" @click="openEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="primary" @click="openHistory(scope.row)">历史</el-button>
            <el-button size="mini" type="info" @click="goDetail(scope.row)">详情</el-button>
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
      <el-table-column prop="remark" label="备注说明"  show-overflow-tooltip="true"/>
    </el-table>

    <div class="pagination_box mt-16">
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
    <el-dialog :visible.sync="showForm" title="变更记录" width="60%">
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
    headerCellStyle() {
      return { backgroundColor: 'green', color: '#fff', fontWeight: 'bold', textAlign: 'center' };
    }
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
    logout() {
      try {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
      } catch (e) {}
      this.$router.replace('/login');
    },
    goLogin() {
      this.$router.push('/login');
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
.table-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.actions-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.actions-right {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>