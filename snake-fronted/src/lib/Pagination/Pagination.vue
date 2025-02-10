<template>
  <nav
    class="flex items-center justify-between border-t border-gray-200 mt-2 px-4 sm:px-0"
  >
    <div class="-mt-px flex w-0 flex-1">
      <button
        :disabled="isFirstPage || disabled"
        class="inline-flex items-center border-t-2 border-transparent pr-1 pt-4 text-sm font-medium transition-colors duration-200 ease-in-out"
        :class="[
          isFirstPage || disabled
            ? 'text-gray-300 cursor-not-allowed'
            : 'text-gray-500 hover:border-gray-300 hover:text-gray-700',
        ]"
        @click="handlePageChange(current - 1)"
      >
        <i class="i-tabler-arrow-move-left size-5 mr-3" />
        Previous
      </button>
    </div>

    <div class="hidden md:-mt-px md:flex">
      <TransitionGroup name="fade">
        <button
          v-for="page in displayedPages"
          :key="page"
          class="inline-flex items-center border-t-2 px-4 pt-4 text-sm font-medium transition-all duration-200 ease-in-out"
          :class="[
            current === page
              ? 'border-indigo-500 text-indigo-600'
              : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
            disabled ? 'cursor-not-allowed opacity-50' : 'cursor-pointer',
          ]"
          :disabled="disabled"
          @click="typeof page === 'number' && handlePageChange(page)"
        >
          {{ page }}
        </button>
      </TransitionGroup>
    </div>

    <div v-if="showQuickJumper" class="hidden md:flex items-center">
      <input
        type="number"
        :value="current"
        :disabled="disabled"
        min="1"
        :max="pageCount"
        class="w-16 px-2 py-1 text-sm border rounded-md"
        @change="handleQuickJump"
      />
    </div>

    <div class="-mt-px flex w-0 flex-1 justify-end">
      <button
        :disabled="isLastPage || disabled"
        class="inline-flex items-center border-t-2 border-transparent pl-1 pt-4 text-sm font-medium transition-colors duration-200 ease-in-out"
        :class="[
          isLastPage || disabled
            ? 'text-gray-300 cursor-not-allowed'
            : 'text-gray-500 hover:border-gray-300 hover:text-gray-700',
        ]"
        @click="handlePageChange(current + 1)"
      >
        Next
        <i class="i-tabler-arrow-move-right size-5 ml-3" />
      </button>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { computed } from "vue";

interface Props {
  total: number;
  current: number;
  pageSize?: number;
  disabled?: boolean;
  showQuickJumper?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  pageSize: 20,
  disabled: false,
  showQuickJumper: false,
});

const emit = defineEmits<{
  "update:current": [page: number];
  change: [page: number];
}>();

const pageCount = computed(() => Math.ceil(props.total / props.pageSize));
const isFirstPage = computed(() => props.current <= 1);
const isLastPage = computed(() => props.current >= pageCount.value);

const displayedPages = computed(() => {
  const count = pageCount.value;
  const current = props.current;
  const delta = 2;
  const range: (number | string)[] = [];

  for (
    let i = Math.max(2, current - delta);
    i <= Math.min(count - 1, current + delta);
    i++
  ) {
    range.push(i);
  }

  if (current - delta > 2) {
    range.unshift("...");
  }
  if (current + delta < count - 1) {
    range.push("...");
  }

  range.unshift(1);
  if (count > 1) {
    range.push(count);
  }

  return range;
});

const handlePageChange = (page: number) => {
  if (props.disabled || page === props.current) return;
  if (page >= 1 && page <= pageCount.value) {
    emit("update:current", page);
    emit("change", page);
  }
};

const handleQuickJump = (event: Event) => {
  const value = Number((event.target as HTMLInputElement).value);
  if (!isNaN(value) && value >= 1 && value <= pageCount.value) {
    handlePageChange(value);
  }
};
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(30px);
}

.fade-move {
  transition: transform 0.3s ease;
}
</style>
