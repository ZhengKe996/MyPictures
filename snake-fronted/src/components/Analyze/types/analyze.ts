// 基础分析组件Props
export interface AnalyzeProps {
  queryAll?: boolean;
  queryPublic?: boolean;
  spaceId?: string;
}

// API响应数据基础接口
export interface ApiResponse<T> {
  code: number;
  message: string;
  data?: T;
}

// 图表基础配置接口
export interface BaseChartConfig {
  tooltip?: Record<string, any>;
  grid?: Record<string, any>;
  xAxis?: Record<string, any>;
  yAxis?: Record<string, any>;
}

// 渐变色配置
export interface GradientColor {
  start: string;
  end: string;
}
