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
            
            // 调用/api/auth/check接口，获取完整的登录信息
            const loginInfo = await checkLogin({
              tempToken: this.loginData.tempToken,
              selectedOrgCode: this.form.selectedOrg.orgCode
            });
            
            console.log('权限获取登录成功，登录信息:', loginInfo);

            localStorage.setItem('token', loginInfo.accessToken);
            localStorage.setItem('csrfToken', loginInfo.csrfToken);
            
            localStorage.setItem('tokenType', loginInfo.tokenType);
            localStorage.setItem('expiresIn', loginInfo.expiresIn);
            localStorage.setItem('refreshToken', loginInfo.refreshToken);
            localStorage.setItem('loginTime', loginInfo.loginTime);
            localStorage.setItem('selectedOrgCode', loginInfo.selectedOrgCode);
            localStorage.setItem('selectedOrgTime', loginInfo.selectedOrgTime);
            localStorage.setItem('sessionId', loginInfo.sessionId);
            
            // 保存用户信息
            localStorage.setItem('user', JSON.stringify(loginInfo.user));
            
            // 保存当前机构部门信息
            localStorage.setItem('currentOrgDepts', JSON.stringify(loginInfo.currentOrgDepts));
            // 保存所有权限
            localStorage.setItem('allPermissions', JSON.stringify(loginInfo.allPermissions));
            localStorage.setItem('userPermissions', JSON.stringify(loginInfo.userPermissions));
            localStorage.setItem('rolePermissions', JSON.stringify(loginInfo.rolePermissions));
            localStorage.setItem('menuPermissions', JSON.stringify(loginInfo.menuPermissions));
            localStorage.setItem('pagePermissions', JSON.stringify(loginInfo.pagePermissions));
            localStorage.setItem('dataPermissions', JSON.stringify(loginInfo.dataPermissions));
            localStorage.setItem('fieldPermissions', JSON.stringify(loginInfo.fieldPermissions));
            localStorage.setItem('apiPermissions', JSON.stringify(loginInfo.apiPermissions));
            localStorage.setItem('bizPermissions', JSON.stringify(loginInfo.bizPermissions));
            localStorage.setItem('timePermissions', JSON.stringify(loginInfo.timePermissions));

            
            
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
  background: var(--gradient-primary);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
  overflow: hidden;
  padding: var(--spacing-lg);
  font-family: var(--font-sans);
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

.container {
  display: flex;
  max-width: 1200px;
  width: 100%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-glow);
  animation: fadeInUp var(--transition-slow) ease-out;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.left-panel {
  flex: 1;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.15), rgba(255, 255, 255, 0.05));
  padding: var(--spacing-2xl);
  color: var(--text-white);
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  overflow: hidden;
  animation: slideInLeft var(--transition-slow) ease-out;
}

.left-panel::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
  background-size: 60px 60px;
  animation: float 30s infinite linear;
  opacity: 0.4;
}

.right-panel {
  flex: 1;
  background: var(--bg-card);
  padding: var(--spacing-2xl);
  display: flex;
  flex-direction: column;
  justify-content: center;
  animation: slideInRight var(--transition-slow) ease-out 0.3s both;
}

.logo {
  display: flex;
  align-items: center;
  margin-bottom: var(--spacing-xl);
  animation: fadeInUp var(--transition-base) ease-out 0.5s both;
  position: relative;
  z-index: 1;
}

.logo-icon {
  font-size: 48px;
  margin-right: var(--spacing-md);
  animation: pulse 3s infinite;
}

.logo-text {
  font-size: var(--text-3xl);
  font-weight: 800;
  letter-spacing: 1px;
}

