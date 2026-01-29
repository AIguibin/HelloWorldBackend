<template>
  <div class="net-link-list">
    <el-card shadow="never" class="card-container">
      <template slot="header">
        <div class="card-header">
          <span><i class="el-icon-connection"></i> 信贷系统外部链接网络管理清单</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd" icon="el-icon-plus">新增链路</el-button>
            <el-button type="success" @click="handleExport" icon="el-icon-download">导出Excel</el-button>
            <el-button type="info" @click="handlePrint" icon="el-icon-printer">打印</el-button>
          </div>
        </div>
      </template>
      
      <!-- 筛选区域 -->
      <div class="filter-section">
        <el-form :inline="true" :model="filters" class="filter-form">
          <el-form-item label="源环境">
            <el-select v-model="filters.sourceEnv" placeholder="所有环境" clearable style="width: 150px">
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
            <el-select v-model="filters.targetEnv" placeholder="所有环境" clearable style="width: 150px">
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
            <el-select v-model="filters.status" placeholder="所有状态" clearable style="width: 150px">
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
            <el-select v-model="filters.protocol" placeholder="所有协议" clearable style="width: 150px">
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
              style="width: 300px"
              @keyup.enter.native="handleSearch">
              <i slot="prefix" class="el-input__icon el-icon-search"></i>
            </el-input>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleSearch" icon="el-icon-search">查询</el-button>
            <el-button @click="handleReset" icon="el-icon-refresh">重置</el-button>
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
          style="width: 100%">
          <el-table-column prop="linkCode" label="链路编号" width="180" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="linkName" label="链路名称" min-width="200" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column label="源系统/环境" width="180">
            <template slot-scope="scope">
              <div>{{ scope.row.sourceSystem }}</div>
              <el-tag :type="getEnvTagType(scope.row.sourceEnv)" size="mini" style="margin-top: 4px">
                {{ getEnvLabel(scope.row.sourceEnv) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="sourceIp" label="源IP地址" width="140"></el-table-column>
          <el-table-column label="目标系统/环境" width="180">
            <template slot-scope="scope">
              <div>{{ scope.row.targetSystem }}</div>
              <el-tag :type="getEnvTagType(scope.row.targetEnv)" size="mini" style="margin-top: 4px">
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
              <el-tag size="mini">{{ scope.row.protocol }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="authMethod" label="认证方式" width="180" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="scenario" label="使用场景" min-width="200" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="owner" label="负责人" width="100"></el-table-column>
          <el-table-column prop="status" label="链路状态" width="120">
            <template slot-scope="scope">
              <el-tag :type="getStatusTagType(scope.row.status)" size="mini">
                {{ getStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template slot-scope="scope">
              <el-button size="mini" type="primary" @click="handleEdit(scope.row)" icon="el-icon-edit">编辑</el-button>
              <el-button size="mini" type="danger" @click="handleDelete(scope.row)" icon="el-icon-delete">删除</el-button>
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
      :close-on-click-modal="false">
      <el-form 
        :model="form" 
        :rules="rules" 
        ref="form" 
        label-width="120px">
        <el-form-item label="链路编号" prop="linkCode">
          <el-input v-model="form.linkCode" placeholder="留空则自动生成" :disabled="isEdit"></el-input>
        </el-form-item>
        <el-form-item label="链路名称" prop="linkName">
          <el-input v-model="form.linkName" placeholder="请输入链路名称"></el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="源系统名称" prop="sourceSystem">
              <el-input v-model="form.sourceSystem" placeholder="请输入源系统名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="源环境" prop="sourceEnv">
              <el-select v-model="form.sourceEnv" placeholder="请选择源环境" style="width: 100%">
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
          <el-input v-model="form.sourceIp" placeholder="请输入源IP地址"></el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="目标系统名称" prop="targetSystem">
              <el-input v-model="form.targetSystem" placeholder="请输入目标系统名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标环境" prop="targetEnv">
              <el-select v-model="form.targetEnv" placeholder="请选择目标环境" style="width: 100%">
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
              <el-input v-model="form.targetHost" placeholder="请输入目标主机（IP或域名）"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="目标端口" prop="targetPort">
              <el-input-number 
                v-model="form.targetPort" 
                :min="1" 
                :max="65535" 
                placeholder="端口号"
                style="width: 100%">
              </el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="连接协议" prop="protocol">
              <el-select v-model="form.protocol" placeholder="请选择连接协议" style="width: 100%">
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
              <el-select v-model="form.status" placeholder="请选择链路状态" style="width: 100%">
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
          <el-input v-model="form.authMethod" placeholder="请输入认证方式"></el-input>
        </el-form-item>
        <el-form-item label="使用场景">
          <el-input v-model="form.scenario" type="textarea" :rows="2" placeholder="请输入使用场景"></el-input>
        </el-form-item>
        <el-form-item label="链路描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入链路描述"></el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="负责人">
              <el-input v-model="form.owner" placeholder="请输入负责人"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="监控等级">
              <el-input v-model="form.monitoringLevel" placeholder="如：P0-核心"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
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
        const res = await listNetLinks(params)
        if (res.code === 200) {
          this.tableData = res.data.records || []
          this.pagination.total = res.data.total || 0
        } else {
          this.$message.error(res.message || '加载数据失败')
        }
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
        const res = await getNetLink(row.id)
        if (res.code === 200) {
          this.form = { ...res.data }
          this.dialogVisible = true
          this.$nextTick(() => {
            this.$refs.form && this.$refs.form.clearValidate()
          })
        } else {
          this.$message.error(res.message || '获取详情失败')
        }
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
          const res = await deleteNetLink(row.id)
          if (res.code === 200) {
            this.$message.success('删除成功')
            this.loadData()
          } else {
            this.$message.error(res.message || '删除失败')
          }
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
          
          let res
          if (this.isEdit) {
            res = await updateNetLink(this.form.id, formData)
          } else {
            res = await createNetLink(formData)
          }
          
          if (res.code === 200) {
            this.$message.success(this.isEdit ? '更新成功' : '创建成功')
            this.dialogVisible = false
            this.loadData()
          } else {
            this.$message.error(res.message || '保存失败')
          }
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
.net-link-list {
  padding: 20px;
}

.card-container {
  min-height: calc(100vh - 100px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-size: 18px;
  font-weight: bold;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.filter-section {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.filter-form {
  margin: 0;
}

.table-section {
  margin-bottom: 20px;
}

.pagination-section {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.dialog-footer {
  text-align: right;
}
</style>
