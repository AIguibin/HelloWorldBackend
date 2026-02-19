<template>
  <div class="net-link-list">
    <el-card shadow="hover" class="card-container">
      <template slot="header">
        <div class="card-header">
          <h2 class="card-title">
            <i class="el-icon-connection"></i>
            信贷系统外部链接网络管理清单
          </h2>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd" class="action-btn add-btn">
              <i class="el-icon-plus"></i>
              新增链路
            </el-button>
            <el-button type="success" @click="handleExport" class="action-btn export-btn">
              <i class="el-icon-download"></i>
              导出Excel
            </el-button>
            <el-button type="info" @click="handlePrint" class="action-btn print-btn">
              <i class="el-icon-printer"></i>
              打印
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 筛选区域 -->
      <div class="filter-section">
        <el-form :inline="true" :model="filters" class="filter-form">
          <el-form-item label="源环境">
            <el-select v-model="filters.sourceEnv" placeholder="所有环境" clearable class="filter-select">
              <el-option label="所有环境" value="all"></el-option>
              <el-option 
                v-for="item in envOptions" 
                :key="item.dictValue" 
                :label="item.dictLabel" 
                :value="item.dictValue">
              </el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="目标环境">
            <el-select v-model="filters.targetEnv" placeholder="所有环境" clearable class="filter-select">
              <el-option label="所有环境" value="all"></el-option>
              <el-option 
                v-for="item in envOptions" 
                :key="item.dictValue" 
                :label="item.dictLabel" 
                :value="item.dictValue">
              </el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="状态">
            <el-select v-model="filters.status" placeholder="所有状态" clearable class="filter-select">
              <el-option label="所有状态" value="all"></el-option>
              <el-option 
                v-for="item in statusOptions" 
                :key="item.dictValue" 
                :label="item.dictLabel" 
                :value="item.dictValue">
              </el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="协议">
            <el-select v-model="filters.protocol" placeholder="所有协议" clearable class="filter-select">
              <el-option label="所有协议" value="all"></el-option>
              <el-option 
                v-for="item in protocolOptions" 
                :key="item.dictValue" 
                :label="item.dictLabel" 
                :value="item.dictValue">
              </el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="关键字">
            <el-input 
              v-model="filters.keyword" 
              placeholder="搜索链路编号、名称、IP、场景等" 
              clearable
              class="filter-input"
              @keyup.enter.native="handleSearch">
              <i slot="prefix" class="el-input__icon el-icon-search"></i>
            </el-input>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleSearch" class="search-btn">
              <i class="el-icon-search"></i>
              查询
            </el-button>
            <el-button @click="handleReset" class="reset-btn">
              <i class="el-icon-refresh-left"></i>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 数据表格 -->
      <div class="table-section">
        <el-table 
          :data="tableData" 
          stripe 
          border
          v-loading="loading"
          class="data-table"
          style="width: 100%">
          <el-table-column prop="linkCode" label="链路编号" width="180" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="linkName" label="链路名称" min-width="200" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column label="源系统/环境" width="180">
            <template slot-scope="scope">
              <div>{{ scope.row.sourceSystem }}</div>
              <el-tag :type="getEnvTagType(scope.row.sourceEnv)" size="mini" class="env-tag">
                {{ getEnvLabel(scope.row.sourceEnv) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="sourceIp" label="源IP地址" width="140"></el-table-column>
          <el-table-column label="目标系统/环境" width="180">
            <template slot-scope="scope">
              <div>{{ scope.row.targetSystem }}</div>
              <el-tag :type="getEnvTagType(scope.row.targetEnv)" size="mini" class="env-tag">
                {{ getEnvLabel(scope.row.targetEnv) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="目标主机:端口" width="160">
            <template slot-scope="scope">
              {{ scope.row.targetHost }}:{{ scope.row.targetPort }}
            </template>
          </el-table-column>
          <el-table-column prop="protocol" label="连接协议" width="120">
            <template slot-scope="scope">
              <el-tag size="mini" class="protocol-tag">{{ scope.row.protocol }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="authMethod" label="认证方式" width="180" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="scenario" label="使用场景" min-width="200" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="owner" label="负责人" width="100"></el-table-column>
          <el-table-column prop="status" label="链路状态" width="120">
            <template slot-scope="scope">
              <el-tag :type="getStatusTagType(scope.row.status)" size="mini" class="status-tag">
                {{ getStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template slot-scope="scope">
              <el-button size="mini" type="primary" @click="handleEdit(scope.row)" class="table-btn edit-btn">
                <i class="el-icon-edit"></i>
                编辑
              </el-button>
              <el-button size="mini" type="danger" @click="handleDelete(scope.row)" class="table-btn delete-btn">
                <i class="el-icon-delete"></i>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
          :current-page="pagination.page"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pagination.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total">
        </el-pagination>
      </div>
    </el-card>
    
    <!-- 新增/编辑弹窗 -->
    <el-dialog 
      :title="dialogTitle" 
      :visible.sync="dialogVisible" 
      width="800px"
      :close-on-click-modal="false"
      class="form-dialog">
      <el-form 
        :model="form" 
        :rules="rules" 
        ref="form" 
        label-width="120px"
        class="dialog-form">
        <el-form-item label="链路编号" prop="linkCode">
          <el-input v-model="form.linkCode" placeholder="留空则自动生成" :disabled="isEdit" class="form-input"></el-input>
        </el-form-item>
        <el-form-item label="链路名称" prop="linkName">
          <el-input v-model="form.linkName" placeholder="请输入链路名称" class="form-input"></el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="源系统名称" prop="sourceSystem">
              <el-input v-model="form.sourceSystem" placeholder="请输入源系统名称" class="form-input"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="源环境" prop="sourceEnv">
              <el-select v-model="form.sourceEnv" placeholder="请选择源环境" class="form-select">
                <el-option 
                  v-for="item in envOptions" 
                  :key="item.dictValue" 
                  :label="item.dictLabel" 
                  :value="item.dictValue">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="源IP地址" prop="sourceIp">
          <el-input v-model="form.sourceIp" placeholder="请输入源IP地址" class="form-input"></el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="目标系统名称" prop="targetSystem">
              <el-input v-model="form.targetSystem" placeholder="请输入目标系统名称" class="form-input"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标环境" prop="targetEnv">
              <el-select v-model="form.targetEnv" placeholder="请选择目标环境" class="form-select">
                <el-option 
                  v-for="item in envOptions" 
                  :key="item.dictValue" 
                  :label="item.dictLabel" 
                  :value="item.dictValue">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="16">
            <el-form-item label="目标主机" prop="targetHost">
              <el-input v-model="form.targetHost" placeholder="请输入目标主机（IP或域名）" class="form-input"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="目标端口" prop="targetPort">
              <el-input-number 
                v-model="form.targetPort" 
                :min="1" 
                :max="65535" 
                placeholder="端口号"
                class="form-input-number">
              </el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="连接协议" prop="protocol">
              <el-select v-model="form.protocol" placeholder="请选择连接协议" class="form-select">
                <el-option 
                  v-for="item in protocolOptions" 
                  :key="item.dictValue" 
                  :label="item.dictLabel" 
                  :value="item.dictValue">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="链路状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择链路状态" class="form-select">
                <el-option 
                  v-for="item in statusOptions" 
                  :key="item.dictValue" 
                  :label="item.dictLabel" 
                  :value="item.dictValue">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="认证方式">
          <el-input v-model="form.authMethod" placeholder="请输入认证方式" class="form-input"></el-input>
        </el-form-item>
        <el-form-item label="使用场景">
          <el-input v-model="form.scenario" type="textarea" :rows="2" placeholder="请输入使用场景" class="form-textarea"></el-input>
        </el-form-item>
        <el-form-item label="链路描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入链路描述" class="form-textarea"></el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="负责人">
              <el-input v-model="form.owner" placeholder="请输入负责人" class="form-input"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="监控等级">
              <el-input v-model="form.monitoringLevel" placeholder="如：P0-核心" class="form-input"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" class="cancel-btn">
          <i class="el-icon-close"></i>
          取消
        </el-button>
        <el-button type="primary" @click="handleSave" :loading="saving" class="save-btn">
          <i class="el-icon-check"></i>
          保存
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { 
  listNetLinks, 
  getNetLink, 
  createNetLink, 
  updateNetLink, 
  deleteNetLink, 
  exportNetLinks,
  getDictItemsByType 
} from '@/api'

export default {
  name: 'NetLinkList',
  data() {
    return {
      loading: false,
      saving: false,
      tableData: [],
      filters: {
        sourceEnv: '',
        targetEnv: '',
        status: '',
        protocol: '',
        keyword: ''
      },
      pagination: {
        page: 1,
        size: 10,
        total: 0
      },
      dialogVisible: false,
      dialogTitle: '新增链路',
      isEdit: false,
      form: {
        linkCode: '',
        linkName: '',
        sourceSystem: '',
        sourceEnv: '',
        sourceIp: '',
        targetSystem: '',
        targetEnv: '',
        targetHost: '',
        targetPort: null,
        protocol: '',
        authMethod: '',
        scenario: '',
        description: '',
        owner: '',
        status: 'active',
        monitoringLevel: ''
      },
      rules: {
        linkName: [
          { required: true, message: '请输入链路名称', trigger: 'blur' }
        ],
        sourceSystem: [
          { required: true, message: '请输入源系统名称', trigger: 'blur' }
        ],
        sourceEnv: [
          { required: true, message: '请选择源环境', trigger: 'change' }
        ],
        sourceIp: [
          { required: true, message: '请输入源IP地址', trigger: 'blur' }
        ],
        targetSystem: [
          { required: true, message: '请输入目标系统名称', trigger: 'blur' }
        ],
        targetEnv: [
          { required: true, message: '请选择目标环境', trigger: 'change' }
        ],
        targetHost: [
          { required: true, message: '请输入目标主机', trigger: 'blur' }
        ],
        targetPort: [
          { required: true, message: '请输入目标端口', trigger: 'blur' },
          { type: 'number', min: 1, max: 65535, message: '端口号必须在1-65535之间', trigger: 'blur' }
        ],
        protocol: [
          { required: true, message: '请选择连接协议', trigger: 'change' }
        ],
        status: [
          { required: true, message: '请选择链路状态', trigger: 'change' }
        ]
      },
      envOptions: [],
      statusOptions: [],
      protocolOptions: []
    }
  },
  mounted() {
    this.loadDictOptions()
    this.loadData()
  },
  methods: {
    // 加载字典选项
    async loadDictOptions() {
      try {
        // 加载环境选项（假设字典类型编码为 NET_ENV）
        const envRes = await getDictItemsByType('NET_ENV')
        this.envOptions = envRes.data || []
        
        // 加载状态选项（假设字典类型编码为 NET_STATUS）
        const statusRes = await getDictItemsByType('NET_STATUS')
        this.statusOptions = statusRes.data || []
        
        // 加载协议选项（假设字典类型编码为 NET_PROTOCOL）
        const protocolRes = await getDictItemsByType('NET_PROTOCOL')
        this.protocolOptions = protocolRes.data || []
      } catch (error) {
        console.error('加载字典选项失败:', error)
        // 如果字典不存在，使用默认值
        this.envOptions = [
          { dictValue: 'prod', dictLabel: '生产' },
          { dictValue: 'uat', dictLabel: 'UAT' },
          { dictValue: 'sit', dictLabel: 'SIT' },
          { dictValue: 'dev', dictLabel: '开发' }
        ]
        this.statusOptions = [
          { dictValue: 'active', dictLabel: '启用中' },
          { dictValue: 'testing', dictLabel: '测试中' },
          { dictValue: 'disabled', dictLabel: '已停用' }
        ]
        this.protocolOptions = [
          { dictValue: 'HTTP', dictLabel: 'HTTP' },
          { dictValue: 'HTTPS', dictLabel: 'HTTPS' },
          { dictValue: 'TCP', dictLabel: 'TCP' },
          { dictValue: 'SFTP', dictLabel: 'SFTP' },
          { dictValue: 'MQ', dictLabel: 'MQ' }
        ]
      }
    },
    
    // 加载数据
    async loadData() {
      this.loading = true
      try {
        const params = {
          page: this.pagination.page,
          size: this.pagination.size,
          sourceEnv: this.filters.sourceEnv === 'all' ? '' : this.filters.sourceEnv,
          targetEnv: this.filters.targetEnv === 'all' ? '' : this.filters.targetEnv,
          status: this.filters.status === 'all' ? '' : this.filters.status,
          protocol: this.filters.protocol === 'all' ? '' : this.filters.protocol,
          keyword: this.filters.keyword
        }
        const data = await listNetLinks(params)
        this.tableData = data.records || []
        this.pagination.total = data.total || 0
      } catch (error) {
        this.$message.error('加载数据失败: ' + (error.message || '未知错误'))
      } finally {
        this.loading = false
      }
    },
    
    // 查询
    handleSearch() {
      this.pagination.page = 1
      this.loadData()
    },
    
    // 重置
    handleReset() {
      this.filters = {
        sourceEnv: '',
        targetEnv: '',
        status: '',
        protocol: '',
        keyword: ''
      }
      this.handleSearch()
    },
    
    // 新增
    handleAdd() {
      this.dialogTitle = '新增链路'
      this.isEdit = false
      this.form = {
        linkCode: '',
        linkName: '',
        sourceSystem: '',
        sourceEnv: '',
        sourceIp: '',
        targetSystem: '',
        targetEnv: '',
        targetHost: '',
        targetPort: null,
        protocol: '',
        authMethod: '',
        scenario: '',
        description: '',
        owner: '',
        status: 'active',
        monitoringLevel: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    
    // 编辑
    async handleEdit(row) {
      this.dialogTitle = '编辑链路'
      this.isEdit = true
      try {
        const data = await getNetLink(row.id)
        this.form = { ...data }
        this.dialogVisible = true
        this.$nextTick(() => {
          this.$refs.form && this.$refs.form.clearValidate()
        })
      } catch (error) {
        this.$message.error('获取详情失败: ' + (error.message || '未知错误'))
      }
    },
    
    // 删除
    handleDelete(row) {
      this.$confirm(`确定要删除链路"${row.linkName}"吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteNetLink(row.id)
          this.$message.success('删除成功')
          this.loadData()
        } catch (error) {
          this.$message.error('删除失败: ' + (error.message || '未知错误'))
        }
      }).catch(() => {})
    },
    
    // 保存
    handleSave() {
      this.$refs.form.validate(async (valid) => {
        if (!valid) return false
        
        this.saving = true
        try {
          const formData = { ...this.form }
          // 如果链路编号为空，设置为null让后端自动生成
          if (!formData.linkCode || formData.linkCode.trim() === '') {
            formData.linkCode = null
          }
          
          if (this.isEdit) {
            await updateNetLink(this.form.id, formData)
          } else {
            await createNetLink(formData)
          }
          
          this.$message.success(this.isEdit ? '更新成功' : '创建成功')
          this.dialogVisible = false
          this.loadData()
        } catch (error) {
          this.$message.error('保存失败: ' + (error.message || '未知错误'))
        } finally {
          this.saving = false
        }
      })
    },
    
    // 导出
    async handleExport() {
      try {
        const params = {
          sourceEnv: this.filters.sourceEnv === 'all' ? '' : this.filters.sourceEnv,
          targetEnv: this.filters.targetEnv === 'all' ? '' : this.filters.targetEnv,
          status: this.filters.status === 'all' ? '' : this.filters.status,
          protocol: this.filters.protocol === 'all' ? '' : this.filters.protocol,
          keyword: this.filters.keyword
        }
        
        // request.js拦截器对于blob响应会直接返回response对象
        const response = await exportNetLinks(params)
        
        // 从响应头获取文件名
        const contentDisposition = (response.headers && response.headers['content-disposition']) || (response.headers && response.headers['Content-Disposition']) || ''
        let fileName = '信贷系统外部链接网络管理清单.xlsx'
        if (contentDisposition) {
          const fileNameMatch = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/)
          if (fileNameMatch && fileNameMatch[1]) {
            fileName = decodeURIComponent(fileNameMatch[1].replace(/['"]/g, ''))
          }
        }
        
        // 创建下载链接
        const blob = response.data || response
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = fileName
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        
        this.$message.success('导出成功')
      } catch (error) {
        console.error('导出失败:', error)
        this.$message.error('导出失败: ' + (error.message || '未知错误'))
      }
    },
    
    // 打印
    handlePrint() {
      // 生成打印内容
      let printContent = `
        <html>
        <head>
          <title>信贷系统外部链接网络管理清单</title>
          <style>
            body { font-family: Arial, sans-serif; margin: 20px; }
            h1 { color: #2c3e50; border-bottom: 2px solid #2c3e50; padding-bottom: 10px; }
            table { width: 100%; border-collapse: collapse; margin-top: 20px; font-size: 12px; }
            th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
            th { background-color: #2c3e50; color: white; }
            tr:nth-child(even) { background-color: #f9f9f9; }
            .footer { margin-top: 30px; font-size: 12px; color: #666; }
          </style>
        </head>
        <body>
          <h1>信贷系统外部链接网络管理清单</h1>
          <p>打印时间: ${new Date().toLocaleString('zh-CN')}</p>
          <table>
            <thead>
              <tr>
                <th>链路编号</th>
                <th>链路名称</th>
                <th>源系统/环境</th>
                <th>源IP地址</th>
                <th>目标系统/环境</th>
                <th>目标主机:端口</th>
                <th>连接协议</th>
                <th>认证方式</th>
                <th>使用场景</th>
                <th>负责人</th>
                <th>链路状态</th>
              </tr>
            </thead>
            <tbody>
      `
      
      this.tableData.forEach(item => {
        printContent += `
          <tr>
            <td>${item.linkCode || ''}</td>
            <td>${item.linkName || ''}</td>
            <td>${item.sourceSystem || ''} (${this.getEnvLabel(item.sourceEnv)})</td>
            <td>${item.sourceIp || ''}</td>
            <td>${item.targetSystem || ''} (${this.getEnvLabel(item.targetEnv)})</td>
            <td>${item.targetHost || ''}:${item.targetPort || ''}</td>
            <td>${item.protocol || ''}</td>
            <td>${item.authMethod || ''}</td>
            <td>${item.scenario || ''}</td>
            <td>${item.owner || ''}</td>
            <td>${this.getStatusLabel(item.status)}</td>
          </tr>
        `
      })
      
      printContent += `
            </tbody>
          </table>
          <div class="footer">
            <p>© 2023 信贷系统网络管理清单 | 共 ${this.pagination.total} 条记录</p>
          </div>
        </body>
        </html>
      `
      
      const printWindow = window.open('', '_blank')
      printWindow.document.write(printContent)
      printWindow.document.close()
      printWindow.focus()
      
      setTimeout(() => {
        printWindow.print()
        printWindow.close()
      }, 500)
    },
    
    // 分页
    handlePageChange(page) {
      this.pagination.page = page
      this.loadData()
    },
    
    handleSizeChange(size) {
      this.pagination.size = size
      this.pagination.page = 1
      this.loadData()
    },
    
    // 获取环境标签类型
    getEnvTagType(env) {
      const map = {
        'prod': 'danger',
        'uat': 'warning',
        'sit': 'info',
        'dev': 'success'
      }
      return map[env] || 'info'
    },
    
    // 获取环境标签文本
    getEnvLabel(env) {
      const item = this.envOptions.find(opt => opt.dictValue === env)
      return item ? item.dictLabel : env
    },
    
    // 获取状态标签类型
    getStatusTagType(status) {
      const map = {
        'active': 'success',
        'testing': 'warning',
        'disabled': 'danger'
      }
      return map[status] || 'info'
    },
    
    // 获取状态标签文本
    getStatusLabel(status) {
      const item = this.statusOptions.find(opt => opt.dictValue === status)
      return item ? item.dictLabel : status
    }
  }
}
</script>

<style scoped>
/* Page container */
.net-link-list {
  width: 100%;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eaf6 100%);
  min-height: calc(100vh - 60px);
}

/* Card container */
.card-container {
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(123, 104, 238, 0.08);
  margin-bottom: 20px;
  border: 1px solid rgba(123, 104, 238, 0.1);
}

/* Card header styling */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.header-actions {
  display: flex;
  gap: 12px;
}

/* Action button styling */
.action-btn {
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 600;
  transition: all 0.3s ease;
  border: none;
  font-size: 14px;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 6px;
}

.add-btn {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  color: #fff;
}

.add-btn:hover {
  background: linear-gradient(135deg, #9370DB 0%, #BA55D3 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(123, 104, 238, 0.3);
}

.export-btn {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: #fff;
}

.export-btn:hover {
  background: linear-gradient(135deg, #85ce61 0%, #a0d868 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
}

.print-btn {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #fff;
}

.print-btn:hover {
  background: linear-gradient(135deg, #66b1ff 0%, #8cc5ff 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* Filter section */
.filter-section {
  margin-bottom: 24px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12px;
  border: 1px solid rgba(123, 104, 238, 0.1);
  box-shadow: 0 2px 8px rgba(123, 104, 238, 0.06);
}

.filter-form {
  margin: 0;
}

.filter-form ::v-deep .el-form-item__label {
  color: #5a4fcf;
  font-weight: 600;
  font-size: 14px;
}

/* Filter input styling */
.filter-input {
  width: 300px;
}

.filter-input ::v-deep .el-input__inner {
  border-radius: 8px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
  font-size: 14px;
  padding: 0 15px 0 35px;
  height: 36px;
  line-height: 36px;
}

.filter-input ::v-deep .el-input__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.filter-input ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

.filter-input ::v-deep .el-input__prefix {
  left: 10px;
  color: #7B68EE;
}

/* Filter select styling */
.filter-select {
  width: 150px;
}

.filter-select ::v-deep .el-input__inner {
  border-radius: 8px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
  font-size: 14px;
  padding: 0 15px 0 35px;
  height: 36px;
  line-height: 36px;
}

.filter-select ::v-deep .el-input__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.filter-select ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

.filter-select ::v-deep .el-input__suffix {
  right: 10px;
}

/* Search and reset button styling */
.search-btn {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  border: none;
  color: #fff;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.search-btn:hover {
  background: linear-gradient(135deg, #9370DB 0%, #BA55D3 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(123, 104, 238, 0.3);
}

.reset-btn {
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eaf6 100%);
  border: 1px solid #d4d4e8;
  color: #606266;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.reset-btn:hover {
  border-color: #f56c6c;
  color: #f56c6c;
  background: linear-gradient(135deg, #fef0f0 0%, #fde2e2 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.2);
}

/* Table section */
.table-section {
  margin-bottom: 20px;
}

/* Data table styling */
.data-table {
  border-radius: 8px;
  overflow: hidden;
}

.data-table ::v-deep .el-table__header-wrapper {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
}

.data-table ::v-deep .el-table__header th {
  background: transparent;
  color: #ffffff;
  font-weight: 600;
  font-size: 14px;
  border-color: rgba(255, 255, 255, 0.2);
  padding: 14px 12px;
}

.data-table ::v-deep .el-table__body tr {
  transition: all 0.2s ease;
}

.data-table ::v-deep .el-table__body tr:hover > td {
  background: linear-gradient(135deg, #fafbff 0%, #f5f7ff 100%);
}

.data-table ::v-deep .el-table__body td {
  padding: 12px;
  border-color: #e8e8f0;
  color: #1a1a2e;
  font-size: 14px;
}

.data-table ::v-deep .el-table__body tr:nth-child(even) {
  background: #fafbff;
}

.data-table ::v-deep .el-table__body tr:nth-child(odd) {
  background: #ffffff;
}

/* Tag styling in table */
.env-tag,
.protocol-tag,
.status-tag {
  border-radius: 4px;
  padding: 4px 10px;
  font-weight: 500;
  border: none;
  font-size: 12px;
}

.env-tag.el-tag--danger {
  background: linear-gradient(135deg, #f56c6c 0%, #ff6b6b 100%);
  color: #ffffff;
}

.env-tag.el-tag--warning {
  background: linear-gradient(135deg, #ff9800 0%, #ff6b00 100%);
  color: #ffffff;
}

.env-tag.el-tag--info {
  background: linear-gradient(135deg, #909399 0%, #606266 100%);
  color: #ffffff;
}

.env-tag.el-tag--success {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: #ffffff;
}

.protocol-tag {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  color: #ffffff;
}

.status-tag.el-tag--success {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: #ffffff;
}

.status-tag.el-tag--warning {
  background: linear-gradient(135deg, #ff9800 0%, #ff6b00 100%);
  color: #ffffff;
}

.status-tag.el-tag--danger {
  background: linear-gradient(135deg, #f56c6c 0%, #ff6b6b 100%);
  color: #ffffff;
}

/* Table button styling */
.table-btn {
  border-radius: 6px;
  padding: 6px 14px;
  font-size: 12px;
  font-weight: 500;
  border: 1px solid #d4d4e8;
  background: #fff;
  color: #1A1A2E;
  transition: all 0.3s ease;
  margin-right: 6px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.table-btn:hover {
  border-color: #7B68EE;
  color: #7B68EE;
  background: linear-gradient(135deg, #f5f7ff 0%, #e8eaf6 100%);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(123, 104, 238, 0.2);
}

.edit-btn:hover {
  border-color: #409EFF;
  color: #409EFF;
  background: rgba(64, 158, 255, 0.1);
}

.delete-btn:hover {
  border-color: #f56c6c;
  color: #f56c6c;
  background: rgba(245, 108, 108, 0.1);
}

/* Pagination section */
.pagination-section {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(123, 104, 238, 0.06);
}

/* Form dialog styling */
.form-dialog ::v-deep .el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.form-dialog ::v-deep .el-dialog__header {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  color: #ffffff;
  padding: 16px 20px;
}

.form-dialog ::v-deep .el-dialog__title {
  color: #ffffff;
  font-weight: 600;
}

.form-dialog ::v-deep .el-dialog__body {
  padding: 24px 20px;
  background: #ffffff;
}

.dialog-form ::v-deep .el-form-item__label {
  color: #5a4fcf;
  font-weight: 600;
  font-size: 14px;
}

/* Form input styling */
.form-input ::v-deep .el-input__inner {
  border-radius: 6px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
  font-size: 14px;
  padding: 0 15px;
  height: 36px;
  line-height: 36px;
}

.form-input ::v-deep .el-input__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.form-input ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

/* Form select styling */
.form-select ::v-deep .el-input__inner {
  border-radius: 6px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
  font-size: 14px;
  padding: 0 15px;
  height: 36px;
  line-height: 36px;
}

.form-select ::v-deep .el-input__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.form-select ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

/* Form input number styling */
.form-input-number ::v-deep .el-input__inner {
  border-radius: 6px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
  font-size: 14px;
  height: 36px;
  line-height: 36px;
}

.form-input-number ::v-deep .el-input__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.form-input-number ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

/* Form textarea styling */
.form-textarea ::v-deep .el-textarea__inner {
  border-radius: 6px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
  font-size: 14px;
  padding: 10px 15px;
  line-height: 1.6;
  resize: vertical;
}

.form-textarea ::v-deep .el-textarea__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.form-textarea ::v-deep .el-textarea__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

/* Dialog footer */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e8e8f0;
}

.cancel-btn {
  background: #ffffff;
  border: 1px solid #d4d4e8;
  color: #606266;
  transition: all 0.3s ease;
  padding: 10px 20px;
  border-radius: 6px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
}

.cancel-btn:hover {
  border-color: #f56c6c;
  color: #f56c6c;
  background: linear-gradient(135deg, #fef0f0 0%, #fde2e2 100%);
}

.save-btn {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  border: none;
  color: #ffffff;
  transition: all 0.3s ease;
  padding: 10px 20px;
  border-radius: 6px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;
}

.save-btn:hover {
  background: linear-gradient(135deg, #9370DB 0%, #BA55D3 100%);
  box-shadow: 0 4px 12px rgba(123, 104, 238, 0.3);
  transform: translateY(-1px);
}

/* Responsive design */
@media screen and (max-width: 1200px) {
  .net-link-list {
    padding: 16px;
  }

  .filter-section {
    padding: 16px;
  }

  .card-title {
    font-size: 16px;
  }

  .filter-input {
    width: 260px;
  }

  .filter-select {
    width: 130px;
  }
}

@media screen and (max-width: 768px) {
  .net-link-list {
    padding: 12px;
  }

  .filter-section {
    padding: 12px;
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .header-actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .action-btn {
    flex: 1;
    min-width: 80px;
  }

  .filter-form ::v-deep .el-form-item {
    width: 100%;
  }

  .filter-input,
  .filter-select {
    width: 100%;
  }

  .table-btn {
    padding: 4px 8px;
    font-size: 11px;
  }
}

@media screen and (max-width: 480px) {
  .net-link-list {
    padding: 8px;
  }

  .filter-section {
    padding: 8px;
  }

  .card-title {
    font-size: 14px;
  }

  .action-btn {
    font-size: 13px;
    padding: 8px 12px;
  }

  .filter-input ::v-deep .el-input__inner,
  .filter-select ::v-deep .el-input__inner,
  .form-input ::v-deep .el-input__inner,
  .form-select ::v-deep .el-input__inner,
  .form-input-number ::v-deep .el-input__inner {
    height: 32px;
    line-height: 32px;
    font-size: 13px;
  }

  .form-textarea ::v-deep .el-textarea__inner {
    font-size: 13px;
  }

  .table-btn {
    padding: 4px 6px;
    font-size: 10px;
    margin-right: 4px;
  }

  .data-table ::v-deep .el-table__header th {
    font-size: 12px;
    padding: 10px 8px;
  }

  .data-table ::v-deep .el-table__body td {
    font-size: 12px;
    padding: 8px;
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

.card-container,
.filter-section {
  animation: fadeIn 0.4s ease-out;
}

/* Scrollbar styling */
.data-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.data-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar-track {
  background: #f5f7fa;
  border-radius: 4px;
}

.data-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  border-radius: 4px;
}

.data-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #9370DB 0%, #BA55D3 100%);
}
</style>
