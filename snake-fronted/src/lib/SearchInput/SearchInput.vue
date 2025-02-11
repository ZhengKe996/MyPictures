<template>
  <div class="flex w-full justify-start items-center">
    <label
      v-if="showLabel"
      :for="id"
      class="block text-sm/6 font-medium text-gray-900"
    >
      {{ label }}
    </label>
    <div class="mx-2 flex-1">
      <div
        class="flex rounded-md bg-white outline outline-1 -outline-offset-1 outline-gray-300 focus-within:outline focus-within:outline-2 focus-within:-outline-offset-2 focus-within:outline-indigo-600"
      >
        <input
          :type="type"
          :id="id"
          :name="name"
          :value="modelValue"
          class="block min-w-0 grow px-3 py-1.5 text-base text-gray-900 placeholder:text-gray-400 focus:outline focus:outline-0 sm:text-sm/6"
          :placeholder="placeholder"
          @input="handleInput"
          @keypress="handleKeyPress"
        />
        <!-- 清除按钮 -->
        <button
          v-show="modelValue"
          type="button"
          class="flex items-center justify-center px-2 text-gray-400 hover:text-gray-600 transition-colors duration-200 focus:outline-none"
          @click="clearInput"
          title="清除"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-4 w-4"
            viewBox="0 0 24 24"
            stroke-width="2"
            stroke="currentColor"
            fill="none"
          >
            <path stroke="none" d="M0 0h24v24H0z" fill="none" />
            <path d="M18 6l-12 12" />
            <path d="M6 6l12 12" />
          </svg>
        </button>
        <!-- 分隔线 -->
        <div v-show="modelValue" class="w-px h-5 my-auto bg-gray-200"></div>
        <!-- 搜索按钮 -->
        <button
          type="button"
          class="flex items-center justify-center px-3 py-1.5 text-gray-500 hover:text-gray-700 focus:outline-none transition-colors duration-200"
          :class="[
            size === 'sm' ? 'text-sm' : '',
            size === 'lg' ? 'text-lg' : '',
          ]"
          @click="$emit('search')"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5"
            viewBox="0 0 24 24"
            stroke-width="2"
            stroke="currentColor"
            fill="none"
          >
            <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
            <path d="M10 10m-7 0a7 7 0 1 0 14 0a7 7 0 1 0 -14 0"></path>
            <path d="M21 21l-6 -6"></path>
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useThrottleFn } from "@vueuse/core";

interface Props {
  modelValue?: string;
  label?: string;
  showLabel?: boolean;
  placeholder?: string;
  type?: string;
  id?: string;
  name?: string;
  size?: "sm" | "md" | "lg";
}

withDefaults(defineProps<Props>(), {
  modelValue: "",
  label: "Search",
  showLabel: true,
  placeholder: "Search...",
  type: "text",
  id: "search",
  name: "search",
  size: "md",
});

const emit = defineEmits<{
  (e: "update:modelValue", value: string): void;
  (e: "search"): void;
  (e: "keypress", event: KeyboardEvent): void;
  (e: "clear"): void;
}>();

const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement;
  emit("update:modelValue", target.value);
};

const handleKeyPress = useThrottleFn((event: KeyboardEvent) => {
  emit("keypress", event);
  if (event.key === "Enter") {
    emit("search");
  }
}, 300);

const clearInput = () => {
  emit("update:modelValue", "");
  emit("clear");
};
</script>
