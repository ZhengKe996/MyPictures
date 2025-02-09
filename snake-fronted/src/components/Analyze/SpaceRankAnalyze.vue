<template>
  <div class="w-full space-y-6 animated animated-duration-500 animated-fade-in">
    <div
      class="bg-white rounded-xl border border-gray-100 shadow-xs hover:shadow-sm transition-all duration-500"
    >
      <div class="p-6 border-b border-gray-100">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <h3 class="text-lg font-semibold text-gray-900">
              空间使用排行分析
            </h3>
            <span class="text-sm text-gray-500">Top 10</span>
          </div>
          <div
            class="bg-amber-50/70 rounded-full p-2 transition-colors duration-500"
          >
            <i class="i-tabler:trophy text-amber-500 size-5"></i>
          </div>
        </div>
      </div>

      <div class="relative p-6">
        <!-- Loading Overlay -->
        <div
          v-if="loading"
          class="absolute inset-0 bg-white/80 backdrop-blur-sm z-10 flex items-center justify-center"
        >
          <div class="flex flex-col items-center gap-3">
            <div
              class="w-8 h-8 border-3 border-amber-600 border-t-transparent rounded-full animate-spin"
            ></div>
            <span class="text-sm text-gray-500">加载中...</span>
          </div>
        </div>

        <!-- Chart Container -->
        <div
          class="h-[320px] transition-opacity duration-300"
          :class="{ 'opacity-50': loading }"
        >
          <v-chart
            :option="options"
            :style="{ height: '100%', width: '100%' }"
            :animation="true"
          />
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
        <i class="i-tabler:alert-circle size-5 text-error-400" />
        <span class="font-medium">{{ errorMessage }}</span>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup lang="ts">
import VChart from "vue-echarts";
import "echarts";
import { computed, ref, watchEffect } from "vue";
import { getSpaceRankAnalyze } from "@/services";
import type { SpaceType } from "@/config";
import { baseColors } from "./config";

// 图表数据
const dataList = ref<SpaceType[]>([]);
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
    } = await getSpaceRankAnalyze({
      topN: 10,
    });

    if (code === 0 && responseData) {
      dataList.value = responseData;
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

// 图表选项
const options = computed(() => {
  const spaceNames = dataList.value.map((item) => item.spaceName);
  const usageData = dataList.value.map((item, index) => ({
    value: parseFloat(((item.totalSize ?? 0) / (1024 * 1024)).toFixed(2)),
    itemStyle: {
      color: {
        type: "linear",
        x: 0,
        y: 0,
        x2: 0,
        y2: 1,
        colorStops: [
          {
            offset: 0,
            color: index < 3 ? baseColors[index].start : baseColors[3].start,
          },
          {
            offset: 1,
            color: index < 3 ? baseColors[index].end : baseColors[3].end,
          },
        ],
      },
    },
  }));

  return {
    tooltip: {
      trigger: "axis",
      backgroundColor: "rgba(255, 255, 255, 0.9)",
      borderWidth: 1,
      borderColor: "#E5E7EB",
      textStyle: { color: "#374151" },
      padding: [8, 12],
      formatter: (params: any) => {
        const dataIndex = params[0].dataIndex;
        const value = params[0].value;
        return `${spaceNames[dataIndex]}<br/>使用量: ${value} MB<br/>排名: ${
          dataIndex + 1
        }`;
      },
    },
    grid: {
      top: "5%",
      left: "3%",
      right: "4%",
      bottom: "15%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: spaceNames,
      axisLine: { lineStyle: { color: "#E5E7EB" } },
      axisTick: { show: false },
      axisLabel: {
        color: "#6B7280",
        interval: 0,
        rotate: 45,
        formatter: (value: string) => {
          return value.length > 10 ? value.substring(0, 10) + "..." : value;
        },
      },
    },
    yAxis: {
      type: "value",
      name: "空间使用量 (MB)",
      nameTextStyle: { color: "#6B7280" },
      axisLine: { show: true, lineStyle: { color: baseColors[0].start } },
      axisLabel: { color: "#6B7280" },
      splitLine: { lineStyle: { color: "#F3F4F6", type: "dashed" } },
    },
    series: [
      {
        name: "空间使用量",
        type: "bar",
        data: usageData,
        barMaxWidth: 40,
        itemStyle: {
          borderRadius: [4, 4, 0, 0],
        },
        emphasis: {
          focus: "series",
          scale: true,
          scaleSize: 5,
        },
        label: {
          show: true,
          position: "top",
          fontSize: 12,
          color: "#6B7280",
          formatter: "{c} MB",
        },
        animation: true,
        animationDelay: (idx: number) => idx * 100,
        animationDelayUpdate: (idx: number) => idx * 100,
      },
    ],
  };
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
