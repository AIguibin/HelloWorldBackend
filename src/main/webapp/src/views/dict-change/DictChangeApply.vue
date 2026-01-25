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
        
        <!-- 字典类型信息 -->
        <el-form-item label="字典类型编码" prop="newDctTp" v-if="form.changeType === 'ADD' || form.changeType === 'MOD'">
          <el-input v-model="form.newDctTp" placeholder="请输入字典类型编码"></el-input>
        </el-form-item>
        
        <el-form-item label="字典类型名称" prop="newDctTpNm" v-if="form.changeType === 'ADD' || form.changeType === 'MOD'">
          <el-input v-model="form.newDctTpNm" placeholder="请输入字典类型名称"></el-input>
        </el-form-item>
        
        <el-form-item label="字典类型ID" prop="dctTpId" v-if="form.changeType === 'MOD' || form.changeType === 'DEL'">
          <el-input v-model="form.dctTpId" placeholder="请输入字典类型ID"></el-input>
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
                
                <el-form-item label="字典描述">
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
import { applyChange, saveDraft } from '@/api'

export default {
  name: 'DictChangeApply',
  data() {
    return {
      form: {
        changeType: 'ADD',
        dctTpId: '',
        newDctTp: '',
        newDctTpNm: '',
        changeReason: '',
        changeImpact: '',
        itemChanges: []
      },
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
          { required: true, message: '请输入字典类型ID', trigger: 'blur' }
        ],
        changeReason: [
          { required: true, message: '请输入变更原因', trigger: 'blur' }
        ]
      },
      itemRules: {
        changeOperation: [
          { required: true, message: '请选择操作类型', trigger: 'change' }
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
  methods: {
    // 处理变更类型变更
    handleChangeTypeChange() {
      // 重置表单
      this.form.dctTpId = ''
      this.form.newDctTp = ''
      this.form.newDctTpNm = ''
      this.form.itemChanges = []
    },
    
    // 添加字典项
    addDictItem() {
      this.form.itemChanges.push({
        changeOperation: 'ADD',
        newDctKey: '',
        newDctValNm: '',
        newDctVal: '',
        newDctGrp: '',
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
              if (!item.newDctKey || !item.newDctValNm || !item.newDctVal) {
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