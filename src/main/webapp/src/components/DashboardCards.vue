<template>
  <div class="dashboard-cards-container">
    <!-- 功能模块卡片布局 -->
    <div class="section-header">
      <h2 class="section-title">功能模块</h2>
      <p class="section-subtitle">点击下方卡片进入对应功能区域</p>
    </div>
    <div class="dashboard-cards">
      <!-- 变更管理卡片 -->
      <el-card 
        class="module-card change-management" 
        shadow="hover"
        @click.native="goToModule('/change-records')"
      >
        <div class="card-content" style="pointer-events: none;">
          <div class="card-icon">
            <i class="el-icon-document"></i>
          </div>
          <div class="card-info">
            <h2 class="card-title">变更管理</h2>
            <p class="card-description">管理系统中的所有代码变更记录，包括创建、查看、编辑和跟踪变更历史</p>
          </div>
          <div class="card-action">
            <i class="el-icon-arrow-right"></i>
          </div>
        </div>
      </el-card>

      <!-- 版本管理方案卡片 -->
      <el-card 
        class="module-card version-management" 
        shadow="hover"
        @click.native="goToModule('/version-management')"
      >
        <div class="card-content" style="pointer-events: none;">
          <div class="card-icon">
            <i class="el-icon-refresh"></i>
          </div>
          <div class="card-info">
            <h2 class="card-title">版本管理方案</h2>
            <p class="card-description">管理系统版本迭代计划，包括版本规划、发布计划和版本依赖关系管理</p>
          </div>
          <div class="card-action">
            <i class="el-icon-arrow-right"></i>
          </div>
        </div>
      </el-card>

      <!-- 日常开发规范卡片 -->
      <el-card 
        class="module-card development-standards" 
        shadow="hover"
        @click.native="goToModule('/development-standards')"
      >
        <div class="card-content" style="pointer-events: none;">
          <div class="card-icon">
            <i class="el-icon-s-order"></i>
          </div>
          <div class="card-info">
            <h2 class="card-title">日常开发规范</h2>
            <p class="card-description">查看和管理团队开发规范，包括编码标准、文档要求和工作流程指南</p>
          </div>
          <div class="card-action">
            <i class="el-icon-arrow-right"></i>
          </div>
        </div>
      </el-card>

      <!-- 系统设置卡片 -->
      <el-card 
        class="module-card system-settings" 
        shadow="hover"
        @click.native="goToModule('/system-settings')"
      >
        <div class="card-content" style="pointer-events: none;">
          <div class="card-icon">
            <i class="el-icon-setting"></i>
          </div>
          <div class="card-info">
            <h2 class="card-title">系统设置</h2>
            <p class="card-description">配置系统参数，管理用户权限和系统功能，确保系统安全稳定运行</p>
          </div>
          <div class="card-action">
            <i class="el-icon-arrow-right"></i>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 使用提示 -->
    <div class="tips-section">
      <h3 class="tips-title">使用提示</h3>
      <div class="tips-list">
        <div class="tip-item" @click="goToModule('/')">
          <div class="tip-content">
            <span class="tip-icon">💡</span>
            <span class="tip-text">点击上方卡片快速进入对应功能模块</span>
          </div>
          <i class="el-icon-arrow-right tip-action"></i>
        </div>
        <div class="tip-item" @click="goToModule('/')">
          <div class="tip-content">
            <span class="tip-icon">📱</span>
            <span class="tip-text">系统支持响应式设计，在移动设备上同样可以使用</span>
          </div>
          <i class="el-icon-arrow-right tip-action"></i>
        </div>
        <div class="tip-item" @click="goToModule('/')">
          <div class="tip-content">
            <span class="tip-icon">🔍</span>
            <span class="tip-text">首次访问功能模块时会自动加载对应组件</span>
          </div>
          <i class="el-icon-arrow-right tip-action"></i>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DashboardCards',
  methods: {
    // 导航到指定功能模块
    goToModule(path) {
      console.log('导航到路径:', path);
      // 确保路径有效并导航到目标页面
      if (path && path.startsWith('/') && this.$router) {
        try {
          this.$router.push(path).catch(error => {
            // 处理导航错误，特别是重复导航错误
            if (!error.name || error.name !== 'NavigationDuplicated') {
              console.error('导航失败:', error);
            }
          });
        } catch (error) {
          console.error('路由导航出错:', error);
        }
      } else {
        console.warn('无效的导航路径:', path);
      }
    }
  }
};
</script>

