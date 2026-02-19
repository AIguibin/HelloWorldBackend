<template>
  <div class="dict-item-table">
    <!-- 表格工具栏 -->
    <div class="toolbar">
      <el-button type="primary" @click="addItem" size="small" class="action-btn add-btn">
        <i class="el-icon-plus"></i>
        新增
      </el-button>
      <el-button type="danger" @click="deleteSelected" size="small" :disabled="!hasSelection" class="action-btn delete-btn">
        <i class="el-icon-delete"></i>
        删除
      </el-button>
      <el-button type="warning" @click="refreshTable" size="small" class="action-btn refresh-btn">
        <i class="el-icon-refresh"></i>
        刷新
      </el-button>
    </div>

    <!-- 字典项表格 -->
    <el-table
      v-loading="loading"
      :data="itemList"
      @selection-change="handleSelectionChange"
      style="width: 100%"
      border
      class="data-table"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="dctSeq" label="排序号" width="80">
        <template slot-scope="scope">
          <el-input-number
            v-model="scope.row.dctSeq"
            :min="1"
            size="small"
            @change="handleSeqChange(scope.row)"
            class="table-input-number"
          />
        </template>
      </el-table-column>
      <el-table-column prop="dctGrp" label="分组" width="120">
        <template slot-scope="scope">
          <el-input
            v-model="scope.row.dctGrp"
            size="small"
            @change="handleItemChange(scope.row)"
            class="table-input"
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
            class="table-input"
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
            class="table-input"
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
            class="table-input"
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
            class="table-textarea"
          />
        </template>
      </el-table-column>
      <el-table-column prop="stcd" label="状态" width="100">
        <template slot-scope="scope">
          <el-select
            v-model="scope.row.stcd"
            size="small"
            @change="handleItemChange(scope.row)"
            class="table-select"
          >
            <el-option label="启用" value="01" />
            <el-option label="禁用" value="02" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="editItem(scope.row)" class="table-btn edit-btn">
            <i class="el-icon-edit"></i>
            编辑
          </el-button>
          <el-button type="danger" size="mini" @click="deleteItem(scope.row)" class="table-btn delete-btn">
            <i class="el-icon-delete"></i>
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
    <el-dialog title="编辑字典项" :visible.sync="dialogVisible" width="600px" class="edit-dialog">
      <el-form :model="form" :rules="rules" ref="form" label-width="80px" class="dialog-form">
        <el-form-item label="排序号" prop="dctSeq">
          <el-input-number v-model="form.dctSeq" :min="1" class="form-input-number" />
        </el-form-item>
        <el-form-item label="分组" prop="dctGrp">
          <el-input v-model="form.dctGrp" class="form-input" />
        </el-form-item>
        <el-form-item label="字典键" prop="dctKey">
          <el-input v-model="form.dctKey" placeholder="请输入字典键" class="form-input" />
        </el-form-item>
        <el-form-item label="字典值名称" prop="dctValNm">
          <el-input v-model="form.dctValNm" placeholder="请输入字典值名称" class="form-input" />
        </el-form-item>
        <el-form-item label="字典值" prop="dctVal">
          <el-input v-model="form.dctVal" placeholder="请输入字典值" class="form-input" />
        </el-form-item>
        <el-form-item label="描述" prop="dctDsc">
          <el-input v-model="form.dctDsc" type="textarea" placeholder="请输入描述" class="form-textarea" />
        </el-form-item>
        <el-form-item label="状态" prop="stcd">
          <el-select v-model="form.stcd" class="form-select">
            <el-option label="启用" value="01" />
            <el-option label="禁用" value="02" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" class="cancel-btn">
          <i class="el-icon-close"></i>
          取消
        </el-button>
        <el-button type="primary" @click="saveItem" class="save-btn">
          <i class="el-icon-check"></i>
          保存
        </el-button>
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
/* Page container */
.dict-item-table {
  width: 100%;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eaf6 100%);
  min-height: calc(100vh - 60px);
}

/* Toolbar styling */
.toolbar {
  margin-bottom: 20px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12px;
  border: 1px solid rgba(123, 104, 238, 0.1);
  box-shadow: 0 2px 8px rgba(123, 104, 238, 0.06);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

/* Action button styling */
.action-btn {
  border-radius: 8px;
  padding: 8px 16px;
  font-weight: 600;
  transition: all 0.3s ease;
  border: none;
  font-size: 14px;
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

.delete-btn {
  background: linear-gradient(135deg, #f56c6c 0%, #ff6b6b 100%);
  color: #fff;
}

.delete-btn:hover {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8a80 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.3);
}

.delete-btn:disabled {
  background: linear-gradient(135deg, #c0c4cc 0%, #909399 100%);
  cursor: not-allowed;
  opacity: 0.5;
}

.refresh-btn {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #fff;
}

.refresh-btn:hover {
  background: linear-gradient(135deg, #66b1ff 0%, #8cc5ff 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
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

/* Table input styling */
.table-input-number ::v-deep .el-input__inner {
  border-radius: 6px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
  font-size: 13px;
  height: 32px;
  line-height: 32px;
}

.table-input-number ::v-deep .el-input__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.table-input-number ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

.table-input ::v-deep .el-input__inner {
  border-radius: 6px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
  font-size: 13px;
  height: 32px;
  line-height: 32px;
  padding: 0 12px;
}

.table-input ::v-deep .el-input__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.table-input ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

.table-textarea ::v-deep .el-textarea__inner {
  border-radius: 6px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
  font-size: 13px;
  padding: 8px 12px;
  line-height: 1.6;
  resize: vertical;
}

.table-textarea ::v-deep .el-textarea__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.table-textarea ::v-deep .el-textarea__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

.table-select ::v-deep .el-input__inner {
  border-radius: 6px;
  border: 1px solid #d4d4e8;
  transition: all 0.3s ease;
  font-size: 13px;
  height: 32px;
  line-height: 32px;
  padding: 0 12px;
}

.table-select ::v-deep .el-input__inner:hover {
  border-color: #9370DB;
  box-shadow: 0 0 0 2px rgba(147, 112, 219, 0.1);
}

.table-select ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 3px rgba(123, 104, 238, 0.15);
}

/* Table button styling */
.table-btn {
  border-radius: 6px;
  padding: 4px 12px;
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

/* Pagination styling */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(123, 104, 238, 0.06);
}

/* Edit dialog styling */
.edit-dialog ::v-deep .el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.edit-dialog ::v-deep .el-dialog__header {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  color: #ffffff;
  padding: 16px 20px;
}

.edit-dialog ::v-deep .el-dialog__title {
  color: #ffffff;
  font-weight: 600;
}

.edit-dialog ::v-deep .el-dialog__body {
  padding: 24px 20px;
  background: #ffffff;
}

.dialog-form ::v-deep .el-form-item__label {
  color: #5a4fcf;
  font-weight: 600;
  font-size: 14px;
}

/* Form input styling */
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
  .dict-item-table {
    padding: 16px;
  }

  .toolbar {
    padding: 14px 16px;
  }
}

@media screen and (max-width: 768px) {
  .dict-item-table {
    padding: 12px;
  }

  .toolbar {
    padding: 12px;
    flex-direction: column;
    gap: 12px;
  }

  .action-btn {
    width: 100%;
    justify-content: center;
  }

  .table-btn {
    padding: 4px 8px;
    font-size: 11px;
  }
}

@media screen and (max-width: 480px) {
  .dict-item-table {
    padding: 8px;
  }

  .toolbar {
    padding: 8px;
  }

  .action-btn {
    font-size: 13px;
    padding: 8px 12px;
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

.toolbar,
.data-table {
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
