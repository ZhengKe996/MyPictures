<template>
  <div
    class="flex items-center justify-center space-x-4 text-sm text-gray-600 dark:text-gray-300 transition-all duration-300 ease-in-out group hover:text-custom-gradient-end"
  >
    <a
      href="https://beian.miit.gov.cn"
      target="_blank"
      class="relative group flex items-center transition-all duration-300"
    >
      <span class="transition-transform duration-300 group-hover:translate-x-1">
        浙ICP备2021022773号
      </span>
      <span
        class="absolute bottom-0 left-0 w-0 h-0.5 bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end transition-all duration-300 group-hover:w-full"
      ></span>
    </a>
    <span>©2022-PRESENT ZhengKe</span>
  </div>
</template>

<script setup lang="ts">
// 可以在这里添加任何必要的逻辑
</script>

<style scoped>
@keyframes softPulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.02);
  }
}

a:hover {
  animation: softPulse 0.5s ease-in-out;
}
</style>