<style scoped>
.dashboard-cards-container {
  padding: 8px;
  max-width: 1400px;
  margin: 0 auto;
}

/* 区域标题样式 */
.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-title {
  font-size: 32px;
  font-weight: 700;
  color: #1F2937;
  margin-bottom: 12px;
  background: linear-gradient(135deg, #4F46E5, #7C3AED);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.section-subtitle {
  font-size: 16px;
  color: #6B7280;
  margin: 0;
}

.quick-access-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

/* 快速访问卡片样式 */
.quick-action-card {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 32px 24px;
  transition: all 0.3s ease;
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  border: 1px solid transparent;
  position: relative;
  overflow: hidden;
}

.quick-action-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #3B82F6, #6366F1);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
}

.quick-action-card:hover::before {
  transform: scaleX(1);
}

.quick-action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: #3B82F6;
}

.quick-action-card .card-icon {
  font-size: 48px;
  margin-bottom: 16px;
  color: #3B82F6;
  transition: transform 0.3s ease;
}

.quick-action-card:hover .card-icon {
  transform: scale(1.1);
}

.quick-action-card .card-title {
  font-size: 18px;
  font-weight: 600;
  color: #1F2937;
  margin-bottom: 8px;
}

.quick-action-card .card-description {
  font-size: 14px;
  color: #6B7280;
  line-height: 1.5;
}

/* 功能模块卡片布局 */
.dashboard-cards {
  display: flex;
  flex-wrap: nowrap;
  gap: 30px;
  margin: 0 auto;
  padding: 10px;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  scroll-behavior: smooth;
}

/* 自定义滚动条样式 */
.dashboard-cards::-webkit-scrollbar {
  height: 6px;
}

.dashboard-cards::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.dashboard-cards::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 10px;
}

.dashboard-cards::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.module-card {
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  cursor: pointer;
  border-radius: 16px !important;
  overflow: hidden;
  position: relative;
  width: 120px;
  height: 120px;
  background: white !important;
}

.module-card::before {
  content: '';
  position: absolute;
  top: -5px;
  left: -5px;
  right: -5px;
  bottom: -5px;
  background: linear-gradient(45deg, transparent 45%, rgba(255,255,255,0.1) 50%, transparent 55%);
  background-size: 300% 300%;
  opacity: 0;
  transition: opacity 0.5s ease;
  z-index: 1;
}

.module-card:hover::before {
  opacity: 1;
  animation: shimmer 2s infinite;
}

.module-card:hover {
  width: 382px;
  height: 191px;
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.12) !important;
  z-index: 10;
}

.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 10px;
  cursor: pointer;
  text-align: center;
  transition: all 0.4s ease;
}

.module-card:hover .card-content {
  flex-direction: row;
  text-align: left;
  padding: 16px 20px;
}

.card-content .card-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin: 0 auto 8px;
  flex-shrink: 0;
  transition: all 0.4s ease;
}

.module-card:hover .card-icon {
  margin: 0 16px 0 0;
}

.card-info {
  flex: 1;
  min-width: 0;
}

.card-title {
  font-size: 14px;
  font-weight: 700;
  margin: 4px 0;
  color: #1F2937;
  transition: transform 0.3s ease;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100px;
}

.module-card:hover .card-title {
  transform: translateX(0);
  max-width: 100%;
  white-space: normal;
  font-size: 18px;
}

.card-description {
  display: none;
}

.module-card:hover .card-description {
  display: block;
  font-size: 14px;
  line-height: 1.5;
  color: #4B5563;
  margin: 8px 0 0;
  flex: 1;
}

.card-action {
  display: none;
}

.module-card:hover .card-action {
  display: block;
  font-size: 20px;
  color: #7B68EE;
  opacity: 1;
  margin-left: 16px;
}

/* 不同卡片的渐变背景和图标样式 */
.change-management {
  background: linear-gradient(135deg, 
    rgba(255, 105, 180, 0.08) 0%, 
    rgba(255, 165, 0, 0.08) 100%) !important;
  border: 1px solid rgba(255, 105, 180, 0.2) !important;
}

