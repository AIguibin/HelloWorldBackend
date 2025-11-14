<template>
  <div style="max-width:360px;margin:60px auto;">
    <h2 style="text-align:center;margin-bottom:20px;">变更登记系统登录</h2>
    <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
      <el-form-item label="账号" prop="username">
        <el-input v-model="form.username" autocomplete="username" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" type="password" autocomplete="current-password" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit" :loading="loading">登录</el-button>
        <el-button type="text" @click="onForgotPassword" :disabled="loading" style="margin-left:8px;">忘记密码</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { login } from '../api';
import { Message } from 'element-ui';

export default {
  name: 'Login',
  data() {
    return {
      form: { username: '', password: '' },
      loading: false,
      rules: {
        username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
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
          const data = await login(this.form.username, this.form.password);
          localStorage.setItem('token', data.token);
          localStorage.setItem('csrfToken', data.csrfToken);
          localStorage.setItem('user', JSON.stringify({ username: data.username, usernumb: data.usernumb, chineseName: data.chineseName }));
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