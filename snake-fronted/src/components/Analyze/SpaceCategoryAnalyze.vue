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
import { computed, ref, watchEffect } from "vue";
import { getSpaceCategoryAnalyze } from "@/services";
import type { CategoryAnalyzeType, AnalyzeProps } from "@/config";
import { baseColors } from "./config";
import BaseAnalyze from "./BaseAnalyze.vue";
import type { EChartsOption } from "echarts";

const props = withDefaults(defineProps<AnalyzeProps>(), {
  queryAll: false,
  queryPublic: false,
});

// 图表数据
const dataList = ref<CategoryAnalyzeType[]>();
// 加载状态
const loading = ref(true);
const errorMessage = ref("");

// 获取数据
const fetchData = async () => {
  loading.value = true;
  errorMessage.value = "";

  try {
    const {
      data: responseData,
      code,
      message,
    } = await getSpaceCategoryAnalyze({
      queryAll: props.queryAll,
      queryPublic: props.queryPublic,
      spaceId: props.spaceId,
    });

    if (code === 0 && responseData) {
      dataList.value = responseData;
    } else {
      errorMessage.value = "获取数据失败，" + message;
    }
  } catch (error) {
    errorMessage.value = "请求发生错误，请稍后重试";
  } finally {
    loading.value = false;
  }
};

/**
 * 监听变量，参数改变时触发数据的重新加载
 */
watchEffect(() => {
  fetchData();
});

// 图表选项
const options = computed<EChartsOption>(() => {
  const categories = (dataList.value ?? []).map((item) => item.category);
  const countData = (dataList.value ?? []).map((item) => item.count);
  const sizeData = (dataList.value ?? []).map((item) =>
    ((item.totalSize ?? 0) / (1024 * 1024)).toFixed(2)
  );

  return {
    tooltip: {
      trigger: "axis",
      backgroundColor: "rgba(255, 255, 255, 0.9)",
      borderWidth: 1,
      borderColor: "#E5E7EB",
      textStyle: { color: "#374151" },
      padding: [8, 12],
    },
    legend: {
      data: ["图片数量", "图片总大小"],
      bottom: "0",
      icon: "circle",
      itemWidth: 8,
      itemHeight: 8,
      textStyle: { color: "#6B7280" },
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
      data: categories,
      axisLine: { lineStyle: { color: "#E5E7EB" } },
      axisTick: { show: false },
      axisLabel: { color: "#6B7280" },
    },
    yAxis: [
      {
        type: "value",
        name: "图片数量",
        nameTextStyle: { color: "#6B7280" },
        axisLine: { show: true, lineStyle: { color: baseColors[0].start } },
        axisLabel: { color: "#6B7280" },
        splitLine: { lineStyle: { color: "#F3F4F6", type: "dashed" } },
      },
      {
        type: "value",
        name: "图片总大小 (MB)",
        nameTextStyle: { color: "#6B7280" },
        position: "right",
        axisLine: { show: true, lineStyle: { color: baseColors[1].start } },
        axisLabel: { color: "#6B7280" },
        splitLine: { show: false },
      },
    ],
    series: [
      {
        name: "图片数量",
        type: "bar",
        data: countData,
        itemStyle: {
          color: {
            type: "linear",
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: baseColors[0].start },
              { offset: 1, color: baseColors[0].end },
            ],
          },
          borderRadius: [4, 4, 0, 0],
        },
        emphasis: {
          itemStyle: {
            color: {
              type: "linear",
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: baseColors[0].end },
                { offset: 1, color: baseColors[0].start },
              ],
            },
          },
        },
      },
      {
        name: "图片总大小",
        type: "bar",
        data: sizeData,
        yAxisIndex: 1,
        itemStyle: {
          color: {
            type: "linear",
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: baseColors[1].start },
              { offset: 1, color: baseColors[1].end },
            ],
          },
          borderRadius: [4, 4, 0, 0],
        },
        emphasis: {
          itemStyle: {
            color: {
              type: "linear",
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: baseColors[1].end },
                { offset: 1, color: baseColors[1].start },
              ],
            },
          },
        },
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
