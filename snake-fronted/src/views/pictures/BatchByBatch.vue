<template>
  <div class="min-h-screen bg-gradient-to-br p-8">
    <div class="max-w-xl mx-auto">
      <transition
        enter-active-class="transition ease-out duration-300"
        enter-from-class="transform opacity-0 scale-95"
        enter-to-class="transform opacity-100 scale-100"
        leave-active-class="transition ease-in duration-200"
        leave-from-class="transform opacity-100 scale-100"
        leave-to-class="transform opacity-0 scale-95"
      >
        <div
          class="bg-white/90 backdrop-blur-xl rounded-xl p-8 space-y-8 shadow-[0_0_15px_rgba(0,0,0,0.1),0_0_6px_rgba(0,0,0,0.05)] border border-white/40 hover:shadow-[0_0_20px_rgba(0,0,0,0.12),0_0_8px_rgba(0,0,0,0.06)] transition-all duration-300 ease-out"
        >
          <h2
            class="text-3xl font-bold text-transparent bg-clip-text gradient-primary mb-8"
          >
            Batch By {{ isBatchMode ? "Pexels" : "Bing" }}
          </h2>

          <!-- Search Text -->
          <div class="space-y-3 group">
            <label
              class="block text-sm font-medium text-gray-700 group-hover:text-blue-600 transition-colors"
            >
              Search Text
            </label>
            <div class="relative">
              <input
                v-model="searchText"
                type="text"
                class="w-full px-5 py-3 bg-white/50 border-2 border-gray-200 rounded-lg placeholder:text-slate-400 placeholder:transition-colors placeholder:duration-200 hover:placeholder:text-slate-500 focus:placeholder:text-slate-300 focus:ring-4 focus:ring-blue-500/15 focus:border-blue-500 hover:border-blue-400/70 hover:bg-white/70 hover:-translate-y-0.5 focus:-translate-y-1 transition-all duration-300 ease-out shadow-[0_2px_4px_rgba(0,0,0,0.02),0_1px_2px_rgba(0,0,0,0.03),inset_0_1px_2px_rgba(0,0,0,0.03)] hover:shadow-[0_4px_8px_rgba(0,0,0,0.04),0_2px_4px_rgba(0,0,0,0.03),inset_0_1px_1px_rgba(0,0,0,0.02)] focus:shadow-[0_0_0_4px_rgba(59,130,246,0.15),0_4px_12px_rgba(0,0,0,0.05),inset_0_1px_1px_rgba(0,0,0,0.02)] disabled:opacity-70 disabled:cursor-not-allowed disabled:bg-gray-50/80"
                placeholder="Enter text here..."
              />
            </div>
          </div>

          <!-- Count Input -->
          <div class="space-y-3 group">
            <label
              class="block text-sm font-medium text-gray-700 group-hover:text-blue-600 transition-colors"
            >
              Count (1-30)
            </label>
            <div class="flex items-center space-x-3">
              <div class="relative flex-1">
                <input
                  v-model="count"
                  type="number"
                  min="1"
                  max="30"
                  @input="validateCount"
                  class="w-full px-5 py-3 bg-white/50 text-center [appearance:textfield] [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none border-2 border-gray-200 rounded-lg focus:ring-4 focus:ring-blue-500/15 focus:border-blue-500 hover:border-blue-400/70 hover:bg-white/70 hover:-translate-y-0.5 focus:-translate-y-1 transition-all duration-300 ease-out shadow-[0_2px_4px_rgba(0,0,0,0.02),0_1px_2px_rgba(0,0,0,0.03)] hover:shadow-[0_4px_8px_rgba(0,0,0,0.04),0_2px_4px_rgba(0,0,0,0.03)] focus:shadow-[0_0_0_4px_rgba(59,130,246,0.15),0_4px_12px_rgba(0,0,0,0.05)]"
                  :class="{
                    'border-red-300 focus:border-red-500 focus:ring-red-500/15':
                      isCountInvalid,
                  }"
                  @keypress="numberOnly"
                />
                <span
                  v-if="isCountInvalid"
                  class="absolute -bottom-6 left-0 text-xs text-red-500 transition-opacity duration-200"
                >
                  Please enter a number between 1 and 30
                </span>
              </div>
              <button
                @click="decrementNumber"
                class="px-4 py-3 gradient-primary text-white rounded-lg shadow-md hover:shadow-lg transition-all duration-300 active:scale-95"
              >
                -
              </button>
              <button
                @click="incrementNumber"
                class="px-4 py-3 gradient-primary text-white rounded-lg shadow-md hover:shadow-lg transition-all duration-300 active:scale-95"
              >
                +
              </button>
            </div>
          </div>

          <!-- Name Prefix -->
          <div class="space-y-3 group">
            <label
              class="block text-sm font-medium text-gray-700 group-hover:text-blue-600 transition-colors"
            >
              Name Prefix
            </label>
            <div class="relative">
              <input
                v-model="namePrefix"
                type="text"
                class="w-full px-5 py-3 bg-white/50 border-2 border-gray-200 rounded-lg placeholder:text-slate-400 placeholder:transition-colors placeholder:duration-200 hover:placeholder:text-slate-500 focus:placeholder:text-slate-300 focus:ring-4 focus:ring-blue-500/15 focus:border-blue-500 hover:border-blue-400/70 hover:bg-white/70 hover:-translate-y-0.5 focus:-translate-y-1 transition-all duration-300 ease-out shadow-[0_2px_4px_rgba(0,0,0,0.02),0_1px_2px_rgba(0,0,0,0.03),inset_0_1px_2px_rgba(0,0,0,0.03)] hover:shadow-[0_4px_8px_rgba(0,0,0,0.04),0_2px_4px_rgba(0,0,0,0.03),inset_0_1px_1px_rgba(0,0,0,0.02)] focus:shadow-[0_0_0_4px_rgba(59,130,246,0.15),0_4px_12px_rgba(0,0,0,0.05),inset_0_1px_1px_rgba(0,0,0,0.02)] disabled:opacity-70 disabled:cursor-not-allowed disabled:bg-gray-50/80"
                placeholder="Enter text here..."
              />
            </div>
          </div>

          <!-- Button -->
          <div class="pt-4">
            <button
              @click="handleSubmit"
              class="w-full px-6 py-4 gradient-primary text-white text-lg font-semibold rounded-lg shadow-md hover:shadow-lg transition-all duration-300 hover:-translate-y-0.5 active:scale-[0.98] disabled:opacity-70 disabled:cursor-not-allowed disabled:hover:translate-y-0 disabled:active:scale-100 disabled:hover:shadow-md"
              :disabled="!searchText || !count || !namePrefix || isLoading"
            >
              <span class="flex items-center justify-center space-x-2">
                <svg
                  v-if="isLoading"
                  class="animate-spin -ml-1 mr-3 h-5 w-5 text-white"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                >
                  <circle
                    class="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    stroke-width="4"
                  ></circle>
                  <path
                    class="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                  ></path>
                </svg>
                <span>{{
                  isLoading ? "Generating..." : "Generate Images"
                }}</span>
                <svg
                  v-if="!isLoading"
                  class="w-5 h-5 transition-transform group-hover:translate-x-1"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M13 7l5 5m0 0l-5 5m5-5H6"
                  />
                </svg>
              </span>
            </button>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watchEffect } from "vue";
