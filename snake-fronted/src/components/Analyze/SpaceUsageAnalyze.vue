<template>
  <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
    <BaseAnalyze
      title="存储空间"
      icon="database"
      icon-color="indigo"
      :loading="loading"
      :error-message="errorMessage"
    >
      <template #default>
        <div class="text-center space-y-6">
          <div class="flex flex-col items-center justify-center gap-2">
            <div
              class="text-3xl font-bold bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent"
            >
              {{ formatSize(data.usedSize) }}
            </div>
            <div class="text-sm text-gray-500">
              总容量: {{ data.maxSize ? formatSize(data.maxSize) : "无限制" }}
            </div>
          </div>

          <div class="relative w-48 h-48 mx-auto">
            <a-progress
              type="dashboard"
              :percent="data.sizeUsageRatio ?? 0"
              :stroke-width="10"
              :stroke-color="{
                '0%': baseColors[0].start,
                '100%': baseColors[0].end,
              }"
            />
          </div>
        </div>
      </template>
    </BaseAnalyze>

    <BaseAnalyze
      title="图片数量"
      icon="photo"
      icon-color="purple"
      :loading="loading"
      :error-message="errorMessage"
    >
      <template #default>
        <div class="text-center space-y-6">
          <div class="flex flex-col items-center justify-center gap-2">
            <div
              class="text-3xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent"
            >
              {{ data.usedCount }}
            </div>
            <div class="text-sm text-gray-500">
              总数量: {{ data.maxCount ?? "无限制" }}
            </div>
          </div>

          <div class="relative w-48 h-48 mx-auto">
            <a-progress
              type="dashboard"
              :percent="data.countUsageRatio ?? 0"
              :stroke-width="10"
              :stroke-color="{
                '0%': baseColors[2].start,
                '100%': baseColors[2].end,
              }"
            />
          </div>
        </div>
      </template>
    </BaseAnalyze>
  </div>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import { formatSize } from "@/utils";
import { getSpaceUsageAnalyze } from "@/services";
import type { UsageAnalyzeeType, AnalyzeProps } from "@/config";
import { baseColors } from "./config";
import BaseAnalyze from "./BaseAnalyze.vue";

const props = withDefaults(defineProps<AnalyzeProps>(), {
  queryAll: false,
  queryPublic: false,
});

// 图表数据
const data = ref<UsageAnalyzeeType>({});
// 加载状态
const loading = ref(true);
const errorMessage = ref("");

// 获取数据
const fetchData = async () => {
  loading.value = true;
  errorMessage.value = "";

  try {
    const {
      data: responseData,
      code,
      message,
    } = await getSpaceUsageAnalyze({
      queryAll: props.queryAll,
      queryPublic: props.queryPublic,
      spaceId: props.spaceId,
    });

    if (code === 0 && responseData) {
      data.value = responseData;
    } else {
      errorMessage.value = "获取数据失败，" + message;
    }
  } catch (error) {
    errorMessage.value = "请求发生错误，请稍后重试";
  } finally {
    loading.value = false;
  }
};

/**
 * 监听变量，参数改变时触发数据的重新加载
 */
watchEffect(() => {
  fetchData();
});
</script>

<style scoped>
.message-enter-active,
.message-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.message-enter-from,
.message-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

:deep(.ant-progress-text) {
  font-size: 1.5rem !important;
  font-weight: 600 !important;
  color: #374151 !important;
  font-family: ui-monospace, monospace !important;
}

:deep(.ant-progress-circle-path) {
  transition: stroke-dashoffset 1s cubic-bezier(0.4, 0, 0.2, 1),
    stroke 0.5s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

:deep(.ant-progress-circle-trail) {
  stroke: rgb(243 244 246) !important;
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
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
