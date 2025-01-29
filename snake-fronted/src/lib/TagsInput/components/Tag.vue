<template>
  <div
    :class="[
      'inline-flex items-center rounded-full px-3 py-1',
      `bg-${props.color}-100 text-${props.color}-700`,
      sizeClasses,
      'transition-all duration-200 hover:bg-opacity-80',
    ]"
  >
    <span class="truncate">{{ text }}</span>
    <button
      @click="handleRemove"
      :class="[
        'ml-2 rounded-full hover:bg-opacity-80',
        `hover:bg-${props.color}-200`,
        'focus:outline-none focus:ring-2',
        `focus:ring-${props.color}-500 focus:ring-offset-2`,
        'transition-colors duration-200',
      ]"
    >
      <svg
        class="h-4 w-4"
        :class="[`text-${props.color}-500`]"
        viewBox="0 0 20 20"
        fill="currentColor"
      >
        <path
          fill-rule="evenodd"
          d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
          clip-rule="evenodd"
        />
      </svg>
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";

interface Props {
  text: string;
  index: number;
  color?: string;
  size?: "sm" | "md" | "lg";
}

const props = withDefaults(defineProps<Props>(), {
  color: "blue",
  size: "md",
});

const emit = defineEmits<{
  (e: "remove", index: number): void;
}>();

const sizeClasses = computed(
  () =>
    ({
      sm: "text-xs",
      md: "text-sm",
      lg: "text-base",
    }[props.size])
);

const handleRemove = () => {
  emit("remove", props.index);
};
</script>