.change-management .card-icon {
  background: linear-gradient(135deg, #FF69B4, #FFA500);
  color: #FFF;
  box-shadow: 0 4px 15px rgba(255, 105, 180, 0.3);
}

.version-management {
  background: linear-gradient(135deg, 
    rgba(70, 130, 180, 0.08) 0%, 
    rgba(50, 205, 50, 0.08) 100%) !important;
  border: 1px solid rgba(70, 130, 180, 0.2) !important;
}

.version-management .card-icon {
  background: linear-gradient(135deg, #4682B4, #32CD32);
  color: #FFF;
  box-shadow: 0 4px 15px rgba(70, 130, 180, 0.3);
}

.development-standards {
  background: linear-gradient(135deg, 
    rgba(128, 0, 128, 0.08) 0%, 
    rgba(255, 105, 180, 0.08) 100%) !important;
  border: 1px solid rgba(128, 0, 128, 0.2) !important;
}

.development-standards .card-icon {
  background: linear-gradient(135deg, #800080, #FF69B4);
  color: #FFF;
  box-shadow: 0 4px 15px rgba(128, 0, 128, 0.3);
}

.system-settings {
  background: linear-gradient(135deg, 
    rgba(75, 0, 130, 0.08) 0%, 
    rgba(138, 43, 226, 0.08) 100%) !important;
  border: 1px solid rgba(75, 0, 130, 0.2) !important;
}

.system-settings .card-icon {
  background: linear-gradient(135deg, #4B0082, #8A2BE2);
  color: #FFF;
  box-shadow: 0 4px 15px rgba(75, 0, 130, 0.3);
}

/* 使用提示区域样式 */
.tips-section {
  background: linear-gradient(135deg, #F9FAFB, #F3F4F6);
  padding: 24px;
  border-radius: 16px;
  border: 1px solid #E5E7EB;
  margin-top: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.tips-title {
  font-size: 20px;
  font-weight: 700;
  color: #1F2937;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #E5E7EB;
  background: linear-gradient(135deg, #4F46E5, #7C3AED);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tip-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: white;
  border-radius: 10px;
  font-size: 14px;
  color: #4B5563;
  border: 1px solid #E5E7EB;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  overflow: hidden;
}

.tip-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(135deg, #4F46E5, #7C3AED);
  transform: scaleY(0);
  transform-origin: bottom;
  transition: transform 0.3s ease;
}

.tip-item:hover {
  transform: scale(1.02);
  border-color: #4F46E5;
  box-shadow: 0 6px 16px rgba(79, 70, 229, 0.12);
}

.tip-item:hover::before {
  transform: scaleY(1);
}

.tip-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.tip-icon {
  font-size: 18px;
  flex-shrink: 0;
}

.tip-text {
  line-height: 1.5;
}

.tip-action {
  font-size: 16px;
  color: #7B68EE;
  opacity: 0.6;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.tip-item:hover .tip-action {
  opacity: 1;
  transform: translateX(3px);
}

/* 动画效果 */
@keyframes shimmer {
  0% {
    background-position: 100% 100%;
  }
  100% {
    background-position: -100% -100%;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .quick-access-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .dashboard-cards {
    gap: 16px;
    padding: 8px;
  }
  
  .section-title {
    font-size: 20px;
  }
  
  .tips-title {
    font-size: 18px;
  }
  
  .card-content {
    padding: 20px;
  }
  
  .module-card {
    height: 160px;
    min-width: 100px;
  }
  
  .module-card:hover {
    transform: translateY(-3px) scale(1.02);
  }
  
  .quick-action-card {
    padding: 24px 20px;
  }
  
  .quick-action-card .card-icon {
    font-size: 36px;
  }
  
  .tip-item {
    font-size: 13px;
    padding: 10px 14px;
  }
  
  .tip-icon {
    font-size: 16px;
  }
  
  .tip-action {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .card-content {
    flex-direction: column;
    text-align: center;
  }
  
  .card-link .card-icon {
    margin-right: 0;
    margin-bottom: 16px;
  }
  
  .card-action {
    margin-top: 12px;
  }
}
</style>