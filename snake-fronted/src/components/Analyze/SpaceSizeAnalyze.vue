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
import { computed, ref, watchEffect } from "vue";
import { getSpaceSizeAnalyze } from "@/services";
import type { SizeAnalyzeType, AnalyzeProps } from "@/config";
import { baseColors } from "./config";
import BaseAnalyze from "./BaseAnalyze.vue";
import type { EChartsOption } from "echarts";

const props = withDefaults(defineProps<AnalyzeProps>(), {
  queryAll: false,
  queryPublic: false,
});

const dataList = ref<SizeAnalyzeType[]>([]);
const loading = ref(true);
const errorMessage = ref("");

const fetchData = async () => {
  loading.value = true;
  errorMessage.value = "";

  try {
    const {
      data: responseData,
      code,
      message,
    } = await getSpaceSizeAnalyze({
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

watchEffect(() => {
  fetchData();
});

const options = computed<EChartsOption>(() => {
  const pieData = (dataList.value ?? []).map((item, index) => ({
    name: item.sizeRange,
    value: item.count,
    itemStyle: {
      color: {
        type: "linear" as "linear",
        x: 0,
        y: 0,
        x2: 0,
        y2: 1,
        colorStops: [
          { offset: 0, color: baseColors[index % baseColors.length].start },
          { offset: 1, color: baseColors[index % baseColors.length].end },
        ],
      },
    },
    emphasis: {
      itemStyle: {
        color: {
          type: "linear" as "linear",
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: baseColors[index % baseColors.length].end },
            { offset: 1, color: baseColors[index % baseColors.length].start },
          ],
        },
      },
    },
  }));

  return {
    tooltip: {
      trigger: "item",
      backgroundColor: "rgba(255, 255, 255, 0.9)",
      borderWidth: 1,
      borderColor: "#E5E7EB",
      textStyle: { color: "#374151" },
      padding: [8, 12],
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
