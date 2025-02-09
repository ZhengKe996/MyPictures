<template>
  <div class="w-full space-y-6 animated animated-duration-500 animated-fade-in">
    <div
      class="bg-white rounded-xl border border-gray-100 shadow-xs hover:shadow-sm transition-all duration-500"
    >
      <div class="p-6 border-b border-gray-100">
        <div class="flex items-center justify-between">
          <h3 class="text-lg font-semibold text-gray-900">空间图片用户分析</h3>
          <div class="flex items-center gap-4">
            <a-segmented
              v-model:value="timeDimension"
              :options="timeDimensionOptions"
              class="bg-gray-50/80"
            />
            <div class="relative">
              <a-input-search
                v-model:value="searchUserId"
                placeholder="请输入用户 id"
                enter-button="搜索"
                @search="doSearch"
                class="w-48"
              />
            </div>
            <div
              class="bg-emerald-50/70 rounded-full p-2 transition-colors duration-500"
            >
              <i class="i-tabler:users text-emerald-500 size-5"></i>
            </div>
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
              class="w-8 h-8 border-3 border-emerald-600 border-t-transparent rounded-full animate-spin"
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
import { getSpaceUserAnalyze } from "@/services";
import type { UserAnalyzeType, AnalyzeProps } from "@/config";
import { Message } from "@/lib/Message";
import { baseColors } from "./config";

interface Props extends AnalyzeProps {
  timeDimension?: string;
  userId?: string;
}

const props = withDefaults(defineProps<Props>(), {
  queryAll: false,
  queryPublic: false,
});

// 时间维度选项
const timeDimension = ref<"day" | "week" | "month">("day");
// 分段选择器组件的选项
const timeDimensionOptions = [
  {
    label: "日",
    value: "day",
  },
  {
    label: "周",
    value: "week",
  },
  {
    label: "月",
    value: "month",
  },
];
// 用户选项
const userId = ref<string>();
const searchUserId = ref("");
const errorMessage = ref("");
const doSearch = (value: string) => {
  userId.value = value;
};

// 图表数据
const dataList = ref<UserAnalyzeType[]>([]);
// 加载状态
const loading = ref(true);

// 获取数据
const fetchData = async () => {
  loading.value = true;
  // 转换搜索参数
  const {
    data: responseData,
    code,
    message,
  } = await getSpaceUserAnalyze({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId,
    timeDimension: timeDimension.value,
    userId: userId.value,
  });
  if (code === 0 && responseData) {
    dataList.value = responseData;
  } else {
    Message.error("获取数据失败，" + message);
  }
  loading.value = false;
};

/**
 * 监听变量，参数改变时触发数据的重新加载
 */
watchEffect(() => {
  fetchData();
});

// 图表选项
const options = computed(() => {
  const periods = dataList.value.map((item) => item.period); // 时间区间
  const counts = dataList.value.map((item) => item.count); // 上传数量

  return {
    tooltip: {
      trigger: "axis",
      backgroundColor: "rgba(255, 255, 255, 0.9)",
      borderWidth: 1,
      borderColor: "#E5E7EB",
      textStyle: { color: "#374151" },
      padding: [8, 12],
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
      data: periods,
      axisLine: { lineStyle: { color: "#E5E7EB" } },
      axisTick: { show: false },
      axisLabel: { color: "#6B7280" },
    },
    yAxis: {
      type: "value",
      name: "上传数量",
      nameTextStyle: { color: "#6B7280" },
      axisLine: { show: true, lineStyle: { color: baseColors[4].start } },
      axisLabel: { color: "#6B7280" },
      splitLine: { lineStyle: { color: "#F3F4F6", type: "dashed" } },
    },
    series: [
      {
        name: "上传数量",
        type: "line",
        data: counts,
        smooth: true,
        symbolSize: 8,
        lineStyle: {
          width: 3,
          color: baseColors[4].start,
        },
        itemStyle: {
          color: baseColors[4].start,
        },
        areaStyle: {
          color: {
            type: "linear",
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: baseColors[4].start + "40" },
              { offset: 1, color: baseColors[4].end + "00" },
            ],
          },
        },
        emphasis: {
          focus: "series",
          scale: true,
        },
        animation: true,
        animationDuration: 1000,
        animationEasing: "cubicOut",
        animationDelay: (idx: number) => idx * 100,
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
