<template>
  <BaseAnalyze
    title="空间使用排行分析"
    icon="trophy"
    icon-color="amber"
    :loading="loading"
    :error-message="errorMessage"
  >
    <template #title-suffix>
      <span class="text-sm text-gray-500">Top 10</span>
    </template>

    <template #default>
      <v-chart
        :option="options"
        :style="{ height: '100%', width: '100%' }"
        :animation="true"
        autoresize
      />
    </template>
  </BaseAnalyze>
</template>

<script setup lang="ts">
import VChart from "vue-echarts";
import "echarts";
import { computed } from "vue";
import { getSpaceRankAnalyze } from "@/services";
import type { SpaceType } from "@/config";
import { baseColors } from "./config";
import BaseAnalyze from "./BaseAnalyze.vue";
import { useAnalyzeData } from "./hooks/useAnalyzeData";
import {
  getBaseChartConfig,
  createGradient,
  getBaseAnimationConfig,
} from "./utils/chartUtils";

// 使用抽取的数据加载hook
const { data, loading, errorMessage, fetchData } = useAnalyzeData<SpaceType[]>({
  fetchFn: getSpaceRankAnalyze,
});

// 初始化加载数据
fetchData({ topN: 10 });

// 图表选项
const options = computed(() => {
  if (!data.value?.length) return getBaseChartConfig();

  const spaceNames = data.value.map((item) => item.spaceName);
  const usageData = data.value.map((item, index) => ({
    value: parseFloat(((item.totalSize ?? 0) / (1024 * 1024)).toFixed(2)),
    itemStyle: {
      color: createGradient({
        start: index < 3 ? baseColors[index].start : baseColors[3].start,
        end: index < 3 ? baseColors[index].end : baseColors[3].end,
      }),
    },
  }));

  return {
    ...getBaseChartConfig(),
    tooltip: {
      ...getBaseChartConfig().tooltip,
      trigger: "axis",
      axisPointer: {
        type: "shadow",
      },
      formatter: (params: any[] | any) => {
        const param = Array.isArray(params) ? params[0] : params;
        if (!param) return "";

        const index = param.dataIndex;
        const value = param.value;
        const name = spaceNames[index];

        return name
          ? `${name}<br/>使用量: ${value} MB<br/>排名: ${index + 1}`
          : "";
      },
    },
    grid: {
      top: "15%",
      left: "3%",
      right: "5%",
      bottom: "12%",
      containLabel: true,
    },
    xAxis: {
      ...getBaseChartConfig().xAxis,
      data: spaceNames,
      axisLabel: {
        interval: 0,
        rotate: spaceNames.length > 6 ? 45 : 0,
        fontSize: "12",
        width: 100,
        overflow: "truncate",
        formatter: (value: string) => {
          const maxLength = spaceNames.length > 6 ? 8 : 12;
          return value.length > maxLength
            ? value.substring(0, maxLength) + "..."
            : value;
        },
      },
    },
    yAxis: {
      ...getBaseChartConfig().yAxis,
      name: "空间使用量 (MB)",
      nameTextStyle: {
        fontSize: "12",
        padding: [0, 0, 0, -30],
      },
      axisLabel: {
        fontSize: "12",
        formatter: (value: number) => {
          return value >= 1024
            ? `${(value / 1024).toFixed(1)}GB`
            : `${value.toFixed(1)}MB`;
        },
      },
    },
    series: [
      {
        name: "空间使用量",
        type: "bar",
        data: usageData,
        barWidth: "20%",
        barMaxWidth: 40,
        barMinWidth: 15,
        itemStyle: {
          borderRadius: [4, 4, 0, 0],
        },
        label: {
          show: true,
          position: "top",
          fontSize: 12,
          color: "#6B7280",
          formatter: (params: any) => {
            const value = params.value;
            return value >= 1024
              ? `${(value / 1024).toFixed(1)}GB`
              : `${value.toFixed(1)}MB`;
          },
          rotate: 0,
          distance: 5,
        },
        emphasis: {
          focus: "series",
          scale: true,
          scaleSize: 5,
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.2)",
          },
        },
        ...getBaseAnimationConfig(true),
      },
    ],
  };
});
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
