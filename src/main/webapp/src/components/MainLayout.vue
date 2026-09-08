<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';

defineOptions({ name: 'MainLayout' });

const route = useRoute();
const activeMenu = computed(() => route.path);
</script>

<template>
  <el-container class="app-layout">
    <el-aside :width="'220px'" class="app-aside">
      <div class="app-logo">
        <el-icon :size="22"><Platform /></el-icon>
        <span class="app-logo-text">架构管理平台</span>
      </div>
      <el-menu
        class="app-menu"
        router
        unique-opened
        :default-active="activeMenu"
        background-color="transparent"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>首页</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="app-header" :height="'56px'">
        <div class="app-header-title">{{ route.meta.title ?? '首页' }}</div>
      </el-header>

      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>
