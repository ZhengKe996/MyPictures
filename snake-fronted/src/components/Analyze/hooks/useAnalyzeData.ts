import { ref } from "vue";
import type { ApiResponse } from "../types/analyze";

export interface UseAnalyzeDataOptions<T> {
  fetchFn: (params?: any) => Promise<ApiResponse<T>>;
}

export function useAnalyzeData<T>(options: UseAnalyzeDataOptions<T>) {
  const data = ref<T>();
  const loading = ref(true);
  const errorMessage = ref("");

  const fetchData = async (params?: Record<string, any>) => {
    loading.value = true;
    errorMessage.value = "";

    try {
      const response = await options.fetchFn(params);
      if (response.code === 0 && response.data) {
        data.value = response.data;
      } else {
        errorMessage.value = `获取数据失败，${response.message}`;
      }
    } catch (error) {
      errorMessage.value = "请求发生错误，请稍后重试";
    } finally {
      loading.value = false;
    }
  };

  return {
    data,
    loading,
    errorMessage,
    fetchData,
  };
}
