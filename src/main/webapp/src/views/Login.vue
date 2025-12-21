<template>
  <div class="login-container">
    <!-- 浮动粒子 -->
    <div class="particles">
      <div class="particle"></div>
      <div class="particle"></div>
      <div class="particle"></div>
      <div class="particle"></div>
      <div class="particle"></div>
      <div class="particle"></div>
      <div class="particle"></div>
      <div class="particle"></div>
      <div class="particle"></div>
      <div class="particle"></div>
      <div class="particle"></div>
      <div class="particle"></div>
    </div>
    
    <!-- 主登录容器 -->
    <div class="container">
      <!-- 左侧信息面板 -->
      <div class="left-panel">
        <div class="logo">
          <div class="logo-icon">
            <i class="el-icon-s-grid"></i>
          </div>
          <div class="logo-text">AMS Pro</div>
        </div>
        
        <h1 class="system-name">架构管理系统</h1>
        <p class="system-desc">企业级架构统一管理平台，集成AI智能分析、实时监控、自动化运维等先进功能，助力企业实现数字化转型与智能化升级。</p>
        
        <ul class="feature-list">
          <li><i class="el-icon-shield"></i> 多层安全防护与智能风控</li>
          <li><i class="el-icon-bolt"></i> 高性能分布式架构与弹性伸缩</li>
          <li><i class="el-icon-data-analysis"></i> 实时监控与预测性分析</li>
          <li><i class="el-icon-robot"></i> AI辅助决策与自动化优化</li>
          <li><i class="el-icon-cloud"></i> 多云管理与无缝迁移</li>
        </ul>
      </div>
      
      <!-- 右侧登录面板 -->
      <div class="right-panel">
        <h2 class="login-title">用户登录 <span class="ai-badge"><i class="el-icon-cpu"></i> AI增强版</span></h2>
        <p class="login-subtitle" v-if="loginStep === 0">请输入您的账号和密码访问系统</p>
        <p class="login-subtitle" v-else-if="loginStep === 1">请选择您的机构</p>
        <el-form :model="form" :rules="rules" ref="formRef" label-width="80px" class="login-form">
          <!-- 第一步：输入账号密码 -->
          <el-form-item label="账号" prop="userNum" v-if="loginStep === 0">
            <el-input v-model="form.userNum" autocomplete="username" prefix-icon="el-icon-user" placeholder="请输入账号" />
          </el-form-item>
          <el-form-item label="密码" prop="password" v-if="loginStep === 0">
            <el-input v-model="form.password" type="password" autocomplete="current-password" prefix-icon="el-icon-lock"
              placeholder="请输入密码" @keyup.enter.native="onSubmit" />
          </el-form-item>

          <!-- 第二步：选择机构 -->
          <el-form-item label="机构" prop="selectedOrg" v-if="loginStep === 1">
            <el-select v-model="form.selectedOrg" placeholder="请选择机构" clearable class="org-select">
              <el-option v-for="org in loginData.userAllOrgDeptList" :key="org.orgCode" :label="org.orgName"
                :value="org"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="onSubmit" :loading="loading" class="login-button">
              {{ buttonText }}
            </el-button>
            <div class="links-container">
              <el-button type="text" @click="onBack" :disabled="loading || loginStep === 0" class="link">
                <i class="el-icon-arrow-left"></i> 返回上一步
              </el-button>
              <el-button type="text" @click="onForgotPassword" :disabled="loading" class="link"
                v-if="loginStep === 0">
                <i class="el-icon-key"></i> 忘记密码？
              </el-button>
            </div>
          </el-form-item>
        </el-form>
        <div class="footer">
          <p>© 2024 架构管理系统 v4.0 | 支持AI智能身份验证与生物识别</p>
          <p style="font-size: 12px; margin-top: 8px;">推荐使用 Chrome 90+ / Edge 90+ 浏览器访问</p>
        </div>
      </div>
    </div>
    
    <!-- 浮动提示 -->
    <div class="floating-hint">
      <i class="el-icon-lightbulb"></i>
      <span>提示：默认账号 administrator，密码任意输入即可体验</span>
    </div>
  </div>
