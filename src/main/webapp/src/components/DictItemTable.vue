<template>
  <div class="dict-item-table">
    <!-- 表格工具栏 -->
    <div class="toolbar">
      <el-button type="primary" @click="addItem" size="small">
        <i class="el-icon-plus"></i> 新增
      </el-button>
      <el-button type="danger" @click="deleteSelected" size="small" :disabled="!hasSelection">
        <i class="el-icon-delete"></i> 删除
      </el-button>
      <el-button type="warning" @click="refreshTable" size="small">
        <i class="el-icon-refresh"></i> 刷新
      </el-button>
    </div>

    <!-- 字典项表格 -->
    <el-table
      v-loading="loading"
      :data="itemList"
      @selection-change="handleSelectionChange"
      style="width: 100%"
      border
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="dctSeq" label="排序号" width="80">
        <template slot-scope="scope">
          <el-input-number
            v-model="scope.row.dctSeq"
            :min="1"
            size="small"
            @change="handleSeqChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="dctGrp" label="分组" width="120">
        <template slot-scope="scope">
          <el-input
            v-model="scope.row.dctGrp"
            size="small"
            @change="handleItemChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="dctKey" label="字典键" width="180">
        <template slot-scope="scope">
          <el-input
            v-model="scope.row.dctKey"
            size="small"
            @change="handleItemChange(scope.row)"
            placeholder="请输入字典键"
          />
        </template>
      </el-table-column>
      <el-table-column prop="dctValNm" label="字典值名称" width="180">
        <template slot-scope="scope">
          <el-input
            v-model="scope.row.dctValNm"
            size="small"
            @change="handleItemChange(scope.row)"
            placeholder="请输入字典值名称"
          />
        </template>
      </el-table-column>
      <el-table-column prop="dctVal" label="字典值" width="180">
        <template slot-scope="scope">
          <el-input
            v-model="scope.row.dctVal"
            size="small"
            @change="handleItemChange(scope.row)"
            placeholder="请输入字典值"
          />
        </template>
      </el-table-column>
      <el-table-column prop="dctDsc" label="描述">
        <template slot-scope="scope">
          <el-input
            v-model="scope.row.dctDsc"
            size="small"
            type="textarea"
            @change="handleItemChange(scope.row)"
            placeholder="请输入描述"
          />
        </template>
      </el-table-column>
      <el-table-column prop="stcd" label="状态" width="100">
        <template slot-scope="scope">
          <el-select
            v-model="scope.row.stcd"
            size="small"
            @change="handleItemChange(scope.row)"
          >
            <el-option label="启用" value="01" />
            <el-option label="禁用" value="02" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="editItem(scope.row)">
            编辑
          </el-button>
          <el-button type="danger" size="mini" @click="deleteItem(scope.row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="page"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      />
    </div>

    <!-- 编辑对话框 -->
    <el-dialog title="编辑字典项" :visible.sync="dialogVisible" width="600px">
      <el-form :model="form" :rules="rules" ref="form" label-width="80px">
        <el-form-item label="排序号" prop="dctSeq">
          <el-input-number v-model="form.dctSeq" :min="1" />
        </el-form-item>
        <el-form-item label="分组" prop="dctGrp">
          <el-input v-model="form.dctGrp" />
        </el-form-item>
        <el-form-item label="字典键" prop="dctKey">
          <el-input v-model="form.dctKey" placeholder="请输入字典键" />
        </el-form-item>
        <el-form-item label="字典值名称" prop="dctValNm">
          <el-input v-model="form.dctValNm" placeholder="请输入字典值名称" />
        </el-form-item>
        <el-form-item label="字典值" prop="dctVal">
          <el-input v-model="form.dctVal" placeholder="请输入字典值" />
        </el-form-item>
        <el-form-item label="描述" prop="dctDsc">
          <el-input v-model="form.dctDsc" type="textarea" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态" prop="stcd">
          <el-select v-model="form.stcd">
            <el-option label="启用" value="01" />
            <el-option label="禁用" value="02" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveItem">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'DictItemTable',
  props: {
    dictType: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      loading: false,
      itemList: [],
      selectedItems: [],
      total: 0,
      page: 1,
      size: 10,
      dialogVisible: false,
      form: {
        dctSeq: 1,
        dctGrp: '',
        dctKey: '',
        dctValNm: '',
        dctVal: '',
        dctDsc: '',
        stcd: '01'
      },
      rules: {
        dctSeq: [
          { required: true, message: '请输入排序号', trigger: 'blur' }
        ],
        dctKey: [
          { required: true, message: '请输入字典键', trigger: 'blur' }
        ],
        dctValNm: [
          { required: true, message: '请输入字典值名称', trigger: 'blur' }
        ],
        dctVal: [
          { required: true, message: '请输入字典值', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    hasSelection() {
      return this.selectedItems.length > 0
    }
  },
  mounted() {
    this.loadItems()
  },
  methods: {
    loadItems() {
      this.loading = true
      // TODO: 调用后端API加载字典项
      setTimeout(() => {
        this.itemList = [
          {
            id: '1',
            dctSeq: 1,
            dctGrp: 'basic',
            dctKey: 'STATUS_ENABLED',
            dctValNm: '启用',
            dctVal: '01',
            dctDsc: '状态-启用',
            stcd: '01'
          },
          {
            id: '2',
            dctSeq: 2,
            dctGrp: 'basic',
            dctKey: 'STATUS_DISABLED',
            dctValNm: '禁用',
            dctVal: '02',
            dctDsc: '状态-禁用',
            stcd: '01'
          }
        ]
        this.total = this.itemList.length
        this.loading = false
      }, 500)
    },
    handleSelectionChange(selection) {
      this.selectedItems = selection
    },
    handleSizeChange(val) {
      this.size = val
      this.loadItems()
    },
    handleCurrentChange(val) {
      this.page = val
      this.loadItems()
    },
    addItem() {
      this.form = {
        dctSeq: this.itemList.length + 1,
        dctGrp: '',
        dctKey: '',
        dctValNm: '',
        dctVal: '',
        dctDsc: '',
        stcd: '01'
      }
      this.dialogVisible = true
    },
    editItem(item) {
      this.form = { ...item }
      this.dialogVisible = true
    },
    saveItem() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          // TODO: 调用后端API保存字典项
          this.dialogVisible = false
          this.$message.success('保存成功')
          this.loadItems()
        } else {
          return false
        }
      })
    },
    deleteItem(item) {
      this.$confirm('确定要删除这个字典项吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // TODO: 调用后端API删除字典项
        this.$message.success('删除成功')
        this.loadItems()
      }).catch(() => {
        // 取消删除
      })
    },
    deleteSelected() {
      this.$confirm(`确定要删除选中的${this.selectedItems.length}个字典项吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // TODO: 调用后端API批量删除字典项
        this.$message.success('删除成功')
        this.loadItems()
      }).catch(() => {
        // 取消删除
      })
    },
    refreshTable() {
      this.loadItems()
    },
    handleSeqChange(item) {
      // TODO: 调用后端API更新排序号
    },
    handleItemChange(item) {
      // TODO: 调用后端API更新字典项
    }
  }
}
</script>

<style scoped>
.dict-item-table {
  margin: 20px 0;
}

.toolbar {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.toolbar .el-button {
  margin-right: 10px;
}

.pagination {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  text-align: right;
}
</style>
