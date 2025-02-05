<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <Transition
      enter-active-class="transition-all duration-300 ease-out"
      enter-from-class="opacity-0 transform -translate-y-4"
      enter-to-class="opacity-100 transform translate-y-0"
      leave-active-class="transition-all duration-300 ease-in"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="isExit"
        class="space-content animate-fade-in animate-duration-500"
      >
        <div class="max-w-6xl mx-auto">
          <!-- 头部信息 -->
          <div
            class="flex items-center justify-between mb-8 animate-fade-in-up animate-duration-700"
          >
            <div class="flex items-center gap-3">
              <h1 class="text-2xl font-bold text-gray-800">
                {{ username }}
                <span class="text-sm font-normal text-gray-500 ml-2">
                  (私有空间)
                </span>
              </h1>
            </div>
            <Button
              type="primary"
              icon="i-tabler:photo-plus"
              @click="handleCreatePhoto"
              :isActiveAnim="true"
            >
              创建图片
            </Button>
          </div>

          <!-- 内容区域 -->
          <!-- <div
            class="bg-white rounded-xl shadow-sm p-6 animate-fade-in-up animate-delay-200"
          ></div> -->
        </div>
      </div>
      <div
        v-else
        class="flex flex-col items-center justify-center min-h-[400px]"
      >
        <div class="text-center space-y-8">
          <div
            class="animate-bounce-alt animate-duration-3000 animate-infinite animate-ease-in-out"
          >
            <div
              class="i-tabler:database-off text-8xl text-gray-400/80 mb-4 transform transition duration-300 hover:text-blue-500/80"
            ></div>
          </div>
          <div class="space-y-3">
            <h3 class="text-xl font-medium text-gray-700">
              你还没有创建自己的空间
            </h3>
            <p class="text-gray-500 text-sm">
              创建一个空间来存储和管理你的照片吧！
            </p>
            <button
              @click="handleCreateSpace"
              class="mt-4 px-6 py-2.5 bg-blue-500 text-white rounded-lg transform transition-all duration-200 hover:bg-blue-600 hover:scale-105 hover:shadow-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50 active:scale-95"
            >
              创建空间
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { GetSpaceByUserId } from "@/services";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/store/user";
import { onMounted, ref, computed } from "vue";
import Button from "@/lib/Button/Button.vue";

const userStore = useUserStore();
const router = useRouter();
const route = useRoute();
const isExit = ref(false);
const spaceId = ref<string>("");

// 获取用户名
const username = computed(() => userStore.getUserName || "未知用户");

const CheckSpace = async () => {
  const userId = userStore.getUserID;
  const { data, code } = await GetSpaceByUserId(userId);
  console.log("CheckSpace", data);
  if (code !== 0 && data === null) isExit.value = false;
  else isExit.value = true;
  if (data && data.id) spaceId.value = data.id;
};
onMounted(() => CheckSpace());

const handleCreateSpace = () => router.push("/add/space");

const handleCreatePhoto = () =>
  router.push(`/add/picture?spaceId=${spaceId.value}`);
</script>
