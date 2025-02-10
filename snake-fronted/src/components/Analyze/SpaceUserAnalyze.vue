<template>
  <BaseAnalyze
    title="空间图片用户分析"
    icon="users"
    icon-color="emerald"
    :loading="loading"
    :error-message="errorMessage"
  >
    <template #header-actions>
      <Segment
        v-model:value="timeDimension"
        :options="timeDimensionOptions"
        class="bg-gray-50/80"
      />
      <div class="relative">
        <SearchInput
          v-model="searchUserId"
          placeholder="请输入用户 id"
          @search="() => doSearch(searchUserId)"
          class="w-48"
        />
      </div>
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
import { computed, ref, watchEffect } from "vue";
import VChart from "vue-echarts";
import "echarts";
import { getSpaceUserAnalyze } from "@/services";
import type { UserAnalyzeType, AnalyzeProps } from "@/config";
import { baseColors } from "./config";
import SearchInput from "@/lib/SearchInput";
import BaseAnalyze from "./BaseAnalyze.vue";
import Segment from "@/lib/Segment/Segment.vue";
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

// 时间维度选项
const timeDimension = ref<"day" | "week" | "month">("day");
const timeDimensionOptions = [
  { label: "日", value: "day" },
  { label: "周", value: "week" },
  { label: "月", value: "month" },
];

// 用户选项
const userId = ref<string>();
const searchUserId = ref("");
const doSearch = (value: string) => {
  userId.value = value;
};

// 使用抽取的数据加载hook
const { data, loading, errorMessage, fetchData } = useAnalyzeData<
  UserAnalyzeType[]
>({
  fetchFn: getSpaceUserAnalyze,
});

// 监听参数变化，重新加载数据
watchEffect(() => {
  fetchData({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId,
    timeDimension: timeDimension.value,
    userId: userId.value,
  });
});

// 图表选项
const options = computed(() => {
  if (!data.value?.length) return getBaseChartConfig();

  const periods = data.value.map((item) => item.period);
  const counts = data.value.map((item) => item.count);

  return {
    ...getBaseChartConfig(),
    xAxis: {
      ...getBaseChartConfig().xAxis,
      data: periods,
    },
    yAxis: {
      ...getBaseChartConfig().yAxis,
      name: "上传数量",
      axisLine: { show: true, lineStyle: { color: baseColors[4].start } },
    },
    series: [
      {
        name: "上传数量",
        type: "line",
        data: counts,
        smooth: true,
        symbolSize: 8,
        lineStyle: {
          width: 3,
          color: baseColors[4].start,
        },
        itemStyle: {
          color: baseColors[4].start,
        },
        areaStyle: {
          color: createGradient(
            {
              start: baseColors[4].start,
              end: baseColors[4].end,
            },
            0.25
          ),
        },
        emphasis: {
          focus: "series",
          scale: true,
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
