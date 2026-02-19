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
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eaf6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.form-container {
  width: 100%;
  max-width: 500px;
}

.form-header {
  text-align: center;
  margin-bottom: 32px;
}

.form-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.form-title i {
  font-size: 32px;
  color: #7B68EE;
}

.form-subtitle {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
  line-height: 1.6;
}

.form-card {
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(123, 104, 238, 0.12);
  border: 1px solid rgba(123, 104, 238, 0.1);
}

.form-card ::v-deep .el-card__header {
  border-bottom: 2px solid rgba(123, 104, 238, 0.1);
  padding: 24px;
}

.form-card ::v-deep .el-card__body {
  padding: 32px 24px;
}

.password-form {
  margin: 0;
}

.password-form ::v-deep .el-form-item__label {
  font-size: 14px;
  font-weight: 500;
  color: #4a5568;
  padding-right: 16px;
}

.password-form ::v-deep .el-input__inner {
  border-radius: 8px;
  border: 1px solid #e4e7eb;
  transition: all 0.3s ease;
  font-size: 14px;
  padding: 12px 16px;
}

.password-form ::v-deep .el-input__inner:focus {
  border-color: #7B68EE;
  box-shadow: 0 0 0 4px rgba(123, 104, 238, 0.2);
}

.password-form ::v-deep .el-input__prefix {
  left: 12px;
  color: #7B68EE;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 8px;
}

.cancel-btn {
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.3s ease;
  border: 1px solid #d1d5db;
  color: #606266;
}

.cancel-btn:hover {
  background: #f0f0f0;
  border-color: #b3d8ff;
  color: #409EFF;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.submit-btn {
  padding: 12px 32px;
  border-radius: 8px;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  border: none;
  color: #fff;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #9370DB 0%, #BA55D3 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(123, 104, 238, 0.35);
}

.submit-btn:active {
  transform: translateY(0);
}

.security-tips {
  margin-top: 32px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  padding: 24px;
  border: 1px solid rgba(123, 104, 238, 0.1);
}

.tips-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tips-title i {
  color: #7B68EE;
  font-size: 18px;
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tip-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 10px 0;
  color: #4a5568;
  font-size: 14px;
  line-height: 1.6;
}

.tip-item i {
  color: #67c23a;
  font-size: 16px;
  flex-shrink: 0;
  margin-top: 2px;
}

@media screen and (max-width: 768px) {
  .change-password-page {
    padding: 20px 16px;
  }

  .form-container {
    max-width: 100%;
  }

  .form-header {
    margin-bottom: 24px;
  }

  .form-title {
    font-size: 24px;
  }

  .form-card ::v-deep .el-card__body {
    padding: 24px 16px;
  }

  .form-actions {
    flex-direction: column;
  }

  .cancel-btn,
  .submit-btn {
    width: 100%;
  }

  .security-tips {
    padding: 20px 16px;
  }
}

@media screen and (max-width: 480px) {
  .form-title {
    font-size: 20px;
  }

  .form-subtitle {
    font-size: 14px;
  }

  .password-form ::v-deep .el-form-item__label {
    font-size: 13px;
  }
}
</style>
