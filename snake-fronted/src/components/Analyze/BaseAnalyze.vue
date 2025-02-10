<template>
  <div class="w-full space-y-6 animated animated-duration-500 animated-fade-in">
    <div
      class="bg-white rounded-xl border border-gray-100 shadow-xs hover:shadow-sm transition-all duration-500"
    >
      <!-- Header -->
      <div class="p-6 border-b border-gray-100">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <h3 class="text-lg font-semibold text-gray-900">
              {{ title }}
            </h3>
            <slot name="title-suffix" />
          </div>
          <div class="flex items-center gap-4">
            <slot name="header-actions" />
            <div
              :class="[
                `bg-${iconColor}-50/70 rounded-full p-2 transition-colors duration-500`,
              ]"
            >
              <i
                :class="[`i-tabler-${icon}`, `text-${iconColor}-500 size-5`]"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- Content -->
      <div class="relative p-6">
        <!-- Loading Overlay -->
        <div
          v-if="loading"
          class="absolute inset-0 bg-white/80 backdrop-blur-sm z-10 flex items-center justify-center"
        >
          <div class="flex flex-col items-center gap-3">
            <div
              :class="[
                'w-8 h-8 border-3 border-t-transparent rounded-full animate-spin',
                `border-${iconColor}-600`,
              ]"
            />
            <span class="text-sm text-gray-500">加载中...</span>
          </div>
        </div>

        <!-- Chart Container -->
        <div
          class="h-[320px] transition-opacity duration-300"
          :class="{ 'opacity-50': loading }"
        >
          <slot />
        </div>
      </div>
    </div>

    <!-- Status Message -->
    <TransitionGroup name="message" tag="div">
      <div
        v-if="errorMessage"
        :key="errorMessage"
        class="p-4 rounded-lg border bg-error-50 text-error-700 border-error-200 flex items-center gap-3 animated animated-faster animated-fade-in"
      >
        <i class="i-tabler-alert-circle size-5 text-error-400" />
        <span class="font-medium">{{ errorMessage }}</span>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string;
  icon: string;
  iconColor: string;
  loading: boolean;
  errorMessage?: string;
}>();
</script>

<style scoped>
@import "./styles/analyze.css";

.message-enter-active,
.message-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.message-enter-from,
.message-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

:deep(.echarts) {
  border-radius: 0.75rem;
}

/* 更新卡片阴影自定义类 */
:root {
  --shadow-xs: 0 1px 2px rgba(0, 0, 0, 0.05);
  --shadow-sm: 0 1px 3px rgba(0, 0, 0, 0.1), 0 1px 2px -1px rgba(0, 0, 0, 0.1);
}

.shadow-xs {
  box-shadow: var(--shadow-xs);
}

.hover\:shadow-sm:hover {
  box-shadow: var(--shadow-sm);
}
</style>
