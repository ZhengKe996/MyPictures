<template>
  <div class="min-h-screen p-6 bg-gray-50">
    <Transition
      enter-active-class="transition-all duration-500 ease-out"
      enter-from-class="opacity-0 transform -translate-y-4"
      enter-to-class="opacity-100 transform translate-y-0"
      leave-active-class="transition-all duration-500 ease-in"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <!-- 添加错误状态显示 -->
      <div
        v-if="hasError"
        class="max-w-7xl mx-auto py-12 animate-fade-in animate-duration-500"
      >
        <div class="text-center space-y-6">
          <div class="inline-block p-4 rounded-full bg-red-50 text-red-500">
            <i class="i-tabler-alert-triangle text-4xl"></i>
          </div>
          <div class="space-y-2">
            <h3 class="text-lg font-medium text-gray-900">参数错误</h3>
            <p class="text-sm text-gray-500">
              请确保提供有效的空间ID或选择正确的查询类型
            </p>
          </div>
          <div>
            <RouterLink
              to="/mamager/space"
              class="inline-flex items-center px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end rounded-lg hover:opacity-90 transition-opacity"
            >
              <i class="i-tabler-arrow-left mr-2"></i>
              返回空间列表
            </RouterLink>
          </div>
        </div>
      </div>

      <!-- 正常内容显示 -->
      <div
        v-else-if="isValid"
        class="max-w-7xl mx-auto animate-fade-in animate-duration-500"
      >
        <!-- 标题区域 -->
        <div class="mb-8">
          <div class="flex items-center space-x-3 mb-2">
            <i class="i-tabler-chart-histogram text-2xl text-gray-400"></i>
            <h2 class="text-2xl font-semibold text-gray-800">空间图库分析</h2>
          </div>
          <div class="ml-9 flex items-center space-x-2 text-sm">
            <span class="text-gray-500">当前查看：</span>
            <div
              class="px-3 py-1 rounded-full bg-gradient-to-r from-gray-50 to-gray-100 border border-gray-200"
            >
              <span v-if="queryAll" class="text-gray-600">
                <i class="i-tabler-database mr-1.5"></i>全部空间
              </span>
              <span v-else-if="queryPublic" class="text-gray-600">
                <i class="i-tabler-world mr-1.5"></i>公共图库
              </span>
              <span v-else class="flex items-center">
                <i class="i-tabler-folder mr-1.5 text-blue-500"></i>
                <RouterLink
                  :to="`/space/${spaceId}`"
                  class="text-blue-500 hover:text-blue-600 transition-colors duration-300 hover:underline decoration-blue-300 decoration-2 underline-offset-2"
                >
                  空间 ID：{{ spaceId }}
                </RouterLink>
              </span>
            </div>
            <span
              v-if="!queryAll && !queryPublic"
              class="text-xs text-gray-400 hover:text-gray-600 transition-colors duration-300"
            >
              <i class="i-tabler-external-link ml-1"></i>
            </span>
          </div>
        </div>

        <!-- 分析卡片网格 -->
        <div class="grid md:grid-cols-2 gap-6">
          <!-- 空间使用分析 -->
          <div
            class="bg-white rounded-xl shadow-sm p-6 hover:shadow-lg transition-all duration-300 animate-fade-in-up animate-delay-100"
          >
            <SpaceUsageAnalyze
              :spaceId="spaceId"
              :queryAll="queryAll"
              :queryPublic="queryPublic"
            />
          </div>

          <!-- 空间分类分析 -->
          <div
            class="bg-white rounded-xl shadow-sm p-6 hover:shadow-lg transition-all duration-300 animate-fade-in-up animate-delay-200"
          >
            <SpaceCategoryAnalyze
              :spaceId="spaceId"
              :queryAll="queryAll"
              :queryPublic="queryPublic"
            />
          </div>

          <!-- 标签分析 -->
          <div
            class="bg-white rounded-xl shadow-sm p-6 hover:shadow-lg transition-all duration-300 animate-fade-in-up animate-delay-300"
          >
            <SpaceTagAnalyze
              :spaceId="spaceId"
              :queryAll="queryAll"
              :queryPublic="queryPublic"
            />
          </div>

          <!-- 图片大小分段分析 -->
          <div
            class="bg-white rounded-xl shadow-sm p-6 hover:shadow-lg transition-all duration-300 animate-fade-in-up animate-delay-400"
          >
            <SpaceSizeAnalyze
              :spaceId="spaceId"
              :queryAll="queryAll"
              :queryPublic="queryPublic"
            />
          </div>

          <!-- 用户上传行为分析 -->
          <div
            class="bg-white rounded-xl shadow-sm p-6 hover:shadow-lg transition-all duration-300 animate-fade-in-up animate-delay-500"
          >
            <SpaceUserAnalyze
              :spaceId="spaceId"
              :queryAll="queryAll"
              :queryPublic="queryPublic"
            />
          </div>

          <!-- 空间使用排行分析 -->
          <div
            v-if="isAdmin"
            class="bg-white rounded-xl shadow-sm p-6 hover:shadow-lg transition-all duration-300 animate-fade-in-up animate-delay-600"
          >
            <SpaceRankAnalyze
              :spaceId="spaceId"
              :queryAll="queryAll"
              :queryPublic="queryPublic"
            />
          </div>
          <!-- 权限提示 -->
          <div
            v-else
            class="bg-white/50 rounded-xl border-2 border-dashed border-gray-200 p-6 flex flex-col items-center justify-center space-y-3 animate-fade-in-up animate-delay-600"
          >
            <div class="p-3 rounded-full bg-gray-50">
              <i class="i-tabler:lock text-2xl text-gray-400"></i>
            </div>
            <div class="text-center">
              <h3 class="text-base font-medium text-gray-600">空间使用排行</h3>
              <p class="text-sm text-gray-400 mt-1">需要管理员权限查看此数据</p>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from "vue";
import { useRoute, RouterLink } from "vue-router";
import { useUserStore } from "@/store/user";
import {
  SpaceCategoryAnalyze,
  SpaceTagAnalyze,
  SpaceUserAnalyze,
  SpaceRankAnalyze,
  SpaceSizeAnalyze,
  SpaceUsageAnalyze,
} from "@/components/Analyze";
import { ACCESSENUM } from "@/access";

const route = useRoute();
const hasError = ref(false);

const userStore = useUserStore();

// 添加管理员权限检查
const isAdmin = computed(() => {
  return userStore.getUserRole === ACCESSENUM.ADMIN;
});

// 修改验证参数逻辑，增加管理员特权
const isValid = computed(() => {
  // 如果是管理员
  if (isAdmin.value) {
    // 允许查询全部或公共空间
    if (route.query?.queryAll || route.query?.queryPublic) {
      return true;
    }
    // 或者有具体的空间ID
    if (route.query?.spaceId) {
      return true;
    }
    // 默认查询全部
    return true;
  }

  // 非管理员必须提供有效的空间ID
  return !!route.query?.spaceId && route.query.spaceId !== "";
});

// 空间 id，为管理员添加默认值
const spaceId = computed(() => {
  return route.query?.spaceId as string;
});

// 是否查询所有空间，为管理员添加默认值
const queryAll = computed(() => {
  if (isAdmin.value && !route.query?.spaceId && !route.query?.queryPublic) {
    return true; // 管理员默认查看所有空间
  }
  return !!route.query?.queryAll;
});

// 是否查询公共空间
const queryPublic = computed(() => {
  return !!route.query?.queryPublic;
});

onMounted(() => {
  // 检查参数有效性
  if (!isValid.value) {
    hasError.value = true;
  }
});
</script>

<style scoped></style>
