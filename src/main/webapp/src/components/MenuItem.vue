<template>
  <div>
    <!-- 目录类型 -->
    <el-submenu
      v-if="menu.menuType === 1 && menu.children && menu.children.length > 0"
      :index="menu.path || String(menu.id)"
    >
      <template slot="title">
        <i :class="menu.icon || 'el-icon-menu'"></i>
        <span>{{ menu.menuName }}</span>
      </template>
      <template v-for="child in menu.children" :key="child.id">
        <menu-item
          v-if="child.menuType === 1"
          :menu="child"
        />
        <el-menu-item
          v-else-if="child.menuType === 2"
          :index="child.path || String(child.id)"
        >
          <i :class="child.icon || 'el-icon-document'"></i>
          <span slot="title">{{ child.menuName }}</span>
        </el-menu-item>
      </template>
    </el-submenu>

    <!-- 菜单类型 -->
    <el-menu-item
      v-else-if="menu.menuType === 2"
      :index="menu.path || String(menu.id)"
    >
      <i :class="menu.icon || 'el-icon-document'"></i>
      <span slot="title">{{ menu.menuName }}</span>
    </el-menu-item>
  </div>
</template>

<script>
export default {
  name: 'MenuItem',
  props: {
    menu: {
      type: Object,
      required: true
    }
  }
};
</script>

