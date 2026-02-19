<template>
  <div class="change-password-page">
    <div class="form-container">
      <div class="form-header">
        <h2 class="form-title">
          <i class="el-icon-lock"></i>
          修改密码
        </h2>
        <p class="form-subtitle">为了账户安全，请定期修改您的密码</p>
      </div>
      
      <el-card class="form-card" shadow="hover">
        <el-form :model="form" :rules="rules" ref="formRef" label-width="120px" class="password-form">
          <el-form-item label="当前密码" prop="currentPassword">
            <el-input 
              v-model="form.currentPassword" 
              type="password" 
              autocomplete="current-password"
              placeholder="请输入当前密码"
              show-password
              prefix-icon="el-icon-lock"
            />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input 
              v-model="form.newPassword" 
              type="password" 
              autocomplete="new-password"
              placeholder="请输入新密码（至少6位）"
              show-password
              prefix-icon="el-icon-key"
            />
          </el-form-item>
          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input 
              v-model="form.confirmPassword" 
              type="password" 
              autocomplete="new-password"
              placeholder="请再次输入新密码"
              show-password
              prefix-icon="el-icon-key"
            />
          </el-form-item>
          <el-form-item>
            <div class="form-actions">
              <el-button @click="$router.back()" :disabled="loading" class="cancel-btn">
                <i class="el-icon-back"></i>
                返回
              </el-button>
              <el-button type="primary" @click="onSubmit" :loading="loading" class="submit-btn">
                <i class="el-icon-check"></i>
                提交
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </el-card>
      
      <div class="security-tips">
        <h4 class="tips-title">
          <i class="el-icon-info"></i>
          密码安全建议
        </h4>
        <ul class="tips-list">
          <li class="tip-item">
            <i class="el-icon-check"></i>
            密码长度至少6位
          </li>
          <li class="tip-item">
            <i class="el-icon-check"></i>
            建议使用字母、数字和特殊字符的组合
          </li>
          <li class="tip-item">
            <i class="el-icon-check"></i>
            避免使用生日、手机号等易猜测信息
          </li>
          <li class="tip-item">
            <i class="el-icon-check"></i>
            定期更换密码，提高账户安全性
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import { changePassword } from '../api';
import { Message } from 'element-ui';

export default {
  name: 'ChangePassword',
  data() {
    return {
      loading: false,
      form: {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      rules: {
        currentPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码至少6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: (rule, value, callback) => {
              if (value !== this.form.newPassword) {
                callback(new Error('两次输入的密码不一致'));
              } else {
                callback();
              }
            }, trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    onSubmit() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) return;
        this.loading = true;
        try {
          const payload = {
            currentPassword: this.form.currentPassword,
            newPassword: this.form.newPassword
          };
          await changePassword(payload);
          Message.success('密码修改成功');
          this.$router.replace('/');
        } catch (e) {
        } finally {
          this.loading = false;
        }
      });
    }
  }
};
</script>

<style scoped>
.change-password-page {
  min-height: 100vh;
  background: var(--gradient-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xl) var(--spacing-md);
  animation: fadeIn var(--transition-base) ease-out;
}

.form-container {
  width: 100%;
  max-width: 520px;
}

.form-header {
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.form-title {
  font-size: var(--text-3xl);
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
}

.form-title i {
  font-size: var(--text-3xl);
  color: var(--color-primary);
}

.form-subtitle {
  font-size: var(--text-base);
  color: var(--text-secondary);
  margin: 0;
  line-height: var(--line-height-relaxed);
}

.form-card {
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--border-light);
  background: var(--bg-card);
  animation: fadeInUp var(--transition-base) ease-out 0.2s both;
}

.form-card ::v-deep .el-card__header {
  border-bottom: 1px solid var(--border-light);
  padding: var(--spacing-lg);
}

.form-card ::v-deep .el-card__body {
  padding: var(--spacing-xl) var(--spacing-lg);
}

.password-form {
  margin: 0;
}

.password-form ::v-deep .el-form-item__label {
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--text-secondary);
  padding-right: var(--spacing-md);
}

.password-form ::v-deep .el-input__inner {
  border-radius: var(--radius-lg);
  border: 2px solid var(--border-light);
  transition: all var(--transition-base);
  font-size: var(--text-base);
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--bg-card);
}

.password-form ::v-deep .el-input__inner:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.15), var(--shadow-md);
  transform: translateY(-2px);
}

.password-form ::v-deep .el-input__prefix {
  left: var(--spacing-sm);
  color: var(--color-primary);
}

.form-actions {
  display: flex;
  gap: var(--spacing-md);
  justify-content: center;
  margin-top: var(--spacing-md);
}

.cancel-btn {
  padding: var(--spacing-md) var(--spacing-xl);
  border-radius: var(--radius-lg);
  font-weight: 600;
  font-size: var(--text-base);
  transition: all var(--transition-base);
  border: 2px solid var(--border-light);
  color: var(--text-secondary);
  background: var(--bg-card);
}

.cancel-btn:hover {
  background: var(--bg-hover);
  border-color: var(--color-primary-light);
  color: var(--color-primary);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.submit-btn {
  padding: var(--spacing-md) var(--spacing-xl);
  border-radius: var(--radius-lg);
  font-weight: 600;
  font-size: var(--text-base);
  transition: all var(--transition-base);
  background: var(--gradient-primary);
  border: none;
  color: var(--text-white);
  box-shadow: var(--shadow-md);
  position: relative;
  overflow: hidden;
}

.submit-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left var(--transition-slow);
}

.submit-btn:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-xl);
}

.submit-btn:hover::before {
  left: 100%;
}

.submit-btn:active {
  transform: translateY(-1px);
}

.security-tips {
  margin-top: var(--spacing-xl);
  background: rgba(255, 255, 255, 0.95);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-md);
  animation: fadeInUp var(--transition-base) ease-out 0.4s both;
}

.tips-title {
  font-size: var(--text-lg);
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.tips-title i {
  color: var(--color-primary);
  font-size: var(--text-xl);
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tip-item {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) 0;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  line-height: var(--line-height-relaxed);
  transition: all var(--transition-fast);
}

.tip-item:hover {
  transform: translateX(4px);
}

.tip-item i {
  color: var(--color-success);
  font-size: var(--text-lg);
  flex-shrink: 0;
  margin-top: 2px;
}

@media screen and (max-width: 768px) {
  .change-password-page {
    padding: var(--spacing-lg) var(--spacing-md);
  }

  .form-container {
    max-width: 100%;
  }

  .form-header {
    margin-bottom: var(--spacing-lg);
  }

  .form-title {
    font-size: var(--text-2xl);
  }

  .form-card ::v-deep .el-card__body {
    padding: var(--spacing-lg) var(--spacing-md);
  }

  .form-actions {
    flex-direction: column;
  }

  .cancel-btn,
  .submit-btn {
    width: 100%;
  }

  .security-tips {
    padding: var(--spacing-lg) var(--spacing-md);
  }
}

@media screen and (max-width: 480px) {
  .form-title {
    font-size: var(--text-xl);
  }

  .form-subtitle {
    font-size: var(--text-sm);
  }

  .password-form ::v-deep .el-form-item__label {
    font-size: var(--text-sm);
  }
}
</style>
