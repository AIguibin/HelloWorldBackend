<script setup lang="ts">
const stackCards = [
  { title: '后端框架', value: 'Spring Boot 4.1', icon: 'Cpu', desc: 'Java 21 / MyBatis-Plus / 统一响应与异常' },
  { title: '数据访问', value: 'MyBatis-Plus 3.5', icon: 'Coin', desc: 'MySQL 8.4 / HikariCP 连接池' },
  { title: '前端框架', value: 'Vue 3.5 + Vite 8', icon: 'Monitor', desc: 'TypeScript 5.9 strict / Element Plus' },
  { title: '构建工具', value: 'Maven 3.9 + pnpm 10+', icon: 'SetUp', desc: 'Maven Wrapper / 核心包锁定' },
];
</script>

<template>
  <div class="dashboard">
    <el-card shadow="never" class="welcome-card">
      <div class="welcome-row">
        <el-icon :size="36" color="var(--app-primary-color)">
          <Platform />
        </el-icon>
        <div>
          <h2>工程框架骨架已就绪</h2>
          <p>当前为纯净脚手架，未包含任何业务功能；业务模块按分层规范与基线约定接入</p>
        </div>
      </div>
    </el-card>

    <el-row :gutter="16" class="stack-row">
      <el-col v-for="card in stackCards" :key="card.title" :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stack-card">
          <div class="stack-header">
            <el-icon :size="20">
              <component :is="card.icon" />
            </el-icon>
            <span>{{ card.title }}</span>
          </div>
          <div class="stack-value">
            {{ card.value }}
          </div>
          <div class="stack-desc">
            {{ card.desc }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="placeholder-card">
      <template #header> 业务模块 </template>
      <el-empty description="暂无业务模块，按项目规则文档的交付清单接入" />
    </el-card>
  </div>
</template>

<style scoped>
.welcome-card .welcome-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.welcome-row h2 {
  margin: 0 0 4px;
  font-size: 18px;
  color: var(--app-text-primary);
}

.welcome-row p {
  margin: 0;
  font-size: 13px;
  color: var(--app-text-secondary);
}

.stack-row {
  margin-top: 16px;
}

.stack-card {
  margin-bottom: 16px;
}

.stack-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--app-text-secondary);
}

.stack-value {
  margin-top: 10px;
  font-size: 18px;
  font-weight: 600;
  color: var(--app-text-primary);
}

.stack-desc {
  margin-top: 6px;
  font-size: 12px;
  color: var(--app-text-secondary);
}

.placeholder-card {
  margin-top: 8px;
}
</style>
