<template>
  <BaseAnalyze
    title="空间图片用户分析"
    icon="users"
    icon-color="emerald"
    :loading="loading"
    :error-message="errorMessage"
  >
    <template #header-actions>
      <div class="flex flex-col gap-3">
        <div class="w-full">
          <SearchInput
            v-model="searchUserId"
            placeholder="请输入用户 id"
            @search="() => doSearch(searchUserId)"
            class="w-full"
          />
        </div>
        <div class="w-full">
          <Segment
            v-model:value="timeDimension"
            :options="timeDimensionOptions"
            :block="true"
            class="bg-gray-50/80 text-sm shadow-sm duration-200"
          />
        </div>
      </div>
    </template>

    <template #default>
      <div ref="chartContainer" class="chart-container">
        <v-chart
          ref="chartRef"
          :option="options"
          :autoresize="true"
          :animation="true"
        />
      </div>
    </template>
  </BaseAnalyze>
</template>

<script setup lang="ts">
import { computed, ref, watchEffect } from "vue";
import { useDebounceFn, useEventListener } from "@vueuse/core";
import VChart from "vue-echarts";
import "echarts";
import { getSpaceUserAnalyze } from "@/services";
import type { UserAnalyzeType, AnalyzeProps } from "@/config";
import { baseColors } from "./config";
import SearchInput from "@/lib/SearchInput";
import BaseAnalyze from "./BaseAnalyze.vue";
import Segment from "@/lib/Segment/Segment.vue";
import { useAnalyzeData } from "./hooks/useAnalyzeData";
import { getBaseChartConfig, createGradient } from "./utils/chartUtils";

const props = withDefaults(defineProps<AnalyzeProps>(), {
  queryAll: false,
  queryPublic: false,
});

// 时间维度选项
const timeDimension = ref<"day" | "week" | "month">("day");
const timeDimensionOptions = [
  { label: "日", value: "day" },
  { label: "周", value: "week" },
  { label: "月", value: "month" },
];

// 用户选项
const userId = ref<string>();
const searchUserId = ref("");
const doSearch = (value: string) => {
  userId.value = value;
};

// 使用抽取的数据加载hook
const { data, loading, errorMessage, fetchData } = useAnalyzeData<
  UserAnalyzeType[]
>({
  fetchFn: getSpaceUserAnalyze,
});

// 监听参数变化，重新加载数据
watchEffect(() => {
  fetchData({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId,
    timeDimension: timeDimension.value,
    userId: userId.value,
  });
});

// 图表相关引用
const chartRef = ref();
const chartContainer = ref();

// 使用 VueUse 的防抖函数
const updateChartSize = useDebounceFn(() => {
  if (chartRef.value?.chart) {
    chartRef.value.chart.resize();
  }
}, 200);

// 使用 useEventListener 替代手动事件监听
useEventListener(window, "resize", updateChartSize);

// 优化图表配置
const options = computed(() => {
  if (!data.value?.length) return getBaseChartConfig();

  const periods = data.value.map((item) => item.period);
  const counts = data.value.map((item) => item.count);

  // 根据容器宽度动态计算标签旋转角度和间隔
  const containerWidth = chartContainer.value?.offsetWidth || 600;
  const rotateLabels = periods.length > 8 || containerWidth < 500;

  return {
    ...getBaseChartConfig(),
    grid: {
      top: "10%",
      left: "3%",
      right: "4%",
      bottom: rotateLabels ? "15%" : "10%",
      containLabel: true,
    },
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "cross",
        label: {
          backgroundColor: baseColors[4].start,
        },
      },
    },
    xAxis: {
      ...getBaseChartConfig().xAxis,
      data: periods,
      axisLabel: {
        interval: rotateLabels ? "auto" : 0,
        rotate: rotateLabels ? 45 : 0,
        formatter: (value: string) => {
          const maxLength = rotateLabels ? 8 : 12;
          return value.length > maxLength
            ? value.substring(0, maxLength) + "..."
            : value;
        },
      },
    },
    yAxis: {
      ...getBaseChartConfig().yAxis,
      name: "上传数量",
      axisLine: { show: true, lineStyle: { color: baseColors[4].start } },
      splitLine: {
        show: true,
        lineStyle: {
          color: "rgba(229, 231, 235, 0.5)",
          type: "dashed",
        },
      },
    },
    series: [
      {
        name: "上传数量",
        type: "line",
        data: counts,
        smooth: true,
        symbolSize: (_: number, params: any) => {
          return params.dataIndex === 0 ||
            params.dataIndex === counts.length - 1
            ? 8
            : 6;
        },
        lineStyle: {
          width: 3,
          color: baseColors[4].start,
        },
        itemStyle: {
          color: baseColors[4].start,
          borderWidth: 2,
          borderColor: "#fff",
        },
        areaStyle: {
          color: createGradient(
            {
              start: baseColors[4].start,
              end: baseColors[4].end,
            },
            0.25
          ),
        },
        emphasis: {
          focus: "series",
          scale: false,
          itemStyle: {
            shadowBlur: 10,
            shadowColor: "rgba(0,0,0,0.1)",
          },
        },
        animation: true,
        animationDuration: 1000,
        animationEasing: "cubicInOut",
      },
    ],
  };
});
</script>

<style scoped>
@import "./styles/analyze.css";

.chart-container {
  width: 100%;
  height: calc(100% - 1rem);
  min-height: 300px;
}

:deep(.echarts) {
  width: 100% !important;
  height: 100% !important;
  transition: all 0.3s ease-in-out;
}

@media (max-width: 640px) {
  .chart-container {
    min-height: 250px;
  }
}
</style>