</template>

<script>
import { login, checkLogin } from '../api';
import { Message } from 'element-ui';

export default {
  name: 'Login',
  data() {
    return {
      // 登录表单数据
      form: { 
        userNum: '', 
        password: '',
        selectedOrg: null
      },
      // 加载状态
      loading: false,
      // 表单验证规则
      rules: {
        userNum: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        selectedOrg: [{ required: true, message: '请选择机构', trigger: 'change' }]
      },
      // 登录步骤：0-输入账号密码，1-选择机构
      loginStep: 0,
      // 登录返回数据，初始化为包含空数组的对象，避免undefined错误
      loginData: {
        userAllOrgDeptList: []
      }
    };
  },
  computed: {
    // 动态按钮文本
    buttonText() {
      switch (this.loginStep) {
        case 0:
          return '登录';
        case 1:
          return '进入系统';
        default:
          return '登录';
      }
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
            const result = await login(this.form.userNum, this.form.password);
            console.log('验证账户密码登录成功:', result);
            // API直接返回了data内容，而不是{ code, message, data }结构
            this.loginData = result;
            
            // 如果只有一个机构，直接选择该机构
            if (result.userAllOrgDeptList && result.userAllOrgDeptList.length === 1) {
              this.form.selectedOrg = result.userAllOrgDeptList[0];
            }
            // 进入机构选择步骤
            this.loginStep = 1;
          } else if (this.loginStep === 1) {
            // 第二步：验证机构选择，调用check接口完成登录
            if (!this.form.selectedOrg) {
              Message.error('请选择机构');
              return;
            }
            
            // 调用/api/check接口，获取完整的登录信息
            const loginInfo = await checkLogin({
              tempToken: this.loginData.tempToken,
              selectedOrgCode: this.form.selectedOrg.orgCode
            });
            
            console.log('权限获取登录成功，登录信息:', loginInfo);
            
            // 根据src\main\java\com\aiguibin\platform\arch\controller\AuthController.java的接口checkLogin的返回值，组装前端需要的登录信息
            // 保存登录信息和token到localStorage
           
            localStorage.setItem('loginInfo', JSON.stringify(loginInfo));
            localStorage.setItem('token', loginInfo.session.accessToken);
            localStorage.setItem('csrfToken', loginInfo.session.csrfToken);
            
          
            localStorage.setItem('user', JSON.stringify(loginInfo.user));
            localStorage.setItem('session', JSON.stringify(loginInfo.session));
            localStorage.setItem('currentOrg', JSON.stringify(loginInfo.currentOrg));
            localStorage.setItem('currentDept', JSON.stringify(loginInfo.currentDept));
            localStorage.setItem('permissions', JSON.stringify(loginInfo.permissions));
            localStorage.setItem('authorization', JSON.stringify(loginInfo.authorization));
            localStorage.setItem('userAllOrgDeptList', JSON.stringify(loginInfo.userAllOrgDeptList));
            localStorage.setItem('availableDepts', JSON.stringify(loginInfo.availableDepts));

            
            
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
/* 保留用户指定的背景样式 */
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
  padding: 20px;
  font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
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

/* 双面板容器样式 */
.container {
  display: flex;
  max-width: 1100px;
  width: 100%;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(15px);
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 
      0 20px 60px rgba(0, 0, 0, 0.5),
      0 0 0 1px rgba(255, 255, 255, 0.1);
  animation: fadeInUp 1s ease-out;
  border: 1px solid rgba(255, 255, 255, 0.15);
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

/* 左侧信息面板样式 */
.left-panel {
  flex: 1;
  background: linear-gradient(45deg, rgba(59, 130, 246, 0.9), rgba(139, 92, 246, 0.9));
  padding: 60px 50px;
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  overflow: hidden;
  animation: slideInLeft 1s ease-out;
}

.left-panel::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: float 30s infinite linear;
  opacity: 0.3;
}

/* 右侧登录面板样式 */
.right-panel {
  flex: 1;
  background: rgba(255, 255, 255, 0.97);
  padding: 70px 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  animation: slideInRight 1s ease-out 0.3s both;
}

/* Logo样式 */
.logo {
  display: flex;
  align-items: center;
  margin-bottom: 35px;
  animation: fadeInUp 0.8s ease-out 0.5s both;
  position: relative;
  z-index: 1;
}

.logo-icon {
  font-size: 42px;
  margin-right: 18px;
  animation: pulse 2s infinite;
}

.logo-text {
  font-size: 32px;
  font-weight: 800;
  letter-spacing: 1px;
}

/* 系统名称样式 */
.system-name {
  font-size: 36px;
  font-weight: 800;
  margin-bottom: 20px;
  background: linear-gradient(to right, #fff, #e0f2fe, #bae6fd);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: glow 3s infinite alternate;
  position: relative;
  z-index: 1;
}

/* 系统描述样式 */
.system-desc {
  font-size: 17px;
  opacity: 0.95;
  line-height: 1.7;
  margin-bottom: 45px;
  animation: fadeInUp 0.8s ease-out 0.7s both;
  position: relative;
  z-index: 1;
}

/* 功能列表样式 */
.feature-list {
  list-style: none;
  animation: fadeInUp 0.8s ease-out 0.9s both;
  position: relative;
  z-index: 1;
}

.feature-list li {
  margin-bottom: 18px;
  display: flex;
  align-items: center;
  transition: transform 0.3s;
}

.feature-list li:hover {
  transform: translateX(10px);
}

.feature-list i {
  margin-right: 15px;
  color: #93c5fd;
  font-size: 20px;
  background: rgba(255, 255, 255, 0.1);
  padding: 10px;
  border-radius: 10px;
}

/* 登录标题样式 */
.login-title {
  font-size: 32px;
  color: #1e293b;
  margin-bottom: 12px;
  font-weight: 800;
  animation: fadeInUp 0.8s ease-out 0.5s both;
}

/* 登录副标题样式 */
.login-subtitle {
  color: #64748b;
  margin-bottom: 45px;
  font-size: 16px;
  animation: fadeInUp 0.8s ease-out 0.6s both;
}

/* 登录表单样式 */
.login-form {
  margin-top: 0;
  position: relative;
  z-index: 1;
}

/* 表单项目样式 */
.login-form ::v-deep .el-form-item {
  margin-bottom: 30px;
  animation: fadeInUp 0.8s ease-out 0.7s both;
}

/* 表单标签样式 */
.login-form ::v-deep .el-form-item__label {
  color: #475569;
  font-weight: 600;
  font-size: 15px;
  padding-bottom: 10px;
  display: flex;
  align-items: center;
}

/* 表单标签图标 */
.login-form ::v-deep .el-form-item__label::before {
  content: '';
  margin-right: 8px;
  color: #3b82f6;
}

/* 输入框样式 */
.login-form ::v-deep .el-input {
  position: relative;
}

.login-form ::v-deep .el-input__inner {
  width: 100%;
  padding: 18px 22px;
  border: 2px solid #e2e8f0;
  border-radius: 14px;
  font-size: 16px;
  transition: all 0.3s;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.login-form ::v-deep .el-input__inner:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 
      0 0 0 4px rgba(59, 130, 246, 0.15),
      0 10px 20px rgba(59, 130, 246, 0.1);
  transform: translateY(-2px);
  background: white;
}

.login-form ::v-deep .el-input__inner::placeholder {
  color: #94a3b8;
}

/* 选择器样式 */
.login-form ::v-deep .el-select {
  width: 100%;
}

.login-form ::v-deep .el-select .el-select__input {
  font-size: 16px;
  color: #1e293b;
  padding: 18px 22px;
}

.login-form ::v-deep .el-select .el-select__caret {
  color: #94a3b8;
  transition: all 0.3s ease;
}

.login-form ::v-deep .el-select:focus-within .el-select__caret {
  color: #3b82f6;
  transform: rotate(180deg);
}

.login-form ::v-deep .el-select .el-select-dropdown {
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.login-form ::v-deep .el-select .el-select-dropdown__item {
  padding: 12px 16px;
  font-size: 14px;
  transition: all 0.2s ease;
  &:hover {
    background-color: rgba(59, 130, 246, 0.1);
    color: #3b82f6;
  }
  &.el-select-dropdown__item.selected {
    background-color: rgba(59, 130, 246, 0.1);
    color: #3b82f6;
  }
}

/* 登录按钮样式 */
.login-button {
  width: 100%;
  height: 56px;
  padding: 18px;
  background: linear-gradient(45deg, #3b82f6, #8b5cf6);
  color: white;
  border: none;
  border-radius: 14px;
  font-size: 17px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s;
  margin-top: 15px;
  animation: fadeInUp 0.8s ease-out 0.8s both;
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.3);
  position: relative;
  overflow: hidden;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, #8b5cf6, #3b82f6);
  transition: all 0.4s;
  z-index: -1;
}

.login-button:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 25px rgba(59, 130, 246, 0.5);
}

.login-button:hover::before {
  left: 0;
}

.login-button:active {
  transform: translateY(-2px);
}

/* 链接样式 */
.link {
  color: #3b82f6;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.3s;
  text-decoration: none;
  display: flex;
  align-items: center;
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
}

.link i {
  margin-right: 8px;
  font-size: 14px;
  transition: transform 0.3s;
}

.link:hover {
  color: #1d4ed8;
  background: rgba(59, 130, 246, 0.1);
  transform: translateY(-2px);
  text-decoration: none;
}

.link:hover i {
  transform: translateX(-3px);
}

/* 链接容器样式 */
.links-container {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
  font-size: 14px;
  animation: fadeInUp 0.8s ease-out 0.9s both;
}

/* 页脚样式 */
.footer {
  text-align: center;
  margin-top: 50px;
  color: #94a3b8;
  font-size: 14px;
  animation: fadeInUp 0.8s ease-out 1s both;
}

/* AI徽章样式 */
.ai-badge {
  display: inline-flex;
  align-items: center;
  background: linear-gradient(45deg, #8b5cf6, #3b82f6);
  color: white;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  margin-left: 12px;
  animation: pulse 2s infinite;
}

.ai-badge i {
  margin-right: 7px;
  animation: spin 4s linear infinite;
}

/* 浮动提示样式 */
.floating-hint {
  position: fixed;
  bottom: 30px;
  right: 30px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  padding: 15px 20px;
  border-radius: 12px;
  color: white;
  font-size: 14px;
  animation: fadeInUp 1s ease-out 1.2s both;
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
  z-index: 100;
}

.floating-hint:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-5px);
}

.floating-hint i {
  margin-right: 10px;
  font-size: 18px;
}

/* 动画定义 */
@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(59, 130, 246, 0.4);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(59, 130, 246, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(59, 130, 246, 0);
  }
}

@keyframes glow {
  0%, 100% {
    text-shadow: 0 0 5px rgba(59, 130, 246, 0.7);
  }
  50% {
    text-shadow: 0 0 20px rgba(59, 130, 246, 0.9), 0 0 30px rgba(59, 130, 246, 0.5);
  }
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes float {
  0% {
    transform: translate(0, 0) rotate(0deg);
  }
  25% {
    transform: translate(100px, 50px) rotate(90deg);
  }
  50% {
    transform: translate(0, 100px) rotate(180deg);
  }
  75% {
    transform: translate(-100px, 50px) rotate(270deg);
  }
  100% {
    transform: translate(0, 0) rotate(360deg);
  }
}

/* 浮动粒子 */
.particles {
    position: absolute;
    width: 100%;
    height: 100%;
    overflow: hidden;
}

.particle {
    position: absolute;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    animation: float 20s infinite linear;
    box-shadow: 0 0 20px rgba(255, 255, 255, 0.3), 
                inset 0 0 10px rgba(255, 255, 255, 0.5);
}

/* 肥皂泡泡颜色效果 - 12个大小不一、颜色不同的粒子 */
.particle:nth-child(1) {
    width: 80px;
    height: 80px;
    top: 10%;
    left: 10%;
    animation-delay: 0s;
    background: rgba(255, 192, 203, 0.3); /* 粉色 */
}

.particle:nth-child(2) {
    width: 120px;
    height: 120px;
    top: 60%;
    left: 80%;
    animation-delay: -5s;
    background: rgba(135, 206, 250, 0.3); /* 天蓝色 */
}

.particle:nth-child(3) {
    width: 60px;
    height: 60px;
    top: 80%;
    left: 20%;
    animation-delay: -10s;
    background: rgba(144, 238, 144, 0.3); /* 浅绿色 */
}

.particle:nth-child(4) {
    width: 100px;
    height: 100px;
    top: 20%;
    left: 70%;
    animation-delay: -15s;
    background: rgba(255, 222, 173, 0.3); /* 浅橙色 */
}

.particle:nth-child(5) {
    width: 70px;
    height: 70px;
    top: 40%;
    left: 30%;
    animation-delay: -20s;
    background: rgba(230, 230, 250, 0.3); /* 淡紫色 */
}

.particle:nth-child(6) {
    width: 90px;
    height: 90px;
    top: 50%;
    left: 60%;
    animation-delay: -25s;
    background: rgba(255, 255, 224, 0.3); /* 浅黄色 */
}

.particle:nth-child(7) {
    width: 50px;
    height: 50px;
    top: 15%;
    left: 50%;
    animation-delay: -30s;
    background: rgba(173, 216, 230, 0.3); /* 淡蓝色 */
}

.particle:nth-child(8) {
    width: 110px;
    height: 110px;
    top: 70%;
    left: 35%;
    animation-delay: -35s;
    background: rgba(255, 182, 193, 0.3); /* 浅粉色 */
}

.particle:nth-child(9) {
    width: 65px;
    height: 65px;
    top: 30%;
    left: 85%;
    animation-delay: -40s;
    background: rgba(152, 251, 152, 0.3); /* 淡绿色 */
}

.particle:nth-child(10) {
    width: 95px;
    height: 95px;
    top: 55%;
    left: 15%;
    animation-delay: -45s;
    background: rgba(255, 218, 185, 0.3); /* 杏色 */
}

.particle:nth-child(11) {
    width: 75px;
    height: 75px;
    top: 25%;
    left: 25%;
    animation-delay: -50s;
    background: rgba(218, 165, 32, 0.2); /* 金色 */
}

.particle:nth-child(12) {
    width: 45px;
    height: 45px;
    top: 75%;
    left: 75%;
    animation-delay: -55s;
    background: rgba(192, 192, 192, 0.3); /* 银色 */
}

/* 响应式设计 */
@media (max-width: 900px) {
  .container {
    flex-direction: column;
    max-width: 500px;
  }
  
  .left-panel, .right-panel {
    padding: 40px 30px;
  }
  
  .links-container {
    flex-direction: column;
    gap: 15px;
    align-items: center;
  }
  
  .system-name {
    font-size: 28px;
  }
  
  .login-title {
    font-size: 28px;
  }
  
  .login-subtitle {
    font-size: 15px;
    margin-bottom: 35px;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 10px;
  }
  
  .left-panel {
    padding: 30px 20px;
  }
  
  .right-panel {
    padding: 30px 20px;
  }
  
  .logo-icon {
    font-size: 28px;
  }
  
  .logo-text {
    font-size: 24px;
  }
  
  .system-name {
    font-size: 24px;
  }
  
  .system-desc {
    font-size: 14px;
  }
  
  .feature-list li {
    font-size: 13px;
  }
  
  .login-title {
    font-size: 24px;
  }
  
  .login-subtitle {
    font-size: 14px;
    margin-bottom: 30px;
  }
  
  .login-form ::v-deep .el-input__inner {
    padding: 12px 16px;
    font-size: 14px;
  }
  
  .login-button {
    height: 52px;
    font-size: 16px;
  }
  
  .floating-hint {
    bottom: 20px;
    right: 20px;
    padding: 12px 16px;
    font-size: 13px;
  }
}
</style>