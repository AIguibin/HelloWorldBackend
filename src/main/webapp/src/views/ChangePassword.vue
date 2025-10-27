<template>
  <div style="max-width:420px;margin:40px auto;">
    <h2 style="text-align:center;margin-bottom:20px;">修改密码</h2>
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="当前密码" prop="currentPassword">
        <el-input v-model="form.currentPassword" type="password" autocomplete="current-password" />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="form.newPassword" type="password" autocomplete="new-password" />
      </el-form-item>
      <el-form-item label="确认新密码" prop="confirmPassword">
        <el-input v-model="form.confirmPassword" type="password" autocomplete="new-password" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit" :loading="loading">提交</el-button>
        <el-button @click="$router.back()" :disabled="loading" style="margin-left:8px;">返回</el-button>
      </el-form-item>
    </el-form>
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
          // 错误消息已在拦截器内弹出
        } finally {
          this.loading = false;
        }
      });
    }
  }
};
</script>