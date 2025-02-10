import type { EChartsOption } from "echarts";
import type { GradientColor } from "../types/analyze";

// 创建渐变色
export const createGradient = (colors: GradientColor, opacity = 1) => ({
  type: "linear",
  x: 0,
  y: 0,
  x2: 0,
  y2: 1,
  colorStops: [
    {
      offset: 0,
      color:
        colors.start +
        (opacity < 1 ? Math.floor(opacity * 255).toString(16) : ""),
    },
    {
      offset: 1,
      color: colors.end + (opacity < 1 ? "00" : ""),
    },
  ],
});

// 基础图表配置
export const getBaseChartConfig = (): EChartsOption => ({
  tooltip: {
    backgroundColor: "rgba(255, 255, 255, 0.9)",
    borderWidth: 1,
    borderColor: "#E5E7EB",
    textStyle: { color: "#374151" },
    padding: [8, 12],
  },
  grid: {
    top: "5%",
    left: "3%",
    right: "4%",
    bottom: "15%",
    containLabel: true,
  },
  xAxis: {
    axisLine: { lineStyle: { color: "#E5E7EB" } },
    axisTick: { show: false },
    axisLabel: { color: "#6B7280" },
  },
  yAxis: {
    nameTextStyle: { color: "#6B7280" },
    axisLabel: { color: "#6B7280" },
    splitLine: { lineStyle: { color: "#F3F4F6", type: "dashed" } },
  },
});

// 基础动画配置
export const getBaseAnimationConfig = (delay = true) => ({
  animation: true,
  animationDuration: 1000,
  animationEasing: "cubicOut",
  ...(delay
    ? {
        animationDelay: (idx: number) => idx * 100,
        animationDelayUpdate: (idx: number) => idx * 100,
      }
    : {}),
});
