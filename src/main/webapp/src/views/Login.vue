<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">架构管理系统登录</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px" class="login-form">
        <!-- 第一步：输入账号密码 -->
        <el-form-item label="账号" prop="userNum" v-if="loginStep === 0">
          <el-input 
            v-model="form.userNum" 
            autocomplete="username"
            prefix-icon="el-icon-user"
            placeholder="请输入账号"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="loginStep === 0">
          <el-input 
            v-model="form.password" 
            type="password" 
            autocomplete="current-password"
            prefix-icon="el-icon-lock"
            placeholder="请输入密码"
            @keyup.enter.native="onSubmit"
          />
        </el-form-item>
        
        <!-- 第二步：选择机构 -->
        <el-form-item label="请选择机构" prop="selectedOrg" v-if="loginStep === 1">
          <el-radio-group v-model="selectedOrg">
            <el-radio 
              v-for="org in loginData.orgDeptList" 
              :key="org.orgCode" 
              :label="org" 
              class="org-radio"
            >
              {{ org.orgName }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        
        <!-- 第三步：选择角色 -->
        <el-form-item label="请选择角色" prop="selectedRole" v-if="loginStep === 2">
          <div class="role-info" v-if="selectedOrg">
            <div class="dept-info">部门：{{ selectedOrg.deptList && selectedOrg.deptList[0] && selectedOrg.deptList[0].deptName ? selectedOrg.deptList[0].deptName : '无' }}</div>
          </div>
          <el-radio-group v-model="selectedRole" class="role-radio-group">
            <el-radio 
              v-for="role in availableRoles" 
              :key="role.roleCode" 
              :label="role" 
              class="role-radio"
            >
              <div class="role-name">{{ role.roleName }}</div>
              <div class="role-tag" v-if="role.isPrimary === 1">(主角色)</div>
            </el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            @click="onSubmit" 
            :loading="loading"
            class="login-button"
          >
            {{ buttonText }}
          </el-button>
          <el-button 
            type="text" 
            @click="onBack" 
            :disabled="loading || loginStep === 0" 
            class="back-button"
          >
            返回上一步
          </el-button>
          <el-button 
            type="text" 
            @click="onForgotPassword" 
            :disabled="loading" 
            class="forgot-password-button" 
            v-if="loginStep === 0"
          >
            忘记密码
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { login } from '../api';
import { Message } from 'element-ui';

export default {
  name: 'Login',
  data() {
    return {
      // 登录表单数据
      form: { userNum: '', password: '' },
      // 加载状态
      loading: false,
      // 表单验证规则
      rules: {
        userNum: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      // 登录步骤：0-输入账号密码，1-选择机构，2-选择角色
      loginStep: 0,
      // 登录返回数据
      loginData: {},
      // 选中的机构
      selectedOrg: null,
      // 选中的角色
      selectedRole: null
    };
  },
  computed: {
    // 动态按钮文本
    buttonText() {
      switch (this.loginStep) {
        case 0:
          return '登录';
        case 1:
          return '下一步';
        case 2:
          return '进入系统';
        default:
          return '登录';
      }
    },
    // 当前机构下可用的角色
    availableRoles() {
      if (!this.selectedOrg || !this.selectedOrg.roleList) {
        return [];
      }
      return this.selectedOrg.roleList;
    }
  },
  methods: {
    onSubmit() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) return;
        
        this.loading = true;
        try {
          if (this.loginStep === 0) {
            // 第一步：提交账号密码登录
            const data = await login(this.form.userNum, this.form.password);
            console.log('登录成功:', data);
            this.loginData = data;
            
            // 如果只有一个机构，直接选择该机构
            if (data.orgDeptList && data.orgDeptList.length === 1) {
              this.selectedOrg = data.orgDeptList[0];
              this.loginStep = 2;
            } else {
              // 否则进入机构选择步骤
              this.loginStep = 1;
            }
          } else if (this.loginStep === 1) {
            // 第二步：验证机构选择，进入角色选择
            if (!this.selectedOrg) {
              Message.error('请选择机构');
              return;
            }
            this.loginStep = 2;
          } else if (this.loginStep === 2) {
            // 第三步：验证角色选择，完成登录
            if (!this.selectedRole) {
              Message.error('请选择角色');
              return;
            }
            
            // 保存登录信息到本地存储
            localStorage.setItem('token', this.loginData.token);
            localStorage.setItem('csrfToken', this.loginData.csrfToken);
            
            // 获取部门信息，使用传统条件判断替代可选链
            let deptCode = this.loginData.deptCode;
            let deptName = this.loginData.deptName;
            if (this.selectedOrg.deptList && this.selectedOrg.deptList[0]) {
              deptCode = this.selectedOrg.deptList[0].deptCode;
              deptName = this.selectedOrg.deptList[0].deptName;
            }
            
            localStorage.setItem('user', JSON.stringify({
              userName: this.loginData.userName,
              userNum: this.loginData.userNum,
              orgCode: this.selectedOrg.orgCode,
              orgName: this.selectedOrg.orgName,
              deptCode: deptCode,
              deptName: deptName,
              roleCode: this.selectedRole.roleCode,
              roleName: this.selectedRole.roleName
            }));
            
            // 跳转到首页
            this.$router.replace('/dashboard');
          }
        } catch (e) {
          console.error(e);
        } finally {
          this.loading = false;
        }
      });
    },
    // 返回上一步
    onBack() {
      if (this.loginStep > 0) {
        this.loginStep--;
      }
    },
    onForgotPassword() {
      Message.info('请先登录后再修改密码');
    }
  }
};
</script>

<style scoped>
.login-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 25%, #f093fb 50%, #4facfe 75%, #00f2fe 100%);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
  overflow: hidden;
}

@keyframes gradientShift {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

.login-box {
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 28px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 1px;
}

.login-form {
  margin-top: 20px;
}

.login-form ::v-deep .el-form-item__label {
  color: #333;
  font-weight: 500;
}

.login-form ::v-deep .el-input__inner {
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  transition: all 0.3s ease;
}

.login-form ::v-deep .el-input__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.login-button:active {
  transform: translateY(0);
}

.forgot-password-button {
  width: 100%;
  margin-top: 10px;
  color: #667eea;
  font-size: 14px;
}

.forgot-password-button:hover {
  color: #764ba2;
}

.back-button {
  width: 100%;
  margin-top: 10px;
  color: #667eea;
  font-size: 14px;
}

.back-button:hover {
  color: #764ba2;
}

.org-radio {
  display: block;
  margin-bottom: 12px;
  padding: 10px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.org-radio:hover {
  background-color: rgba(102, 126, 234, 0.1);
}

.role-info {
  margin-bottom: 16px;
  padding: 12px;
  background-color: rgba(102, 126, 234, 0.1);
  border-radius: 8px;
  font-size: 14px;
}

.dept-info {
  margin-bottom: 8px;
  color: #666;
}

.role-radio-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.role-radio {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  background-color: #f9f9f9;
}

.role-radio:hover {
  background-color: rgba(102, 126, 234, 0.1);
}

.role-name {
  margin-right: 10px;
  font-weight: 500;
}

.role-tag {
  font-size: 12px;
  color: #764ba2;
  background-color: rgba(118, 75, 162, 0.1);
  padding: 2px 8px;
  border-radius: 4px;
}
</style>