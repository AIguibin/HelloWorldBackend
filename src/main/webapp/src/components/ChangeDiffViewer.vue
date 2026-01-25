<template>
  <div class="change-diff-viewer">
    <!-- 变更类型标签 -->
    <div class="change-type" v-if="changeType">
      <el-tag :type="getChangeTypeTagType(changeType)">{{ getChangeTypeText(changeType) }}</el-tag>
    </div>

    <!-- 变更对比表格 -->
    <el-table :data="diffData" style="width: 100%" border>
      <el-table-column prop="field" label="字段" width="120">
        <template slot-scope="scope">
          <span class="field-name">{{ getFieldName(scope.row.field) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="oldValue" label="变更前" width="300">
        <template slot-scope="scope">
          <div class="value old-value" :class="{ 'changed': scope.row.changed }">
            {{ scope.row.oldValue || '-' }}
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="newValue" label="变更后" width="300">
        <template slot-scope="scope">
          <div class="value new-value" :class="{ 'changed': scope.row.changed }">
            {{ scope.row.newValue || '-' }}
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="changeType" label="变更类型" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.changed" type="warning" size="small">变更</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
    </el-table>

    <!-- 字典项变更列表 -->
    <div class="item-changes" v-if="itemChanges && itemChanges.length > 0">
      <h4 class="section-title">字典项变更</h4>
      <el-collapse>
        <el-collapse-item v-for="(item, index) in itemChanges" :key="item.id || index" :title="getItemChangeTitle(item)">
          <el-table :data="getItemDiffData(item)" style="width: 100%" border>
            <el-table-column prop="field" label="字段" width="120">
              <template slot-scope="scope">
                <span class="field-name">{{ getFieldName(scope.row.field) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="oldValue" label="变更前" width="250">
              <template slot-scope="scope">
                <div class="value old-value" :class="{ 'changed': scope.row.changed }">
                  {{ scope.row.oldValue || '-' }}
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="newValue" label="变更后" width="250">
              <template slot-scope="scope">
                <div class="value new-value" :class="{ 'changed': scope.row.changed }">
                  {{ scope.row.newValue || '-' }}
                </div>
              </template>
            </el-table-column>
          </el-table>
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
  margin: 20px 0;
}

.change-type {
  margin-bottom: 15px;
}

.change-type .el-tag {
  font-size: 14px;
  padding: 4px 12px;
}

.section-title {
  margin: 20px 0 15px 0;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.item-changes {
  margin-top: 30px;
}

.value {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 13px;
}

.old-value {
  background-color: #f5f7fa;
}

.new-value {
  background-color: #ecf5ff;
}

.value.changed {
  border: 1px solid #e6a23c;
  background-color: #fdf6ec;
}

.field-name {
  font-weight: 500;
  color: #606266;
}
</style>
