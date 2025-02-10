<template>
  <BaseAnalyze
    title="空间图片用户分析"
    icon="users"
    icon-color="emerald"
    :loading="loading"
    :error-message="errorMessage"
  >
    <template #header-actions>
      <a-segmented
        v-model:value="timeDimension"
        :options="timeDimensionOptions"
        class="bg-gray-50/80"
      />
      <div class="relative">
        <SearchInput
          v-model="searchUserId"
          placeholder="请输入用户 id"
          @search="() => doSearch(searchUserId)"
          class="w-48"
        />
      </div>
    </template>

    <template #default>
      <v-chart
        :option="options"
        :style="{ height: '100%', width: '100%' }"
        :animation="true"
      />
    </template>
  </BaseAnalyze>
</template>

<script setup lang="ts">
import { computed, ref, watchEffect } from "vue";
import VChart from "vue-echarts";
import "echarts";
import type { EChartsOption } from "echarts";
import { getSpaceUserAnalyze } from "@/services";
import type { UserAnalyzeType, AnalyzeProps } from "@/config";
import { Message } from "@/lib/Message";
import { baseColors } from "./config";
import SearchInput from "@/lib/SearchInput";
import BaseAnalyze from "./BaseAnalyze.vue";
const props = withDefaults(defineProps<AnalyzeProps>(), {
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
const options = computed<EChartsOption>(() => {
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
@import "./styles/analyze.css";

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
