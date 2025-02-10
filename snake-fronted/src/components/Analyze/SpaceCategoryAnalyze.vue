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
        autoresize
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
      textStyle: {
        fontSize: "12",
      },
      itemWidth: 15,
      itemHeight: 15,
      itemGap: 15,
    },
    tooltip: {
      ...getBaseChartConfig().tooltip,
      trigger: "axis",
      axisPointer: {
        type: "shadow",
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
      data: categories,
      axisLabel: {
        interval: 0,
        rotate: categories.length > 6 ? 45 : 0,
        fontSize: "12",
        width: 100,
        overflow: "truncate",
      },
    },
    yAxis: [
      {
        ...getBaseChartConfig().yAxis,
        type: "value",
        name: "图片数量",
        nameTextStyle: {
          fontSize: "12",
          padding: [0, 0, 0, -30],
        },
        axisLabel: {
          fontSize: "12",
          formatter: (value: number) => {
            return value >= 1000 ? `${(value / 1000).toFixed(1)}k` : value;
          },
        },
        axisLine: { show: true, lineStyle: { color: baseColors[0].start } },
      },
      {
        ...getBaseChartConfig().yAxis,
        type: "value",
        name: "图片总大小 (MB)",
        nameTextStyle: {
          fontSize: "12",
          padding: [0, -30, 0, 0],
        },
        position: "right",
        axisLabel: {
          fontSize: "12",
          formatter: (value: number) => {
            return value >= 1024
              ? `${(value / 1024).toFixed(1)}GB`
              : `${value.toFixed(1)}MB`;
          },
        },
        axisLine: { show: true, lineStyle: { color: baseColors[1].start } },
        splitLine: { show: false },
      },
    ],
    series: [
      {
        name: "图片数量",
        type: "bar",
        data: countData,
        barWidth: "20%",
        barMaxWidth: 40,
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
          focus: "series",
        },
        ...getBaseAnimationConfig(),
      },
      {
        name: "图片总大小",
        type: "bar",
        data: sizeData,
        barWidth: "20%",
        barMaxWidth: 40,
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
          focus: "series",
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
