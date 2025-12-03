<template>
  <div class="page-container">
    <div class="page-header">
      <h1>版本管理方案</h1>
    </div>
    <div class="page-content">
      <el-empty description="版本管理功能开发中..."></el-empty>
      <el-button type="primary" @click="goBack">返回首页</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'VersionManagement',
  methods: {
    goBack() {
      this.$router.push('/dashboard');
    }
  }
};
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.page-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  min-height: 500px;
}

.el-button {
  margin-top: 24px;
}
</style>