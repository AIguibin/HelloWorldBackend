<template>
  <div class="dict-change-apply">
    <el-card shadow="never" class="card-container">
      <template slot="header">
        <div class="card-header">
          <span>字典变更申请</span>
        </div>
      </template>
      
      <el-form :model="form" :rules="rules" ref="form" label-width="120px">
        <!-- 变更类型 -->
        <el-form-item label="变更类型" prop="changeType">
          <el-radio-group v-model="form.changeType" @change="handleChangeTypeChange">
            <el-radio label="ADD">新增</el-radio>
            <el-radio label="MOD">修改</el-radio>
            <el-radio label="DEL">删除</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <!-- 字典类型选择 -->
        <el-form-item label="字典类型" prop="dctTpId" v-if="form.changeType === 'MOD' || form.changeType === 'DEL'">
          <el-select v-model="form.dctTpId" placeholder="请选择字典类型" @change="handleDictTypeChange">
            <el-option 
              v-for="dictType in dictTypeList" 
              :key="dictType.uuid" 
              :label="dictType.dctTpNm" 
              :value="dictType.uuid">
              {{ dictType.dctTp }} - {{ dictType.dctTpNm }}
            </el-option>
          </el-select>
        </el-form-item>
        
        <!-- 字典类型信息 -->
        <!-- 原字典类型信息（修改/删除时显示） -->
        <template v-if="form.changeType === 'MOD'">
          <el-divider content-position="left">原字典类型信息</el-divider>
          <el-form-item label="字典类型编码">
            <el-input v-model="form.oldDctTp" disabled></el-input>
          </el-form-item>
          <el-form-item label="字典类型名称">
            <el-input v-model="form.oldDctTpNm" disabled></el-input>
          </el-form-item>
          
          <el-divider content-position="left">新字典类型信息</el-divider>
        </template>
        
        <el-form-item label="字典类型编码" prop="newDctTp" v-if="form.changeType === 'ADD' || form.changeType === 'MOD'">
          <el-input v-model="form.newDctTp" placeholder="请输入字典类型编码"></el-input>
        </el-form-item>
        
        <el-form-item label="字典类型名称" prop="newDctTpNm" v-if="form.changeType === 'ADD' || form.changeType === 'MOD'">
          <el-input v-model="form.newDctTpNm" placeholder="请输入字典类型名称"></el-input>
        </el-form-item>
        
        <!-- 变更原因 -->
        <el-form-item label="变更原因" prop="changeReason">
          <el-input 
            v-model="form.changeReason" 
            type="textarea" 
            placeholder="请输入变更原因" 
            :rows="3"
          ></el-input>
        </el-form-item>
        
        <!-- 变更影响 -->
        <el-form-item label="变更影响">
          <el-input 
            v-model="form.changeImpact" 
            type="textarea" 
            placeholder="请输入变更影响分析" 
            :rows="3"
          ></el-input>
        </el-form-item>
        
        <!-- 字典项变更 -->
        <el-form-item v-if="form.changeType === 'ADD' || form.changeType === 'MOD'">
          <template slot="label">
            <span>字典项变更</span>
            <el-button type="primary" size="small" @click="addDictItem" style="margin-left: 10px">
              添加字典项
            </el-button>
          </template>
          
          <div v-if="form.itemChanges.length === 0" class="empty-tip">
            暂无字典项变更
          </div>
          
          <div v-else class="dict-item-list">
            <div 
              v-for="(item, index) in form.itemChanges" 
              :key="index"
              class="dict-item-item"
            >
              <el-divider :content-position="'left'">{{ index + 1 }}. 字典项</el-divider>
              
              <el-form :model="item" :rules="itemRules" class="item-form">
                <el-form-item label="操作类型" prop="changeOperation">
                  <el-select v-model="item.changeOperation" placeholder="请选择操作类型">
                    <el-option label="新增" value="ADD"></el-option>
                    <el-option label="修改" value="MOD"></el-option>
                    <el-option label="删除" value="DEL"></el-option>
                  </el-select>
                </el-form-item>
                
                <!-- 原字典项信息（修改/删除时显示） -->
                <template v-if="item.changeOperation === 'MOD'">
                  <el-divider content-position="left">原字典项信息</el-divider>
                  <el-form-item label="排序号">
                    <el-input-number 
                      v-model="item.oldDctSeq" 
                      :min="1" 
                      :max="9999"
                      disabled
                      style="width: 100%"
                    ></el-input-number>
                  </el-form-item>
                  <el-form-item label="字典键">
                    <el-input v-model="item.oldDctKey" disabled></el-input>
                  </el-form-item>
                  <el-form-item label="字典值名称">
                    <el-input v-model="item.oldDctValNm" disabled></el-input>
                  </el-form-item>
                  <el-form-item label="字典值">
                    <el-input v-model="item.oldDctVal" disabled></el-input>
                  </el-form-item>
                  <el-form-item label="字典组">
                    <el-input v-model="item.oldDctGrp" disabled></el-input>
                  </el-form-item>
                  <el-form-item label="字典描述">
                    <el-input 
                      v-model="item.oldDctDsc" 
                      type="textarea" 
                      disabled
                      :rows="2"
                    ></el-input>
                  </el-form-item>
                  
                  <el-divider content-position="left">新字典项信息</el-divider>
                </template>
                
                <el-form-item label="排序号" prop="newDctSeq" v-if="item.changeOperation === 'ADD' || item.changeOperation === 'MOD'">
                  <el-input-number 
                    v-model="item.newDctSeq" 
                    :min="1" 
                    :max="9999"
                    placeholder="请输入排序号"
                    style="width: 100%"
                  ></el-input-number>
                </el-form-item>
                
                <el-form-item label="字典键" prop="newDctKey" v-if="item.changeOperation === 'ADD' || item.changeOperation === 'MOD'">
                  <el-input v-model="item.newDctKey" placeholder="请输入字典键"></el-input>
                </el-form-item>
                
                <el-form-item label="字典值名称" prop="newDctValNm" v-if="item.changeOperation === 'ADD' || item.changeOperation === 'MOD'">
                  <el-input v-model="item.newDctValNm" placeholder="请输入字典值名称"></el-input>
                </el-form-item>
                
                <el-form-item label="字典值" prop="newDctVal" v-if="item.changeOperation === 'ADD' || item.changeOperation === 'MOD'">
                  <el-input v-model="item.newDctVal" placeholder="请输入字典值"></el-input>
                </el-form-item>
                
                <el-form-item label="字典组" v-if="item.changeOperation === 'ADD' || item.changeOperation === 'MOD'">
                  <el-input v-model="item.newDctGrp" placeholder="请输入字典组"></el-input>
                </el-form-item>
                
                <el-form-item label="字典描述" v-if="item.changeOperation === 'ADD' || item.changeOperation === 'MOD'">
                  <el-input 
                    v-model="item.newDctDsc" 
                    type="textarea" 
                    placeholder="请输入字典描述" 
                    :rows="2"
                  ></el-input>
                </el-form-item>
                
                <el-form-item>
                  <el-button type="danger" size="small" @click="removeDictItem(index)">
                    删除
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </el-form-item>
        
        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="submitForm">提交申请</el-button>
          <el-button @click="saveDraft">保存草稿</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { applyChange, saveDraft, getDictTypes, loadDictType, getChangeDetail } from '@/api'

