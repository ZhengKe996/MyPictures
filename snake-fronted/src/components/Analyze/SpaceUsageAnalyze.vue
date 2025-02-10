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
              {{ formatSize(data?.usedSize) }}
            </div>
            <div class="text-sm text-gray-500">
              总容量: {{ data?.maxSize ? formatSize(data.maxSize) : "无限制" }}
            </div>
          </div>

          <div class="relative w-48 h-48 mx-auto">
            <RoundProgress
              :progress="data?.sizeUsageRatio ?? 0"
              :size="192"
              :stroke-width="10"
              :start-color="baseColors[0].start"
              :end-color="baseColors[0].end"
              show-decoration
            >
              <template #center="{ progress }">
                <span class="text-2xl font-semibold text-gray-700">
                  {{ progress }}%
                </span>
              </template>
            </RoundProgress>
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
              {{ data?.usedCount }}
            </div>
            <div class="text-sm text-gray-500">
              总数量: {{ data?.maxCount ?? "无限制" }}
            </div>
          </div>

          <div class="relative w-48 h-48 mx-auto">
            <RoundProgress
              :progress="data?.countUsageRatio ?? 0"
              :size="192"
              :stroke-width="10"
              :start-color="baseColors[2].start"
              :end-color="baseColors[2].end"
              show-decoration
            >
              <template #center="{ progress }">
                <span class="text-2xl font-semibold text-gray-700">
                  {{ progress }}%
                </span>
              </template>
            </RoundProgress>
          </div>
        </div>
      </template>
    </BaseAnalyze>
  </div>
</template>

<script setup lang="ts">
import { watchEffect } from "vue";
import { formatSize } from "@/utils";
import { getSpaceUsageAnalyze } from "@/services";
import type { UsageAnalyzeeType, AnalyzeProps } from "@/config";
import { baseColors } from "./config";
import BaseAnalyze from "./BaseAnalyze.vue";
import RoundProgress from "@/lib/RoundProgress/RoundProgress.vue";
import { useAnalyzeData } from "./hooks/useAnalyzeData";

const props = withDefaults(defineProps<AnalyzeProps>(), {
  queryAll: false,
  queryPublic: false,
});

// 使用抽取的数据加载hook
const { data, loading, errorMessage, fetchData } =
  useAnalyzeData<UsageAnalyzeeType>({
    fetchFn: getSpaceUsageAnalyze, // 使用对象形式传入
  });

// 监听参数变化，重新加载数据
watchEffect(() => {
  fetchData({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId,
  });
});
</script>

<style scoped>
@import "./styles/analyze.css";
</style>
