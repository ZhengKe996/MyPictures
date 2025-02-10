<template>
  <BaseAnalyze
    title="空间图片大小分析"
    icon="chart-pie-filled"
    icon-color="violet"
    :loading="loading"
    :error-message="errorMessage"
  >
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
import { computed, watchEffect } from "vue";
import { getSpaceSizeAnalyze } from "@/services";
import type { SizeAnalyzeType, AnalyzeProps } from "@/config";
import { baseColors } from "./config";
import BaseAnalyze from "./BaseAnalyze.vue";
import { useAnalyzeData } from "./hooks/useAnalyzeData";
import {
  getBaseChartConfig,
  createGradient,
  getBaseAnimationConfig,
} from "./utils/chartUtils";

const props = withDefaults(defineProps<AnalyzeProps>(), {
  queryAll: false,
  queryPublic: false,
});

// 使用抽取的数据加载hook
const { data, loading, errorMessage, fetchData } = useAnalyzeData<
  SizeAnalyzeType[]
>({
  fetchFn: getSpaceSizeAnalyze, // 使用对象形式传入
});

// 监听参数变化，重新加载数据
watchEffect(() => {
  fetchData({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId,
  });
});

// 图表选项
const options = computed(() => {
  if (!data.value?.length) return getBaseChartConfig();

  const pieData = data.value.map((item, index) => ({
    name: item.sizeRange,
    value: item.count,
    itemStyle: {
      color: createGradient({
        start: baseColors[index % baseColors.length].start,
        end: baseColors[index % baseColors.length].end,
      }),
    },
    emphasis: {
      itemStyle: {
        color: createGradient({
          start: baseColors[index % baseColors.length].end,
          end: baseColors[index % baseColors.length].start,
        }),
      },
    },
  }));

  return {
    ...getBaseChartConfig(),
    tooltip: {
      ...getBaseChartConfig().tooltip,
      trigger: "item",
      formatter: "{b}: {c} 张图片 ({d}%)",
    },
    legend: {
      bottom: "0",
      icon: "circle",
      itemWidth: 8,
      itemHeight: 8,
      textStyle: { color: "#6B7280" },
      padding: [0, 0, 10, 0],
    },
    series: [
      {
        name: "图片大小分布",
        type: "pie",
        radius: ["40%", "70%"],
        center: ["50%", "45%"],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 6,
          borderColor: "#fff",
          borderWidth: 2,
        },
        label: {
          show: false,
          position: "center",
        },
        emphasis: {
          scale: true,
          scaleSize: 10,
          label: {
            show: true,
            fontSize: "18",
            fontWeight: "bold",
            color: "#374151",
          },
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.2)",
          },
        },
        labelLine: {
          show: false,
        },
        data: pieData,
        ...getBaseAnimationConfig(),
      },
    ],
  };
});
</script>

<style scoped>
@import "./styles/analyze.css";
</style>