.system-name {
  font-size: var(--text-4xl);
  font-weight: 800;
  margin-bottom: var(--spacing-md);
  background: linear-gradient(to right, #fff, #dbeafe, #bfdbfe);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: glow 4s infinite alternate;
  position: relative;
  z-index: 1;
}

.system-desc {
  font-size: var(--text-lg);
  opacity: 0.95;
  line-height: var(--line-height-relaxed);
  margin-bottom: var(--spacing-2xl);
  animation: fadeInUp var(--transition-base) ease-out 0.7s both;
  position: relative;
  z-index: 1;
}

.feature-list {
  list-style: none;
  animation: fadeInUp var(--transition-base) ease-out 0.9s both;
  position: relative;
  z-index: 1;
}

.feature-list li {
  margin-bottom: var(--spacing-md);
  display: flex;
  align-items: center;
  transition: transform var(--transition-base);
}

.feature-list li:hover {
  transform: translateX(12px);
}

.feature-list i {
  margin-right: var(--spacing-md);
  color: rgba(255, 255, 255, 0.7);
  font-size: var(--text-xl);
  background: rgba(255, 255, 255, 0.1);
  padding: var(--spacing-sm);
  border-radius: var(--radius-md);
  backdrop-filter: blur(4px);
}

.login-title {
  font-size: var(--text-3xl);
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
  font-weight: 800;
  animation: fadeInUp var(--transition-base) ease-out 0.5s both;
}

.login-subtitle {
  color: var(--text-secondary);
  margin-bottom: var(--spacing-2xl);
  font-size: var(--text-base);
  animation: fadeInUp var(--transition-base) ease-out 0.6s both;
}

.login-form {
  margin-top: 0;
  position: relative;
  z-index: 1;
}

.login-form ::v-deep .el-form-item {
  margin-bottom: var(--spacing-lg);
  animation: fadeInUp var(--transition-base) ease-out 0.7s both;
}

.login-form ::v-deep .el-form-item__label {
  color: var(--text-secondary);
  font-weight: 600;
  font-size: var(--text-sm);
  padding-bottom: var(--spacing-sm);
  display: flex;
  align-items: center;
}

.login-form ::v-deep .el-input__inner {
  width: 100%;
  padding: var(--spacing-md) var(--spacing-lg);
  border: 2px solid var(--border-light);
  border-radius: var(--radius-lg);
  font-size: var(--text-base);
  transition: all var(--transition-base);
  background: var(--bg-card);
  box-shadow: var(--shadow-sm);
}

.login-form ::v-deep .el-input__inner:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.15), var(--shadow-md);
  transform: translateY(-2px);
}

.login-form ::v-deep .el-input__inner::placeholder {
  color: var(--text-muted);
}

.login-form ::v-deep .el-select {
  width: 100%;
}

.login-form ::v-deep .el-select .el-select__input {
  font-size: var(--text-base);
  color: var(--text-primary);
  padding: var(--spacing-md) var(--spacing-lg);
}

.login-form ::v-deep .el-select .el-select__caret {
  color: var(--text-muted);
  transition: transform var(--transition-base);
}

.login-form ::v-deep .el-select:focus-within .el-select__caret {
  color: var(--color-primary);
  transform: rotate(180deg);
}

.login-form ::v-deep .el-select .el-select-dropdown {
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-xl);
  overflow: hidden;
}

.login-form ::v-deep .el-select .el-select-dropdown__item {
  padding: var(--spacing-sm) var(--spacing-md);
  font-size: var(--text-sm);
  transition: all var(--transition-fast);
}

.login-form ::v-deep .el-select .el-select-dropdown__item:hover {
  background: var(--bg-hover);
  color: var(--color-primary);
}

.login-form ::v-deep .el-select .el-select-dropdown__item.el-select-dropdown__item.selected {
  background: var(--bg-active);
  color: var(--color-primary);
}

.login-button {
  width: 100%;
  height: 56px;
  padding: var(--spacing-md);
  background: var(--gradient-primary);
  color: var(--text-white);
  border: none;
  border-radius: var(--radius-lg);
  font-size: var(--text-lg);
  font-weight: 700;
  cursor: pointer;
  transition: all var(--transition-base);
  margin-top: var(--spacing-md);
  animation: fadeInUp var(--transition-base) ease-out 0.8s both;
  box-shadow: var(--shadow-lg);
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
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left var(--transition-slow);
  z-index: -1;
}

.login-button:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
}

.login-button:hover::before {
  left: 100%;
}

.login-button:active {
  transform: translateY(-2px);
}

.link {
  color: var(--color-primary);
  font-size: var(--text-sm);
  font-weight: 600;
  transition: all var(--transition-base);
  text-decoration: none;
  display: flex;
  align-items: center;
  background: none;
  border: none;
  cursor: pointer;
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--radius-md);
  font-family: var(--font-sans);
}

.link i {
  margin-right: var(--spacing-xs);
  font-size: var(--text-sm);
  transition: transform var(--transition-base);
}

.link:hover {
  color: var(--color-primary-dark);
  background: var(--bg-hover);
  transform: translateY(-2px);
}

.link:hover i {
  transform: translateX(-3px);
}

.links-container {
  display: flex;
  justify-content: space-between;
  margin-top: var(--spacing-lg);
  font-size: var(--text-sm);
  animation: fadeInUp var(--transition-base) ease-out 0.9s both;
}

.footer {
  text-align: center;
  margin-top: var(--spacing-2xl);
  color: var(--text-muted);
  font-size: var(--text-sm);
  animation: fadeInUp var(--transition-base) ease-out 1s both;
}

.ai-badge {
  display: inline-flex;
  align-items: center;
  background: var(--gradient-primary);
  color: var(--text-white);
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: 600;
  margin-left: var(--spacing-sm);
  animation: pulse 3s infinite;
}

.ai-badge i {
  margin-right: var(--spacing-xs);
  animation: spin 4s linear infinite;
}

