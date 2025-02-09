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
import { computed, ref, watchEffect } from "vue";
import { getSpaceTagAnalyze } from "@/services";
import type { AnalyzeProps, TagAnalyzeType } from "@/config";
import { baseColors } from "./config";
import BaseAnalyze from "./BaseAnalyze.vue";

const props = withDefaults(defineProps<AnalyzeProps>(), {
  queryAll: false,
  queryPublic: false,
});

// 图表数据
const dataList = ref<TagAnalyzeType[]>([]);
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
    } = await getSpaceTagAnalyze({
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
const options = computed(() => {
  const tagData = dataList.value.map((item, index) => ({
    name: item.tag,
    value: item.count,
    textStyle: {
      color: baseColors[index % baseColors.length].start,
      shadowBlur: 4,
      shadowColor: baseColors[index % baseColors.length].end + "40",
    },
  }));

  return {
    tooltip: {
      backgroundColor: "rgba(255, 255, 255, 0.9)",
      borderWidth: 1,
      borderColor: "#E5E7EB",
      textStyle: { color: "#374151" },
      padding: [8, 12],
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
        animation: true,
        animationDuration: 1000,
        animationEasing: "cubicOut",
        animationDelay: (idx: number) => idx * 100,
      },
    ],
  };
});
</script>

<style scoped>
.message-enter-active,
.message-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.message-enter-from,
.message-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

:deep(.ant-progress-text) {
  font-size: 1.5rem !important;
  font-weight: 600 !important;
  color: #374151 !important;
  font-family: ui-monospace, monospace !important;
}

:deep(.ant-progress-circle-path) {
  transition: stroke-dashoffset 1s cubic-bezier(0.4, 0, 0.2, 1),
    stroke 0.5s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

:deep(.ant-progress-circle-trail) {
  stroke: rgb(243 244 246) !important;
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
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
