import eslintConfigPrettier from 'eslint-config-prettier';
import pluginVue from 'eslint-plugin-vue';
import { defineConfigWithVueTs, vueTsConfigs } from '@vue/eslint-config-typescript';

export default defineConfigWithVueTs(
  {
    ignores: ['node_modules/**', '../resources/static/**', 'dist/**'],
  },
  pluginVue.configs['flat/recommended'],
  vueTsConfigs.recommended,
  {
    rules: {
      // 允许单词组件名（Login、Dashboard 等页面组件）
      'vue/multi-word-component-names': 'off',
    },
  },
  // 关闭与 Prettier 冲突的格式规则：格式统一由 Prettier 负责
  eslintConfigPrettier,
);
