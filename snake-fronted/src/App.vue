<script setup lang="ts">
import zhCN from "ant-design-vue/es/locale/zh_CN";
const locale = zhCN;

import BasicLayout from "@/layouts/BasicLayout.vue";
import { LayoutMenu } from "@/config";
import { useRoute } from "vue-router";
const route = useRoute();
</script>

<template>
  <a-config-provider :locale="locale">
    <template v-if="route.meta?.layout === LayoutMenu.BasicLayout">
      <BasicLayout />
    </template>
    <template v-else>
      <RouterView />
    </template>
  </a-config-provider>
</template>
<style scoped></style>
