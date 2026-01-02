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
.work-list-todo-page {
  padding: 16px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 120px);
}

/* 调整tab样式 */
.work-list-todo-page ::v-deep .el-tabs__header {
  margin-bottom: 16px;
}

.work-list-todo-page ::v-deep .el-tabs__nav-wrap::after {
  height: 0;
}

.work-list-todo-page ::v-deep .el-tabs__item {
  font-size: 16px;
  padding: 10px 20px;
}

.work-list-todo-page ::v-deep .el-tabs__item.is-active {
  color: #409EFF;
}

.work-list-todo-page ::v-deep .el-tabs__active-bar {
  background-color: #409EFF;
}

/* 调整tab内容区域样式 */
.work-list-todo-page ::v-deep .el-tabs__content {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 16px;
}
</style>