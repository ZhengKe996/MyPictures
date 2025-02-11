<template>
  <div
    class="relative inline-flex items-center justify-center"
    :style="{ width: `${size}px`, height: `${size}px` }"
  >
    <svg
      class="w-full h-full -rotate-90 transform"
      :viewBox="`0 0 ${viewBoxSize} ${viewBoxSize}`"
      :style="{
        filter: showDecoration
          ? 'drop-shadow(0 1px 2px rgb(0 0 0 / 0.1))'
          : 'none',
      }"
    >
      <!-- 主背景圆环 -->
      <circle
        :class="['transition-all duration-300', bgClass]"
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
          filter: 'drop-shadow(0 1px 1px rgb(0 0 0 / 0.05))',
        }"
      />

      <!-- 渐变定义 -->
      <defs>
        <linearGradient :id="`gradient-${uniqueId}`" x1="0%" y1="0%" x2="100%">
          <stop
            offset="0%"
            :style="{ stopColor: startColor, stopOpacity: 1 }"
          />
          <stop
            offset="100%"
            :style="{ stopColor: endColor, stopOpacity: 1 }"
          />
        </linearGradient>
      </defs>
    </svg>

    <!-- 中心内容 -->
    <div
      class="absolute inset-0 flex items-center justify-center"
      :class="{
        'group-hover:scale-110 transition-transform duration-300':
          showDecoration,
      }"
    >
      <slot name="center" :progress="currentProgress">
        <span class="text-sm font-medium" :class="textClass">
          {{ currentProgress }}%
        </span>
      </slot>
    </div>
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
  size: 32,
  strokeWidth: 3,
  startColor: "#4f46e5",
  endColor: "#7c3aed",
  label: "",
  showDecoration: false,
  duration: 1000,
  textClass: "text-gray-700",
  bgClass: "stroke-gray-200",
  labelClass: "text-gray-600",
  decorationClass: "",
  decorationDotsClass: "",
});

// 生成唯一ID，用于SVG渐变
const uniqueId = Math.random().toString(36).substr(2, 9);

// 调整计算属性以适应小尺寸
const viewBoxSize = computed(() => 100);
const center = computed(() => viewBoxSize.value / 2);
const radius = computed(() => viewBoxSize.value / 2 - props.strokeWidth * 2);
const circumference = computed(() => 2 * Math.PI * radius.value);
const currentProgress = ref(0);

const strokeDashoffset = computed(() => {
  return (
    circumference.value - (currentProgress.value / 100) * circumference.value
  );
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
