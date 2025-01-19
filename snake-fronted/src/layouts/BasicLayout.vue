<template>
  <a-layout class="min-h-[100vh]">
    <a-layout-header class="flex justify-center">
      <GlobalHeader></GlobalHeader>
    </a-layout-header>
    <a-layout-content><router-view></router-view></a-layout-content>
    <a-layout-footer class="flex justify-center">
      <GlobalFooter></GlobalFooter>
    </a-layout-footer>
  </a-layout>
</template>

<script setup lang="ts">
import GlobalFooter from "@/components/GlobalFooter";
import GlobalHeader from "@/components/GlobalHeader";
</script>
