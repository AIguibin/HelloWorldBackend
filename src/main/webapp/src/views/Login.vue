<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">架构管理系统登录</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px" class="login-form">
        <el-form-item label="账号" prop="userNum">
          <el-input 
            v-model="form.userNum" 
            autocomplete="username"
            prefix-icon="el-icon-user"
            placeholder="请输入账号"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            autocomplete="current-password"
            prefix-icon="el-icon-lock"
            placeholder="请输入密码"
            @keyup.enter.native="onSubmit"
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="onSubmit" 
            :loading="loading"
            class="login-button"
          >
            登录
          </el-button>
          <el-button 
            type="text" 
            @click="onForgotPassword" 
            :disabled="loading" 
            class="forgot-password-button"
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
      form: { userNum: '', password: '' },
      loading: false,
      rules: {
        userNum: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    };
  },
  methods: {
    onSubmit() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) return;
        this.loading = true;
        try {
          const data = await login(this.form.userNum, this.form.password);
          console.log(data);
          localStorage.setItem('token', data.token);
          localStorage.setItem('csrfToken', data.csrfToken);
          localStorage.setItem('user', JSON.stringify({ userName: data.userName, userNum: data.userNum, chineseName: data.chineseName }));
          this.$router.replace('/');
        } catch (e) {
            console.error(e);
        } finally {
          this.loading = false;
        }
      });
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
</style>