import { AdminBatchByBing, AdminBatchByPexels } from "@/services";
import { Message } from "@/lib/Message";
import { useThrottleFn } from "@vueuse/core";
import { useRoute } from "vue-router";

const searchText = ref("");
const count = ref(10); // 修改默认值为10
const namePrefix = ref("");

const isCountInvalid = computed(() => {
  const value = count.value;
  return value < 1 || value > 30;
});

const incrementNumber = () => {
  if (count.value < 30) {
    count.value++;
  }
};

const decrementNumber = () => {
  if (count.value > 1) {
    count.value--;
  }
};

const validateCount = (e: Event) => {
  const input = e.target as HTMLInputElement;
  let value = parseInt(input.value);

  if (isNaN(value)) {
    count.value = 1;
  } else {
    if (value < 1) count.value = 1;
    if (value > 30) count.value = 30;
  }
};

const numberOnly = (e: KeyboardEvent) => {
  const charCode = e.which ? e.which : e.keyCode;
  if (charCode > 31 && (charCode < 48 || charCode > 57)) {
    e.preventDefault();
  }
};

const isLoading = ref(false);

// 定义一个异步函数handleSubmit来处理表单提交事件
const handleSubmit = useThrottleFn(async () => {
  if (isCountInvalid.value) {
    Message.error("Please enter a valid count between 1 and 30");
    return;
  }

  try {
    isLoading.value = true;
    const params = {
      count: count.value,
      namePrefix: namePrefix.value,
      searchText: searchText.value,
    };

    // 根据模式选择不同的API
    const apiCall = isBatchMode.value ? AdminBatchByPexels : AdminBatchByBing;
    const { data, code, message } = await apiCall(params);

    if (code === 0 && data) Message.success(message);
    else Message.error(`抓取图片失败, 原因: ${message}`);
  } catch (error) {
    Message.error(`抓取图片失败, 原因: ${error}`);
  } finally {
    isLoading.value = false;
  }
}, 3000);

const route = useRoute();
const isBatchMode = ref(false);
watchEffect(() => (isBatchMode.value = route.path.includes("pexels")));
</script>
