<template>
  <div class="change-diff-viewer">
    <!-- 变更类型标签 -->
    <div class="change-type-header" v-if="changeType">
      <div class="change-type-badge">
        <el-tag :type="getChangeTypeTagType(changeType)" class="change-type-tag">
          <i :class="getChangeTypeIcon(changeType)"></i>
          {{ getChangeTypeText(changeType) }}
        </el-tag>
      </div>
    </div>

    <!-- 变更对比表格 -->
    <div class="diff-table-container">
      <el-table :data="diffData" style="width: 100%" border class="diff-table">
        <el-table-column prop="field" label="字段" width="140">
          <template slot-scope="scope">
            <span class="field-name">{{ getFieldName(scope.row.field) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="oldValue" label="变更前" min-width="200">
          <template slot-scope="scope">
            <div class="value old-value" :class="{ 'changed': scope.row.changed }">
              {{ scope.row.oldValue || '-' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="newValue" label="变更后" min-width="200">
          <template slot-scope="scope">
            <div class="value new-value" :class="{ 'changed': scope.row.changed }">
              {{ scope.row.newValue || '-' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="changeType" label="变更状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.changed" type="warning" size="small" class="change-tag">
              <i class="el-icon-edit"></i>
              变更
            </el-tag>
            <span v-else class="no-change">-</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 字典项变更列表 -->
    <div class="item-changes-section" v-if="itemChanges && itemChanges.length > 0">
      <div class="section-header">
        <h4 class="section-title">
          <i class="el-icon-document"></i>
          字典项变更
        </h4>
        <span class="item-count">{{ itemChanges.length }} 项</span>
      </div>
      <el-collapse class="item-collapse" accordion>
        <el-collapse-item v-for="(item, index) in itemChanges" :key="item.id || index" :name="index">
          <template slot="title">
            <div class="collapse-title">
              <el-tag :type="getChangeTypeTagType(item.changeOperation)" size="small" class="operation-tag">
                {{ getChangeTypeText(item.changeOperation) }}
              </el-tag>
              <span class="item-key">{{ item.oldDctKey || item.newDctKey || '未知' }}</span>
            </div>
          </template>
          <div class="collapse-content">
            <el-table :data="getItemDiffData(item)" style="width: 100%" border class="item-diff-table">
              <el-table-column prop="field" label="字段" width="140">
                <template slot-scope="scope">
                  <span class="field-name">{{ getFieldName(scope.row.field) }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="oldValue" label="变更前" min-width="180">
                <template slot-scope="scope">
                  <div class="value old-value" :class="{ 'changed': scope.row.changed }">
                    {{ scope.row.oldValue || '-' }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="newValue" label="变更后" min-width="180">
                <template slot-scope="scope">
                  <div class="value new-value" :class="{ 'changed': scope.row.changed }">
                    {{ scope.row.newValue || '-' }}
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChangeDiffViewer',
  props: {
    changeType: {
      type: String,
      default: ''
    },
    oldData: {
      type: Object,
      default: () => ({})
    },
    newData: {
      type: Object,
      default: () => ({})
    },
    itemChanges: {
      type: Array,
      default: () => []
    }
  },
  computed: {
    diffData() {
      return this.calculateDiff()
    }
  },
  methods: {
    calculateDiff() {
      const fields = [
        { key: 'dctTp', label: '字典类型编码' },
        { key: 'dctTpNm', label: '字典类型名称' },
        { key: 'dctTpDsc', label: '字典类型描述' }
      ]

      return fields.map(field => {
        const oldValue = this.oldData[field.key] || ''
        const newValue = this.newData[field.key] || ''
        const changed = oldValue !== newValue

        return {
          field: field.key,
          fieldLabel: field.label,
          oldValue,
          newValue,
          changed
        }
      })
    },
    getFieldName(field) {
      const fieldMap = {
        dctTp: '字典类型编码',
        dctTpNm: '字典类型名称',
        dctTpDsc: '字典类型描述',
        dctSeq: '排序号',
        dctGrp: '分组',
        dctKey: '字典键',
        dctValNm: '字典值名称',
        dctVal: '字典值',
        dctDsc: '描述',
        stcd: '状态'
      }
      return fieldMap[field] || field
    },
    getChangeTypeText(type) {
      const typeMap = {
        ADD: '新增',
        MOD: '修改',
        DEL: '删除'
      }
      return typeMap[type] || type
    },
    getChangeTypeTagType(type) {
      const typeMap = {
        ADD: 'success',
        MOD: 'warning',
        DEL: 'danger'
      }
      return typeMap[type] || 'info'
    },
    getChangeTypeIcon(type) {
      const iconMap = {
        ADD: 'el-icon-plus',
        MOD: 'el-icon-edit',
        DEL: 'el-icon-delete'
      }
      return iconMap[type] || 'el-icon-info'
    },
    getItemChangeTitle(item) {
      const operation = item.changeOperation || 'MOD'
      const key = item.oldDctKey || item.newDctKey || '未知'
      return `${this.getChangeTypeText(operation)} - ${key}`
    },
    getItemDiffData(item) {
      const fields = [
        { key: 'dctSeq', label: '排序号' },
        { key: 'dctGrp', label: '分组' },
        { key: 'dctKey', label: '字典键' },
        { key: 'dctValNm', label: '字典值名称' },
        { key: 'dctVal', label: '字典值' },
        { key: 'dctDsc', label: '描述' },
        { key: 'stcd', label: '状态' }
      ]

      return fields.map(field => {
        const oldValue = item[`old${field.key.charAt(0).toUpperCase() + field.key.slice(1)}`] || ''
        const newValue = item[`new${field.key.charAt(0).toUpperCase() + field.key.slice(1)}`] || ''
        const changed = oldValue !== newValue

        return {
          field: field.key,
          fieldLabel: field.label,
          oldValue,
          newValue,
          changed
        }
      }).filter(item => item.changed)
    }
  }
}
</script>

<style scoped>
.change-diff-viewer {
  width: 100%;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eaf6 100%);
  border-radius: 12px;
  min-height: 400px;
}

/* Change type header */
.change-type-header {
  margin-bottom: 24px;
  display: flex;
  justify-content: center;
}

.change-type-badge {
  display: inline-block;
}

.change-type-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  font-weight: 600;
  padding: 8px 20px;
  border-radius: 6px;
  border: none;
}

.change-type-tag.el-tag--success {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: #ffffff;
}

.change-type-tag.el-tag--warning {
  background: linear-gradient(135deg, #ff9800 0%, #ff6b00 100%);
  color: #ffffff;
}

.change-type-tag.el-tag--danger {
  background: linear-gradient(135deg, #f56c6c 0%, #ff6b6b 100%);
  color: #ffffff;
}

.change-type-tag.el-tag--info {
  background: linear-gradient(135deg, #909399 0%, #606266 100%);
  color: #ffffff;
}

/* Diff table container */
.diff-table-container {
  background: #ffffff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(123, 104, 238, 0.06);
  margin-bottom: 24px;
}

.diff-table {
  border-radius: 8px;
  overflow: hidden;
}

.diff-table ::v-deep .el-table__header-wrapper {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
}

.diff-table ::v-deep .el-table__header th {
  background: transparent;
  color: #ffffff;
  font-weight: 600;
  font-size: 14px;
  border-color: rgba(255, 255, 255, 0.2);
  padding: 14px 12px;
}

.diff-table ::v-deep .el-table__body tr {
  transition: all 0.2s ease;
}

.diff-table ::v-deep .el-table__body tr:hover > td {
  background: linear-gradient(135deg, #fafbff 0%, #f5f7ff 100%);
}

.diff-table ::v-deep .el-table__body td {
  padding: 12px;
  border-color: #e8e8f0;
  color: #1a1a2e;
  font-size: 14px;
}

.diff-table ::v-deep .el-table__body tr:nth-child(even) {
  background: #fafbff;
}

.diff-table ::v-deep .el-table__body tr:nth-child(odd) {
  background: #ffffff;
}

/* Field name styling */
.field-name {
  font-weight: 600;
  color: #5a4fcf;
  font-size: 14px;
}

/* Value styling */
.value {
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 13px;
  line-height: 1.6;
  word-break: break-word;
  transition: all 0.2s ease;
}

.old-value {
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eaf6 100%);
  color: #606266;
  border: 1px solid #e4e7ed;
}

.new-value {
  background: linear-gradient(135deg, #ecf5ff 0%, #d9ecff 100%);
  color: #409eff;
  border: 1px solid #d9ecff;
}

.value.changed {
  border: 2px solid #ff9800;
  background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
  color: #ff6b00;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(255, 152, 0, 0.15);
}

/* Change tag */
.change-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 10px;
  border: none;
}

.no-change {
  color: #c0c4cc;
  font-size: 13px;
}

/* Item changes section */
.item-changes-section {
  background: #ffffff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(123, 104, 238, 0.06);
  padding: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #e8e8f0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
}

.section-title i {
  font-size: 18px;
  color: #7B68EE;
}

.item-count {
  font-size: 13px;
  color: #909399;
  font-weight: 500;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eaf6 100%);
  padding: 4px 12px;
  border-radius: 12px;
}

/* Collapse styling */
.item-collapse {
  border: none;
}

.item-collapse ::v-deep .el-collapse-item__header {
  background: #fafbff;
  border: 1px solid #e8e8f0;
  border-radius: 6px;
  margin-bottom: 12px;
  padding: 12px 16px;
  transition: all 0.3s ease;
}

.item-collapse ::v-deep .el-collapse-item__header:hover {
  background: linear-gradient(135deg, #f5f7ff 0%, #e8eaf6 100%);
  border-color: #9370DB;
}

.item-collapse ::v-deep .el-collapse-item__arrow {
  color: #7B68EE;
  transition: transform 0.3s ease;
}

.item-collapse ::v-deep .el-collapse-item.is-active .el-collapse-item__arrow {
  transform: rotate(90deg);
}

.collapse-title {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.operation-tag {
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 10px;
  border: none;
}

.item-key {
  font-size: 14px;
  font-weight: 500;
  color: #1a1a2e;
}

.collapse-content {
  padding: 16px 0;
}

/* Item diff table */
.item-diff-table {
  border-radius: 6px;
  overflow: hidden;
}

.item-diff-table ::v-deep .el-table__header-wrapper {
  background: linear-gradient(135deg, #9370DB 0%, #BA55D3 100%);
}

.item-diff-table ::v-deep .el-table__header th {
  background: transparent;
  color: #ffffff;
  font-weight: 600;
  font-size: 13px;
  border-color: rgba(255, 255, 255, 0.2);
  padding: 12px 10px;
}

.item-diff-table ::v-deep .el-table__body tr {
  transition: all 0.2s ease;
}

.item-diff-table ::v-deep .el-table__body tr:hover > td {
  background: linear-gradient(135deg, #fafbff 0%, #f5f7ff 100%);
}

.item-diff-table ::v-deep .el-table__body td {
  padding: 10px;
  border-color: #e8e8f0;
  color: #1a1a2e;
  font-size: 13px;
}

.item-diff-table ::v-deep .el-table__body tr:nth-child(even) {
  background: #fafbff;
}

.item-diff-table ::v-deep .el-table__body tr:nth-child(odd) {
  background: #ffffff;
}

/* Responsive design */
@media (max-width: 1200px) {
  .change-diff-viewer {
    padding: 16px;
  }

  .item-changes-section {
    padding: 16px;
  }

  .diff-table ::v-deep .el-table__header th,
  .item-diff-table ::v-deep .el-table__header th {
    font-size: 13px;
    padding: 12px 10px;
  }

  .diff-table ::v-deep .el-table__body td,
  .item-diff-table ::v-deep .el-table__body td {
    font-size: 13px;
    padding: 10px;
  }
}

@media (max-width: 768px) {
  .change-diff-viewer {
    padding: 12px;
  }

  .item-changes-section {
    padding: 12px;
  }

  .change-type-tag {
    font-size: 14px;
    padding: 6px 16px;
  }

  .section-title {
    font-size: 15px;
  }

  .section-title i {
    font-size: 16px;
  }

  .item-count {
    font-size: 12px;
    padding: 3px 10px;
  }

  .diff-table ::v-deep .el-table__header th,
  .item-diff-table ::v-deep .el-table__header th {
    font-size: 12px;
    padding: 10px 8px;
  }

  .diff-table ::v-deep .el-table__body td,
  .item-diff-table ::v-deep .el-table__body td {
    font-size: 12px;
    padding: 8px;
  }

  .value {
    font-size: 12px;
    padding: 6px 10px;
  }

  .field-name {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .change-diff-viewer {
    padding: 8px;
  }

  .item-changes-section {
    padding: 8px;
  }

  .change-type-tag {
    font-size: 13px;
    padding: 5px 12px;
  }

  .section-title {
    font-size: 14px;
  }

  .section-title i {
    font-size: 15px;
  }

  .item-count {
    font-size: 11px;
    padding: 2px 8px;
  }

  .diff-table ::v-deep .el-table__header th,
  .item-diff-table ::v-deep .el-table__header th {
    font-size: 11px;
    padding: 8px 6px;
  }

  .diff-table ::v-deep .el-table__body td,
  .item-diff-table ::v-deep .el-table__body td {
    font-size: 11px;
    padding: 6px;
  }

  .value {
    font-size: 11px;
    padding: 5px 8px;
  }

  .field-name {
    font-size: 12px;
  }

  .collapse-title {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
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

.diff-table-container,
.item-changes-section {
  animation: fadeIn 0.4s ease-out;
}

/* Scrollbar styling */
.diff-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar,
.item-diff-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.diff-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar-track,
.item-diff-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar-track {
  background: #f5f7fa;
  border-radius: 4px;
}

.diff-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar-thumb,
.item-diff-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  border-radius: 4px;
}

.diff-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar-thumb:hover,
.item-diff-table ::v-deep .el-table__body-wrapper::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #9370DB 0%, #BA55D3 100%);
}
</style>
