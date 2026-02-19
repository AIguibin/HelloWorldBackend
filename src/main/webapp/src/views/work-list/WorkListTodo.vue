<template>
  <div class="work-list-todo-page">
    <el-tabs v-model="activeTab" type="card">
      <!-- 待办tab -->
      <el-tab-pane label="待办" name="todo">
        <approval-todo-list />
      </el-tab-pane>
      <!-- 已办tab -->
      <el-tab-pane label="已办" name="done">
        <approval-done-list />
      </el-tab-pane>
      <!-- 已结tab -->
      <el-tab-pane label="已结" name="closed">
        <approval-closed-list />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
// 导入子组件
import ApprovalTodoList from './ApprovalTodoList.vue';
import ApprovalDoneList from './ApprovalDoneList.vue';
import ApprovalClosedList from './ApprovalClosedList.vue';

export default {
  name: 'WorkListTodo',
  components: {
    ApprovalTodoList,
    ApprovalDoneList,
    ApprovalClosedList
  },
  data() {
    return {
      activeTab: 'todo' // 默认选中待办tab
    };
  },
  mounted() {
    // 页面加载时的初始化逻辑
  }
};
</script>

<style scoped>
/* Page container */
.work-list-todo-page {
  width: 100%;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eaf6 100%);
  min-height: calc(100vh - 60px);
}

/* Tab styling */
.work-list-todo-page ::v-deep .el-tabs__header {
  margin-bottom: 20px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12px;
  padding: 16px 20px;
  box-shadow: 0 4px 16px rgba(123, 104, 238, 0.08);
  border: 1px solid rgba(123, 104, 238, 0.1);
}

.work-list-todo-page ::v-deep .el-tabs__nav-wrap::after {
  height: 0;
}

.work-list-todo-page ::v-deep .el-tabs__nav {
  border: none;
  background: transparent;
}

.work-list-todo-page ::v-deep .el-tabs__item {
  font-size: 15px;
  font-weight: 600;
  padding: 12px 24px;
  border-radius: 8px;
  color: #606266;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eaf6 100%);
}

.work-list-todo-page ::v-deep .el-tabs__item:hover {
  color: #7B68EE;
  background: linear-gradient(135deg, #f5f7ff 0%, #e8eaf6 100%);
  border-color: #9370DB;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(123, 104, 238, 0.15);
}

.work-list-todo-page ::v-deep .el-tabs__item.is-active {
  color: #ffffff;
  background: linear-gradient(135deg, #7B68EE 0%, #9370DB 100%);
  border-color: #7B68EE;
  box-shadow: 0 4px 12px rgba(123, 104, 238, 0.3);
  transform: translateY(-2px);
}

.work-list-todo-page ::v-deep .el-tabs__active-bar {
  background: transparent;
  height: 0;
}

/* Tab content styling */
.work-list-todo-page ::v-deep .el-tabs__content {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(123, 104, 238, 0.08);
  padding: 0;
  border: 1px solid rgba(123, 104, 238, 0.1);
  overflow: hidden;
}

.work-list-todo-page ::v-deep .el-tab-pane {
  padding: 20px;
}

/* Animation */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.work-list-todo-page ::v-deep .el-tab-pane {
  animation: fadeIn 0.4s ease-out;
}

/* Responsive design */
@media screen and (max-width: 1200px) {
  .work-list-todo-page {
    padding: 16px;
  }

  .work-list-todo-page ::v-deep .el-tabs__header {
    padding: 14px 16px;
  }

  .work-list-todo-page ::v-deep .el-tabs__item {
    font-size: 14px;
    padding: 10px 20px;
  }
}

@media screen and (max-width: 768px) {
  .work-list-todo-page {
    padding: 12px;
  }

  .work-list-todo-page ::v-deep .el-tabs__header {
    padding: 12px;
    flex-direction: column;
    gap: 12px;
  }

  .work-list-todo-page ::v-deep .el-tabs__nav {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .work-list-todo-page ::v-deep .el-tabs__item {
    width: 100%;
    text-align: center;
    padding: 10px 16px;
    font-size: 13px;
  }

  .work-list-todo-page ::v-deep .el-tab-pane {
    padding: 16px;
  }
}

@media screen and (max-width: 480px) {
  .work-list-todo-page {
    padding: 8px;
  }

  .work-list-todo-page ::v-deep .el-tabs__header {
    padding: 8px;
  }

  .work-list-todo-page ::v-deep .el-tabs__item {
    font-size: 12px;
    padding: 8px 12px;
  }

  .work-list-todo-page ::v-deep .el-tab-pane {
    padding: 12px;
  }
}
</style>