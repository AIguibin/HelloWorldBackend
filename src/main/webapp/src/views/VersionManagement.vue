<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="el-icon-refresh"></i>
          版本管理方案
        </h1>
        <p class="page-subtitle">管理系统版本迭代计划，包括版本规划、发布计划和版本依赖关系管理</p>
      </div>
    </div>
    <div class="page-content">
      <div class="empty-state">
        <div class="empty-illustration">
          <i class="el-icon-refresh"></i>
        </div>
        <el-empty description="版本管理功能开发中...">
          <el-button type="primary" @click="goBack" class="back-btn">
            <i class="el-icon-back"></i>
            返回首页
          </el-button>
        </el-empty>
      </div>
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
  padding: 24px;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eaf6 100%);
}

.page-header {
  margin-bottom: 32px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px 32px;
  box-shadow: 0 4px 20px rgba(123, 104, 238, 0.08);
  border: 1px solid rgba(123, 104, 238, 0.1);
}

.header-content {
  text-align: center;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 16px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.page-title i {
  font-size: 32px;
  color: #7B68EE;
}

.page-subtitle {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
  line-height: 1.6;
}

.page-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  min-height: 500px;
}

.empty-state {
  width: 100%;
  max-width: 600px;
}

.empty-illustration {
  font-size: 80px;
  color: #7B68EE;
  margin-bottom: 24px;
  animation: rotate 4s linear infinite;
}

@keyframes rotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.back-btn {
  margin-top: 24px;
  padding: 12px 32px;
  border-radius: 8px;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  border: none;
  color: #fff;
}

.back-btn:hover {
  background: linear-gradient(135deg, #9370DB 0%, #BA55D3 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(123, 104, 238, 0.3);
}

.back-btn i {
  margin-right: 8px;
}

@media screen and (max-width: 768px) {
  .page-container {
    padding: 16px;
  }

  .page-header {
    padding: 20px 24px;
  }

  .page-title {
    font-size: 24px;
  }

  .page-title i {
    font-size: 28px;
  }

  .page-content {
    padding: 40px 16px;
  }

  .empty-illustration {
    font-size: 64px;
  }
}

@media screen and (max-width: 480px) {
  .page-title {
    font-size: 20px;
  }

  .page-subtitle {
    font-size: 14px;
  }

  .back-btn {
    width: 100%;
  }
}
</style>
