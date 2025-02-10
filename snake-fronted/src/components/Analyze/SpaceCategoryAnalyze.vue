<template>
  <BaseAnalyze
    title="空间图片分类分析"
    icon="chart-pie"
    icon-color="blue"
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
import { getSpaceCategoryAnalyze } from "@/services";
import type { CategoryAnalyzeType, AnalyzeProps } from "@/config";
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
  CategoryAnalyzeType[]
>({
  fetchFn: getSpaceCategoryAnalyze,
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

  const categories = data.value.map((item) => item.category);
  const countData = data.value.map((item) => item.count);
  const sizeData = data.value.map((item) =>
    parseFloat(((item.totalSize ?? 0) / (1024 * 1024)).toFixed(2))
  );

  return {
    ...getBaseChartConfig(),
    legend: {
      ...getBaseChartConfig().legend,
      data: ["图片数量", "图片总大小"],
    },
    xAxis: {
      ...getBaseChartConfig().xAxis,
      data: categories,
    },
    yAxis: [
      {
        ...getBaseChartConfig().yAxis,
        type: "value",
        name: "图片数量",
        axisLine: { show: true, lineStyle: { color: baseColors[0].start } },
      },
      {
        ...getBaseChartConfig().yAxis,
        type: "value",
        name: "图片总大小 (MB)",
        position: "right",
        axisLine: { show: true, lineStyle: { color: baseColors[1].start } },
        splitLine: { show: false },
      },
    ],
    series: [
      {
        name: "图片数量",
        type: "bar",
        data: countData,
        itemStyle: {
          color: createGradient({
            start: baseColors[0].start,
            end: baseColors[0].end,
          }),
          borderRadius: [4, 4, 0, 0],
        },
        emphasis: {
          itemStyle: {
            color: createGradient({
              start: baseColors[0].end,
              end: baseColors[0].start,
            }),
          },
        },
        ...getBaseAnimationConfig(),
      },
      {
        name: "图片总大小",
        type: "bar",
        data: sizeData,
        yAxisIndex: 1,
        itemStyle: {
          color: createGradient({
            start: baseColors[1].start,
            end: baseColors[1].end,
          }),
          borderRadius: [4, 4, 0, 0],
        },
        emphasis: {
          itemStyle: {
            color: createGradient({
              start: baseColors[1].end,
              end: baseColors[1].start,
            }),
          },
        },
        ...getBaseAnimationConfig(),
      },
    ],
  };
});
</script>

<style scoped>
@import "./styles/analyze.css";
</style>
