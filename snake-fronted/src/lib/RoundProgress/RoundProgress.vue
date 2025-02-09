<template>
  <div class="relative" :style="{ width: `${size}px`, height: `${size}px` }">
    <!-- 外层装饰圆环 -->
    <div
      v-if="showDecoration"
      class="absolute inset-0 rounded-full blur-xl"
      :class="decorationClass"
    ></div>

    <div class="relative w-full h-full group">
      <svg
        class="w-full h-full -rotate-90"
        :viewBox="`0 0 ${viewBoxSize} ${viewBoxSize}`"
      >
        <!-- 装饰性背景圆环 -->
        <circle
          v-if="showDecoration"
          class="stroke-gray-200/50"
          :stroke-width="strokeWidth / 3"
          fill="transparent"
          :r="radius + 4"
          :cx="center"
          :cy="center"
        />
        <circle
          v-if="showDecoration"
          class="stroke-gray-200/30"
          :stroke-width="strokeWidth / 3"
          fill="transparent"
          :r="radius - 4"
          :cx="center"
          :cy="center"
        />

        <!-- 主背景圆环 -->
        <circle
          :class="bgClass"
          :stroke-width="strokeWidth"
          fill="transparent"
          :r="radius"
          :cx="center"
          :cy="center"
        />

        <!-- 进度圆环 -->
        <circle
          class="transition-all duration-500 ease-out"
          :stroke-width="strokeWidth"
          fill="transparent"
          :r="radius"
          :cx="center"
          :cy="center"
          :style="{
            strokeDasharray: circumference,
            strokeDashoffset: strokeDashoffset,
            stroke: `url(#gradient-${uniqueId})`,
          }"
        />

        <!-- 渐变定义 -->
        <defs>
          <linearGradient
            :id="`gradient-${uniqueId}`"
            x1="0%"
            y1="0%"
            x2="100%"
            y2="0%"
          >
            <stop
              offset="0%"
              class="transition-all duration-300"
              :style="{ stopColor: startColor, stopOpacity: 1 }"
            />
            <stop
              offset="100%"
              class="transition-all duration-300"
              :style="{ stopColor: endColor, stopOpacity: 1 }"
            />
          </linearGradient>
        </defs>
      </svg>

      <!-- 中心内容 -->
      <slot name="center" :progress="currentProgress">
        <div
          class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 text-center w-full"
        >
          <span
            class="block font-bold transition-all duration-300 group-hover:scale-110"
            :class="[textClass, textSizeClass]"
          >
            {{ currentProgress }}%
          </span>
          <span
            v-if="label"
            class="block mt-2 text-sm font-medium tracking-wider uppercase transition-all duration-300"
            :class="labelClass"
          >
            {{ label }}
          </span>
        </div>
      </slot>
    </div>

    <!-- 装饰性光点 -->
    <template v-if="showDecoration">
      <div
        class="absolute top-0 right-0 rounded-full blur-sm animate-pulse"
        :class="[decorationDotsClass, 'w-4 h-4']"
      ></div>
      <div
        class="absolute bottom-0 left-0 rounded-full blur-sm animate-pulse delay-300"
        :class="[decorationDotsClass, 'w-3 h-3']"
      ></div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";

interface Props {
  progress: number;
  size?: number;
  strokeWidth?: number;
  startColor?: string;
  endColor?: string;
  label?: string;
  showDecoration?: boolean;
  duration?: number;
  textClass?: string;
  bgClass?: string;
  labelClass?: string;
  decorationClass?: string;
  decorationDotsClass?: string;
}

const props = withDefaults(defineProps<Props>(), {
  size: 220,
  strokeWidth: 12,
  startColor: "#4f46e5",
  endColor: "#7c3aed",
  label: "完成度",
  showDecoration: true,
  duration: 2000,
  textClass:
    "bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent",
  bgClass: "stroke-gray-200/80",
  labelClass: "text-gray-600 group-hover:text-indigo-600",
  decorationClass: "bg-gradient-to-r from-purple-500/10 to-pink-500/10",
  decorationDotsClass: "bg-indigo-500",
});

// 生成唯一ID，用于SVG渐变
const uniqueId = Math.random().toString(36).substr(2, 9);

// 计算属性
const viewBoxSize = computed(() => 200);
const center = computed(() => viewBoxSize.value / 2);
const radius = computed(() => 90);
const circumference = computed(() => 2 * Math.PI * radius.value);
const currentProgress = ref(0);

const strokeDashoffset = computed(() => {
  return (
    circumference.value - (currentProgress.value / 100) * circumference.value
  );
});

const textSizeClass = computed(() => {
  if (props.size <= 150) return "text-3xl";
  if (props.size <= 200) return "text-4xl";
  return "text-5xl";
});

// 动画
onMounted(() => {
  const easeOutQuart = (x: number): number => 1 - Math.pow(1 - x, 4);
  let startTime: number | null = null;

  const animate = (currentTime: number) => {
    if (!startTime) startTime = currentTime;
    const elapsed = currentTime - startTime;
    const progress = Math.min(elapsed / props.duration, 1);

    currentProgress.value = Math.round(easeOutQuart(progress) * props.progress);

    if (progress < 1) {
      requestAnimationFrame(animate);
    }
  };

  requestAnimationFrame(animate);
});
</script>
