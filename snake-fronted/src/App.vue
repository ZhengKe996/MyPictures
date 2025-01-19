<script setup lang="ts">
import BasicLayout from "@/layouts/BasicLayout.vue";
import { LayoutMenu } from "@/config";
import { useRoute } from "vue-router";
const route = useRoute();
</script>

<template>
  <template v-if="route.meta?.layout === LayoutMenu.BasicLayout">
    <BasicLayout />
  </template>
  <template v-else>
    <RouterView />
  </template>
</template>
<style scoped></style>