.floating-hint {
  position: fixed;
  bottom: var(--spacing-xl);
  right: var(--spacing-xl);
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(12px);
  padding: var(--spacing-md) var(--spacing-lg);
  border-radius: var(--radius-lg);
  color: var(--text-white);
  font-size: var(--text-sm);
  animation: fadeInUp var(--transition-slow) ease-out 1.2s both;
  border: 1px solid rgba(255, 255, 255, 0.25);
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all var(--transition-base);
  z-index: 100;
}

.floating-hint:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-5px);
  box-shadow: var(--shadow-lg);
}

.floating-hint i {
  margin-right: var(--spacing-sm);
  font-size: var(--text-lg);
}

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
  box-shadow: 0 0 30px rgba(255, 255, 255, 0.2),
              inset 0 0 15px rgba(255, 255, 255, 0.3);
}

.particle:nth-child(1) {
  width: 80px;
  height: 80px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
  background: rgba(255, 182, 193, 0.25);
}

.particle:nth-child(2) {
  width: 120px;
  height: 120px;
  top: 60%;
  left: 80%;
  animation-delay: -5s;
  background: rgba(135, 206, 250, 0.25);
}

.particle:nth-child(3) {
  width: 60px;
  height: 60px;
  top: 80%;
  left: 20%;
  animation-delay: -10s;
  background: rgba(144, 238, 144, 0.25);
}

.particle:nth-child(4) {
  width: 100px;
  height: 100px;
  top: 20%;
  left: 70%;
  animation-delay: -15s;
  background: rgba(255, 218, 185, 0.25);
}

.particle:nth-child(5) {
  width: 70px;
  height: 70px;
  top: 40%;
  left: 30%;
  animation-delay: -20s;
  background: rgba(230, 230, 250, 0.25);
}

.particle:nth-child(6) {
  width: 90px;
  height: 90px;
  top: 50%;
  left: 60%;
  animation-delay: -25s;
  background: rgba(255, 250, 205, 0.25);
}

.particle:nth-child(7) {
  width: 50px;
  height: 50px;
  top: 15%;
  left: 50%;
  animation-delay: -30s;
  background: rgba(176, 224, 230, 0.25);
}

.particle:nth-child(8) {
  width: 110px;
  height: 110px;
  top: 70%;
  left: 35%;
  animation-delay: -35s;
  background: rgba(255, 192, 203, 0.25);
}

.particle:nth-child(9) {
  width: 65px;
  height: 65px;
  top: 30%;
  left: 85%;
  animation-delay: -40s;
  background: rgba(144, 238, 144, 0.25);
}

.particle:nth-child(10) {
  width: 95px;
  height: 95px;
  top: 55%;
  left: 15%;
  animation-delay: -45s;
  background: rgba(255, 228, 181, 0.25);
}

.particle:nth-child(11) {
  width: 75px;
  height: 75px;
  top: 25%;
  left: 25%;
  animation-delay: -50s;
  background: rgba(218, 165, 32, 0.15);
}

.particle:nth-child(12) {
  width: 45px;
  height: 45px;
  top: 75%;
  left: 75%;
  animation-delay: -55s;
  background: rgba(192, 192, 192, 0.25);
}

@media (max-width: 900px) {
  .container {
    flex-direction: column;
    max-width: 520px;
  }

  .left-panel, .right-panel {
    padding: var(--spacing-xl);
  }

  .links-container {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: center;
  }

  .system-name {
    font-size: var(--text-2xl);
  }

  .login-title {
    font-size: var(--text-2xl);
  }

  .login-subtitle {
    font-size: var(--text-sm);
    margin-bottom: var(--spacing-xl);
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: var(--spacing-md);
  }

  .left-panel {
    padding: var(--spacing-lg);
  }

  .right-panel {
    padding: var(--spacing-lg);
  }

  .logo-icon {
    font-size: 36px;
  }

  .logo-text {
    font-size: var(--text-2xl);
  }

  .system-name {
    font-size: var(--text-2xl);
  }

  .system-desc {
    font-size: var(--text-sm);
  }

  .feature-list li {
    font-size: var(--text-sm);
  }

  .login-title {
    font-size: var(--text-xl);
  }

  .login-subtitle {
    font-size: var(--text-sm);
    margin-bottom: var(--spacing-lg);
  }

  .login-form ::v-deep .el-input__inner {
    padding: var(--spacing-sm) var(--spacing-md);
    font-size: var(--text-sm);
  }

  .login-button {
    height: 52px;
    font-size: var(--text-base);
  }

  .floating-hint {
    bottom: var(--spacing-lg);
    right: var(--spacing-lg);
    padding: var(--spacing-sm) var(--spacing-md);
    font-size: var(--text-sm);
  }
}
</style>