export default {
  name: 'DictChangeApply',
  data() {
    return {
      form: {
        changeType: 'ADD',
        dctTpId: '',
        oldDctTp: '',
        oldDctTpNm: '',
        newDctTp: '',
        newDctTpNm: '',
        changeReason: '',
        changeImpact: '',
        itemChanges: []
      },
      dictTypeList: [],
      loadedDictType: null,
      loadedDictItems: [],
      rules: {
        changeType: [
          { required: true, message: '请选择变更类型', trigger: 'change' }
        ],
        newDctTp: [
          { required: true, message: '请输入字典类型编码', trigger: 'blur' }
        ],
        newDctTpNm: [
          { required: true, message: '请输入字典类型名称', trigger: 'blur' }
        ],
        dctTpId: [
          { required: true, message: '请选择字典类型', trigger: 'blur' }
        ],
        changeReason: [
          { required: true, message: '请输入变更原因', trigger: 'blur' }
        ]
      },
      itemRules: {
        changeOperation: [
          { required: true, message: '请选择操作类型', trigger: 'change' }
        ],
        newDctSeq: [
          { required: true, message: '请输入排序号', trigger: 'blur' },
          { type: 'number', min: 1, message: '排序号必须大于0', trigger: 'blur' }
        ],
        newDctKey: [
          { required: true, message: '请输入字典键', trigger: 'blur' }
        ],
        newDctValNm: [
          { required: true, message: '请输入字典值名称', trigger: 'blur' }
        ],
        newDctVal: [
          { required: true, message: '请输入字典值', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    // 加载字典类型列表
    this.loadDictTypes()
    
    // 检查URL参数，如存在变更ID则加载数据
    const changeId = this.$route.query.changeId
    if (changeId) {
      this.loadChangeDetail(changeId)
    }
  },
  methods: {
    // 加载字典类型列表
    loadDictTypes() {
      getDictTypes().then(response => {
        this.dictTypeList = response.data
      }).catch(error => {
        this.$message.error('加载字典类型失败: ' + error.message)
      })
    },
    
    // 加载变更详情
    loadChangeDetail(changeId) {
      getChangeDetail(changeId).then(response => {
        const detail = response.data
        // 填充表单数据
        this.form.changeType = detail.changeType
        this.form.dctTpId = detail.dctTpId
        this.form.oldDctTp = detail.oldDctTp
        this.form.oldDctTpNm = detail.oldDctTpNm
        this.form.newDctTp = detail.newDctTp
        this.form.newDctTpNm = detail.newDctTpNm
        this.form.changeReason = detail.changeReason
        this.form.changeImpact = detail.changeImpact
        this.form.itemChanges = detail.itemChanges
      }).catch(error => {
        this.$message.error('加载变更详情失败: ' + error.message)
      })
    },
    
    // 字典类型变化时加载数据
    handleDictTypeChange(dictTypeId) {
      if (dictTypeId) {
        loadDictType(dictTypeId).then(response => {
          this.loadedDictType = response.data.dictType
          this.loadedDictItems = response.data.dictItems
          // 自动填充表单数据
          this.fillFormData()
        }).catch(error => {
          this.$message.error('加载字典类型数据失败: ' + error.message)
        })
      }
    },
    
    // 填充表单数据
    fillFormData() {
      if (this.form.changeType === 'MOD') {
        // 填充字典类型信息
        this.form.oldDctTp = this.loadedDictType.dctTp
        this.form.newDctTp = this.loadedDictType.dctTp
        this.form.oldDctTpNm = this.loadedDictType.dctTpNm
        this.form.newDctTpNm = this.loadedDictType.dctTpNm
        
        // 填充字典项
        this.form.itemChanges = this.loadedDictItems.map(item => ({
          changeOperation: 'MOD',
          oldDctSeq: item.dctSeq,
          newDctSeq: item.dctSeq,
          oldDctKey: item.dctKey,
          newDctKey: item.dctKey,
          oldDctValNm: item.dctValNm,
          newDctValNm: item.dctValNm,
          oldDctVal: item.dctVal,
          newDctVal: item.dctVal,
          oldDctGrp: item.dctGrp,
          newDctGrp: item.dctGrp,
          oldDctDsc: item.dctDsc,
          newDctDsc: item.dctDsc
        }))
      } else if (this.form.changeType === 'DEL') {
        // 填充字典类型信息，仅用于展示
        this.form.oldDctTp = this.loadedDictType.dctTp
        this.form.oldDctTpNm = this.loadedDictType.dctTpNm
      }
    },
    
    // 处理变更类型变更
    handleChangeTypeChange() {
      // 重置表单
      this.form.dctTpId = ''
      this.form.oldDctTp = ''
      this.form.oldDctTpNm = ''
      this.form.newDctTp = ''
      this.form.newDctTpNm = ''
      this.form.itemChanges = []
      this.loadedDictType = null
      this.loadedDictItems = []
    },
    
    // 添加字典项
    addDictItem() {
      // 自动生成排序号：当前最大排序号 + 1，如果没有则从1开始
      const maxSeq = this.form.itemChanges.length > 0 
        ? Math.max(...this.form.itemChanges.map(item => item.newDctSeq || 0))
        : 0
      this.form.itemChanges.push({
        changeOperation: 'ADD',
        oldDctSeq: null,
        newDctSeq: maxSeq + 1,
        oldDctKey: '',
        newDctKey: '',
        oldDctValNm: '',
        newDctValNm: '',
        oldDctVal: '',
        newDctVal: '',
        oldDctGrp: '',
        newDctGrp: '',
        oldDctDsc: '',
        newDctDsc: ''
      })
    },
    
    // 删除字典项
    removeDictItem(index) {
      this.form.itemChanges.splice(index, 1)
    },
    
    // 提交申请
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          // 验证字典项
          let itemValid = true
          this.form.itemChanges.forEach((item, index) => {
            if (item.changeOperation === 'ADD' || item.changeOperation === 'MOD') {
              if (!item.newDctSeq || !item.newDctKey || !item.newDctValNm || !item.newDctVal) {
                itemValid = false
              }
            }
          })
          
          if (!itemValid) {
            this.$message.error('请完善字典项信息')
            return
          }
          
          // 构建提交数据
          const submitData = {
            changeType: this.form.changeType,
            dictTypeId: this.form.dctTpId,
            typeChange: {
              newDctTp: this.form.newDctTp,
              newDctTpNm: this.form.newDctTpNm
            },
            itemChanges: this.form.itemChanges.map(item => ({
              changeOperation: item.changeOperation,
              newData: {
                dctSeq: item.newDctSeq,
                dctKey: item.newDctKey,
                dctValNm: item.newDctValNm,
                dctVal: item.newDctVal,
                dctGrp: item.newDctGrp,
                dctDsc: item.newDctDsc
              }
            })),
            changeReason: this.form.changeReason,
            changeImpact: this.form.changeImpact
          }
          
          // 提交API
          applyChange(submitData)
            .then(response => {
              this.$message.success('提交成功')
              this.resetForm()
            })
            .catch(error => {
              this.$message.error('提交失败: ' + error.message)
            })
        } else {
          return false
        }
      })
    },
    
    // 保存草稿
    saveDraft() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          // 构建提交数据
          const submitData = {
            changeType: this.form.changeType,
            dictTypeId: this.form.dctTpId,
            typeChange: {
              newDctTp: this.form.newDctTp,
              newDctTpNm: this.form.newDctTpNm
            },
            itemChanges: this.form.itemChanges.map(item => ({
              changeOperation: item.changeOperation,
              newData: {
                dctSeq: item.newDctSeq,
                dctKey: item.newDctKey,
                dctValNm: item.newDctValNm,
                dctVal: item.newDctVal,
                dctGrp: item.newDctGrp,
                dctDsc: item.newDctDsc
              }
            })),
            changeReason: this.form.changeReason,
            changeImpact: this.form.changeImpact
          }
          
          // 保存草稿API
          saveDraft(submitData)
            .then(response => {
              this.$message.success('保存成功')
            })
            .catch(error => {
              this.$message.error('保存失败: ' + error.message)
            })
        } else {
          return false
        }
      })
    },
    
    // 重置表单
    resetForm() {
      this.$refs.form.resetFields()
      this.form.changeType = 'ADD'
      this.form.itemChanges = []
    }
  }
}
</script>

<style scoped>
.dict-change-apply {
  padding: 20px;
}

.card-container {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-tip {
  padding: 20px;
  text-align: center;
  color: #999;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.dict-item-list {
  margin-top: 10px;
}

.dict-item-item {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: #f9f9f9;
}

.item-form {
  margin-top: 10px;
}
</style>