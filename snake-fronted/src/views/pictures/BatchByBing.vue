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
            class="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-purple-600 mb-8"
          >
            Batch By Bing
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
                class="px-4 py-3 bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 text-white rounded-lg shadow-md hover:shadow-lg transition-all duration-300 active:scale-95"
              >
                -
              </button>
              <button
                @click="incrementNumber"
                class="px-4 py-3 bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 text-white rounded-lg shadow-md hover:shadow-lg transition-all duration-300 active:scale-95"
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
              class="w-full px-6 py-4 bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700 text-white text-lg font-semibold rounded-lg shadow-md hover:shadow-lg transition-all duration-300 hover:-translate-y-0.5 active:scale-[0.98] disabled:opacity-70 disabled:cursor-not-allowed disabled:hover:translate-y-0 disabled:active:scale-100 disabled:hover:shadow-md"
              :disabled="!searchText || !count || !namePrefix"
            >
              <span class="flex items-center justify-center space-x-2">
                <span>Generate Images</span>
                <svg
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
import { ref, computed } from "vue";
import { AdminBatchByBing } from "@/services";
import { Message } from "@/lib/Message";
import { useThrottleFn } from "@vueuse/core";

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

// 定义一个异步函数handleSubmit来处理表单提交事件
const handleSubmit = useThrottleFn(async () => {
  if (isCountInvalid.value) {
    Message.error("Please enter a valid count between 1 and 30");
    return;
  }

  try {
    // 调用AdminBatchByBing接口进行图片抓取，传递当前的count, namePrefix, searchText值
    const { data, code, message } = await AdminBatchByBing({
      count: count.value,
      namePrefix: namePrefix.value,
      searchText: searchText.value,
    });
    // 如果接口返回的code为0且有数据，则显示成功消息
    if (code === 0 && data) Message.success(message); // 提示成功
    else Message.error(`抓取图片失败, 原因: ${message}`); // 提示失败
  } catch (error) {
    // 捕获到错误时，显示错误消息
    Message.error(`抓取图片失败, 原因: ${error}`);
  }
}, 3000);
</script>
