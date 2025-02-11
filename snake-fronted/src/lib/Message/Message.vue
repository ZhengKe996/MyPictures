<template>
  <Teleport to="body">
    <TransitionGroup
      :name="position === 'top' ? 'message-down' : 'message-up'"
      @after-leave="destroy"
    >
      <div
        v-show="isVisible"
        :key="id"
        class="fixed left-[50%] z-50 flex items-center px-5 py-3 rounded-lg transform -translate-x-1/2 transition-all duration-300"
        :class="[
          positionClass,
          styles[type].containerClass,
          { 'cursor-pointer': closable },
        ]"
        @click="closable && handleClose()"
      >
        <div class="mr-3 flex-shrink-0" :class="styles[type].iconWrapperClass">
          <svg
            v-if="type === 'success'"
            class="w-6 h-6"
            viewBox="0 0 24 24"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M22 11.08V12a10 10 0 1 1-5.93-9.14"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
            <path
              d="M22 4 12 14.01l-3-3"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>

          <svg
            v-else-if="type === 'warning'"
            class="w-6 h-6"
            viewBox="0 0 24 24"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0zM12 9v4M12 17h.01"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>

          <svg
            v-else
            class="w-6 h-6"
            viewBox="0 0 24 24"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <circle
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              stroke-width="2"
            />
            <path
              d="m15 9-6 6M9 9l6 6"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
            />
          </svg>
        </div>

        <span class="text-sm font-medium" :class="styles[type].textClass">
          {{ content }}
        </span>

        <button
          v-if="closable"
          class="ml-4 p-1 rounded-full transition-all duration-200"
          :class="styles[type].closeButtonClass"
          @click.stop="handleClose"
        >
          <i class="i-tabler-x h-4 w-4" />
        </button>
      </div>
    </TransitionGroup>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";

interface Props {
  id: string;
  type: "success" | "warning" | "error";
  content: string;
  duration?: number;
  position?: "top" | "bottom";
  closable?: boolean;
  onClose?: () => void;
  destroy: () => void;
}

const props = withDefaults(defineProps<Props>(), {
  duration: 3000,
  position: "top",
  closable: false,
});

const isVisible = ref(false);
const timer = ref<NodeJS.Timeout>();

// @ts-ignore
const icons = {
  success: "i-tabler-circle-check",
  warning: "i-tabler-alert-octagon",
  error: "i-tabler-circle-dashed-x",
} as const;

const styles = {
  success: {
    iconWrapperClass: "text-green-700",
    textClass: "text-green-700",
    closeButtonClass: "bg-green-600/10 text-green-700 hover:bg-green-600/20",
    containerClass:
      "bg-green-100 border border-green-200/50 shadow-lg shadow-green-500/10",
  },
  warning: {
    iconWrapperClass: "text-amber-700",
    textClass: "text-amber-700",
    closeButtonClass: "bg-amber-600/10 text-amber-700 hover:bg-amber-600/20",
    containerClass:
      "bg-amber-100 border border-amber-200/50 shadow-lg shadow-amber-500/10",
  },
  error: {
    iconWrapperClass: "text-red-700",
    textClass: "text-red-700",
    closeButtonClass: "bg-red-600/10 text-red-700 hover:bg-red-600/20",
    containerClass:
      "bg-red-100 border border-red-200/50 shadow-lg shadow-red-500/10",
  },
};

const positionClass = computed(() => ({
  "top-4": props.position === "top",
  "bottom-4": props.position === "bottom",
}));

const handleClose = () => {
  isVisible.value = false;
  if (timer.value) {
    clearTimeout(timer.value);
  }
  props.onClose?.();
};

onMounted(() => {
  isVisible.value = true;
  if (props.duration > 0) {
    timer.value = setTimeout(() => {
      handleClose();
    }, props.duration);
  }
});
</script>

<style scoped>
.message-down-enter-active,
.message-down-leave-active,
.message-up-enter-active,
.message-up-leave-active {
  transition: all 0.4s cubic-bezier(0.33, 1, 0.68, 1);
}

.message-down-enter-from,
.message-down-leave-to {
  opacity: 0;
  transform: translate(-50%, -30px);
  filter: blur(4px);
}

.message-up-enter-from,
.message-up-leave-to {
  opacity: 0;
  transform: translate(-50%, 30px);
  filter: blur(4px);
}

@keyframes bounce-once {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-3px);
  }
}

.animate-bounce-once {
  animation: bounce-once 0.6s ease-in-out;
}
</style>
