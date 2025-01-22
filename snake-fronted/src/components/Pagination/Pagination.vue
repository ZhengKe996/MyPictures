<template>
  <nav
    class="flex items-center justify-between border-t border-gray-200 mt-2 px-4 sm:px-0"
  >
    <!-- 上一页 -->
    <div class="-mt-px flex w-0 flex-1">
      <a
        href="#"
        class="inline-flex items-center border-t-2 border-transparent pr-1 pt-4 text-sm font-medium text-gray-500 hover:border-gray-300 hover:text-gray-700"
        @click="PreviousHandle"
      >
        <i class="i-tabler:arrow-move-left size-5 mr-3"></i>
        Previous
      </a>
    </div>
    <div class="hidden md:-mt-px md:flex">
      <a
        v-for="i in pageCount"
        href="#"
        class="inline-flex items-center border-t-2 px-4 pt-4 text-sm font-medium"
        :class="
          current === i
            ? 'border-indigo-500 text-indigo-600'
            : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
        "
        @click="SelectPageHandle(i)"
        >{{ i }}</a
      >
    </div>
    <div class="-mt-px flex w-0 flex-1 justify-end">
      <a
        href="#"
        class="inline-flex items-center border-t-2 border-transparent pl-1 pt-4 text-sm font-medium text-gray-500 hover:border-gray-300 hover:text-gray-700"
        @click="NextHandle"
      >
        Next
        <i class="i-tabler:arrow-move-right size-5 mr-3"></i>
      </a>
    </div>
    <!-- 下一页 -->
  </nav>
</template>

<script setup lang="ts">
import { computed } from "vue";

const {
  total = 0,
  current = 1,
  pageSize = 20,
} = defineProps<{
  total: number;
  current: number;
  pageSize: number;
}>();

const pageCount = computed(() => Math.ceil(total / pageSize));
const emits = defineEmits(["previous", "next", "selectPage"]);
const PreviousHandle = () => {
  if (current > 1) emits("previous", current - 1);
};
const NextHandle = () => {
  if (current + 1 <= Math.ceil(total / pageSize)) emits("next", current + 1);
};
const SelectPageHandle = (page: number) => emits("selectPage", page);
</script>
