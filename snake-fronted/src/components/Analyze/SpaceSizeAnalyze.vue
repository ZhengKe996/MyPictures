<template>
  <div class="w-full space-y-6 animated animated-duration-500 animated-fade-in">
    <div
      class="bg-white rounded-xl border border-gray-100 shadow-xs hover:shadow-sm transition-all duration-500"
    >
      <div class="p-6 border-b border-gray-100">
        <div class="flex items-center justify-between">
          <h3 class="text-lg font-semibold text-gray-900">空间图片大小分析</h3>
          <div
            class="bg-violet-50/70 rounded-full p-2 transition-colors duration-500"
          >
            <i class="i-tabler:chart-pie-filled text-violet-500 size-5"></i>
          </div>
        </div>
      </div>

      <div class="relative p-6">
        <!-- Loading Overlay -->
        <div
          v-if="loading"
          class="absolute inset-0 bg-white/80 backdrop-blur-sm z-10 flex items-center justify-center"
        >
          <div class="flex flex-col items-center gap-3">
            <div
              class="w-8 h-8 border-3 border-violet-600 border-t-transparent rounded-full animate-spin"
            ></div>
            <span class="text-sm text-gray-500">加载中...</span>
          </div>
        </div>

        <!-- Chart Container -->
        <div
          class="h-[320px] transition-opacity duration-300"
          :class="{ 'opacity-50': loading }"
        >
          <v-chart
            :option="options"
            :style="{ height: '100%', width: '100%' }"
            :animation="true"
          />
        </div>
      </div>
    </div>

    <!-- Status Message -->
    <TransitionGroup name="message" tag="div">
      <div
        v-if="errorMessage"
        :key="errorMessage"
        class="p-4 rounded-lg border bg-error-50 text-error-700 border-error-200 flex items-center gap-3 animated animated-faster animated-fade-in"
      >
        <i class="i-tabler:alert-circle size-5 text-error-400" />
        <span class="font-medium">{{ errorMessage }}</span>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup lang="ts">
import VChart from "vue-echarts";
import "echarts";
import { computed, ref, watchEffect } from "vue";
import { getSpaceSizeAnalyze } from "@/services";
import type { SizeAnalyzeType, AnalyzeProps } from "@/config";
import { baseColors } from "./config";

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

const options = computed(() => {
  const pieData = (dataList.value ?? []).map((item, index) => ({
    name: item.sizeRange,
    value: item.count,
    itemStyle: {
      color: {
        type: "linear",
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
          type: "linear",
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
