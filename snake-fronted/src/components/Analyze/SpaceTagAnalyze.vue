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
        autoresize
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
import { getBaseChartConfig } from "./utils/chartUtils";

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

// 添加响应式字体大小计算函数
const calculateFontSize = (value: number, maxValue: number) => {
  const minSize = 12;
  const maxSize = 32;
  return minSize + (value / maxValue) * (maxSize - minSize);
};

// 图表选项
const options = computed(() => {
  if (!data.value) return getBaseChartConfig();

  // 计算最大使用次数，用于字体大小计算
  const maxCount = Math.max(...data.value.map((item) => item.count ?? 0));

  const tagData = data.value.map((item, index) => ({
    name: item.tag,
    value: item.count,
    textStyle: {
      color: baseColors[index % baseColors.length].start,
      shadowBlur: 4,
      shadowColor: baseColors[index % baseColors.length].end + "40",
      fontSize: calculateFontSize(item.count ?? 0, maxCount),
    },
  }));

  return {
    ...getBaseChartConfig(),
    tooltip: {
      ...getBaseChartConfig().tooltip,
      trigger: "item",
      formatter: (params: any) => {
        return `${params.name}<br/>使用次数：${params.value} 次`;
      },
      textStyle: {
        fontSize: "12",
      },
      backgroundColor: "rgba(255, 255, 255, 0.95)",
      borderColor: "#E5E7EB",
      borderWidth: 1,
      padding: [8, 12],
    },
    series: [
      {
        type: "wordCloud",
        // 自适应容器大小
        width: "95%",
        height: "95%",
        // 优化布局配置
        gridSize: 8,
        sizeRange: [12, 32],
        rotationRange: [-45, 45],
        rotationStep: 15,
        shape: "circle",
        drawOutOfBound: false,
        shrinkToFit: true, // 确保文字都在容器内
        layoutAnimation: true, // 启用布局动画
        // 全局文字样式
        textStyle: {
          fontFamily: "sans-serif",
          fontWeight: "normal",
          padding: 2,
        },
        // 高亮效果
        emphasis: {
          focus: "self",
          textStyle: {
            fontWeight: "bold",
            shadowBlur: 10,
            shadowColor: "rgba(0, 0, 0, 0.3)",
          },
        },
        data: tagData,
        // 动画效果
        animation: true,
        animationDuration: 1000,
        animationEasing: "cubicOut",
        animationDelay: (idx: number) => idx * 50,
      },
    ],
  };
});
</script>

<style scoped>
@import "./styles/analyze.css";

:deep(.echarts) {
  width: 100% !important;
  height: 100% !important;
  transition: all 0.3s ease-in-out;
}

:deep(.echarts-for-react) {
  min-height: 300px;
}
</style>
