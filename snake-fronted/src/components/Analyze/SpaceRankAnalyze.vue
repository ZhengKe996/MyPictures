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
      formatter: (params: any[] | any) => {
        // 处理参数可能是数组的情况
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
    xAxis: {
      ...getBaseChartConfig().xAxis,
      data: spaceNames,
      axisLabel: {
        interval: 0,
        rotate: 45,
        formatter: (value: string) =>
          value.length > 10 ? value.substring(0, 10) + "..." : value,
      },
    },
    yAxis: {
      ...getBaseChartConfig().yAxis,
      name: "空间使用量 (MB)",
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
        label: {
          show: true,
          position: "top",
          fontSize: 12,
          color: "#6B7280",
          formatter: "{c} MB",
        },
        emphasis: {
          focus: "series",
          scale: true,
          scaleSize: 5,
        },
        ...getBaseAnimationConfig(),
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
