<template>
  <BaseAnalyze
    title="空间图片标签分析"
    icon="tags"
    icon-color="fuchsia"
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
import "echarts-wordcloud";
import { computed, watchEffect } from "vue";
import { getSpaceTagAnalyze } from "@/services";
import type { AnalyzeProps, TagAnalyzeType } from "@/config";
import { baseColors } from "./config";
import BaseAnalyze from "./BaseAnalyze.vue";
import { useAnalyzeData } from "./hooks/useAnalyzeData";
import { getBaseChartConfig, getBaseAnimationConfig } from "./utils/chartUtils";

const props = withDefaults(defineProps<AnalyzeProps>(), {
  queryAll: false,
  queryPublic: false,
});

// 使用抽取的数据加载hook
const { data, loading, errorMessage, fetchData } = useAnalyzeData<
  TagAnalyzeType[]
>({
  fetchFn: getSpaceTagAnalyze, // 使用对象形式传入
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
  if (!data.value) return getBaseChartConfig();

  const tagData = data.value.map((item, index) => ({
    name: item.tag,
    value: item.count,
    textStyle: {
      color: baseColors[index % baseColors.length].start,
      shadowBlur: 4,
      shadowColor: baseColors[index % baseColors.length].end + "40",
    },
  }));

  return {
    ...getBaseChartConfig(),
    tooltip: {
      ...getBaseChartConfig().tooltip,
      formatter: "{b}: {c} 次使用",
    },
    series: [
      {
        type: "wordCloud",
        gridSize: 12,
        sizeRange: [14, 50],
        rotationRange: [-45, 45],
        rotationStep: 15,
        shape: "circle",
        width: "100%",
        height: "100%",
        drawOutOfBound: false,
        textStyle: {
          fontFamily: "sans-serif",
          fontWeight: "bold",
        },
        emphasis: {
          textStyle: {
            fontWeight: "bold",
            shadowBlur: 10,
            shadowColor: "rgba(0, 0, 0, 0.3)",
          },
        },
        data: tagData,
        ...getBaseAnimationConfig(),
      },
    ],
  };
});
</script>

<style scoped>
@import "./styles/analyze.css";
</style>
