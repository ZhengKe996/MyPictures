<template>
  <div
    class="relative w-[140px] group outline-none focus-visible:outline-none"
    @keydown.down.prevent="navigateOptions('next')"
    @keydown.up.prevent="navigateOptions('prev')"
    @keydown.enter.prevent="selectHighlighted"
    @keydown.space.prevent="selectHighlighted"
    @keydown.esc="closeDropdown"
    @keydown.tab="closeDropdown"
    tabindex="0"
    ref="containerRef"
  >
    <!-- Focus ring indicator -->
    <div
      class="absolute -inset-1 rounded-xl bg-indigo-50/50 opacity-0 transition-opacity duration-200 pointer-events-none group-focus-visible:opacity-100"
      aria-hidden="true"
    ></div>

    <!-- Label with improved focus effect -->
    <label
      v-if="label"
      class="relative block mb-1.5 text-sm font-medium text-gray-700 transition-all duration-200 ease-out group-focus-visible:text-indigo-600 group-focus-visible:translate-x-0.5"
    >
      {{ label }}
    </label>

    <!-- Main button with updated styles -->
    <button
      type="button"
      @click="toggleDropdown"
      :aria-expanded="isOpen"
      :aria-controls="listboxId"
      class="relative w-full flex items-center justify-between px-4 py-2.5 bg-white shadow-sm border border-gray-200 rounded-xl transition-all duration-200 ease-out hover:border-indigo-300 hover:bg-indigo-50/50 group-focus-visible:border-indigo-500 group-focus-visible:ring-4 group-focus-visible:ring-indigo-500/10 disabled:opacity-50 disabled:cursor-not-allowed data-[state=open]:border-indigo-500 data-[state=open]:bg-white data-[state=open]:shadow-md outline-none focus:outline-none"
      :class="[
        isOpen ? 'bg-white shadow-md' : 'bg-gray-50/80 backdrop-blur-sm',
      ]"
      :data-state="isOpen ? 'open' : 'closed'"
    >
      <div class="flex items-center gap-3 min-w-0">
        <div v-if="modelValue?.icon || modelValue?.avatar" class="relative">
          <i
            v-if="modelValue?.icon"
            :class="[
              modelValue.icon,
              'w-5 h-5 text-indigo-600 transition-transform duration-200 group-hover/button:scale-110',
            ]"
          />
          <img
            v-else-if="modelValue?.avatar"
            :src="modelValue.avatar"
            :alt="modelValue?.name"
            class="w-7 h-7 rounded-full object-cover ring-2 ring-white shadow-sm transition-transform duration-200 group-hover/button:scale-110"
          />
          <div
            class="absolute -inset-0.5 rounded-full bg-indigo-500/10 animate-pulse group-hover/button:animate-none"
          ></div>
        </div>
        <span
          v-if="modelValue?.name || placeholder"
          class="truncate text-sm font-medium"
          :class="[modelValue?.name ? 'text-gray-700' : 'text-gray-500']"
        >
          {{ modelValue?.name || placeholder }}
        </span>
      </div>
      <i
        class="i-tabler-chevron-down transform transition-all duration-200"
        :class="[
          isOpen ? 'rotate-180 text-indigo-500' : 'text-gray-400',
          'group-hover/button:text-indigo-500',
        ]"
      />
    </button>

    <!-- Dropdown with improved styles -->
    <Transition
      enter-active-class="transform-gpu transition-all duration-300 ease-out-back"
      enter-from-class="opacity-0 -translate-y-1 scale-95 blur-sm"
      enter-to-class="opacity-100 translate-y-0 scale-100 blur-0"
      leave-active-class="transform-gpu transition-all duration-200 ease-in-back"
      leave-from-class="opacity-100 translate-y-0 scale-100 blur-0"
      leave-to-class="opacity-0 -translate-y-1 scale-95 blur-sm"
    >
      <div v-if="isOpen" class="absolute z-50 w-full mt-2">
        <div class="relative select-menu-container">
          <!-- Top shadow -->
          <div
            class="absolute top-0 inset-x-0 h-4 bg-gradient-to-b from-white to-transparent pointer-events-none z-10 rounded-t-xl"
          ></div>

          <ul
            :id="listboxId"
            role="listbox"
            class="relative max-h-64 overflow-auto rounded-xl bg-white border border-gray-200 shadow-lg divide-y divide-gray-100 scrollbar-thin scrollbar-thumb-indigo-200 scrollbar-track-transparent focus:outline-none"
            :aria-activedescendant="highlightedId"
          >
            <li
              v-for="(option, index) in options"
              :key="option.id"
              :id="getOptionId(index)"
              @click="selectOption(option)"
              @mousemove="highlightedIndex = index"
              :class="[
                'px-4 py-2.5 flex items-center gap-3 text-sm',
                'transition-all duration-150 ease-out',
                'cursor-pointer select-none group/item relative',
                'outline-none focus:outline-none',
                highlightedIndex === index
                  ? ['bg-indigo-50', 'text-indigo-900']
                  : ['text-gray-700', 'hover:bg-gray-50'],
                isSelected(option) ? 'font-medium' : 'font-normal',
                !option.name ? 'justify-center' : '',
              ]"
              role="option"
              :aria-selected="isSelected(option)"
            >
              <!-- Highlight indicator -->
              <div
                v-if="highlightedIndex === index"
                class="absolute left-0 top-[10%] bottom-[10%] w-0.5 bg-indigo-500 highlighted-indicator"
              ></div>

              <!-- Option content -->
              <div
                v-if="option.icon || option.avatar"
                class="relative flex-shrink-0"
              >
                <i
                  v-if="option.icon"
                  :class="[
                    option.icon,
                    'w-5 h-5 transition-all duration-200',
                    highlightedIndex === index
                      ? 'text-indigo-600'
                      : 'text-gray-500',
                    'group-hover/item:scale-110',
                  ]"
                />
                <img
                  v-else-if="option.avatar"
                  :src="option.avatar"
                  :alt="option.name"
                  :class="[
                    'w-7 h-7 rounded-full object-cover shadow-sm',
                    'transition-all duration-200',
                    'group-hover/item:scale-105',
                    highlightedIndex === index
                      ? 'ring-2 ring-indigo-200/50'
                      : 'ring-2 ring-white',
                  ]"
                />
                <!-- Selection indicator for icon-only options -->
                <div
                  v-if="isSelected(option) && !option.name"
                  class="absolute -right-0.5 -bottom-0.5 w-3.5 h-3.5 bg-white rounded-full flex items-center justify-center shadow-sm ring-1 ring-indigo-100"
                >
                  <i class="i-tabler-check w-3 h-3 text-indigo-600" />
                </div>
              </div>

              <!-- Option text -->
              <div v-if="option.name" class="flex-1 min-w-0">
                <span class="block truncate">{{ option.name }}</span>
                <span
                  v-if="option.description"
                  class="block truncate text-xs text-gray-400 transition-colors duration-200 group-hover/item:text-gray-500"
                >
                  {{ option.description }}
                </span>
              </div>

              <!-- Check icon with animation (only for options with name) -->
              <i
                v-if="isSelected(option) && option.name"
                class="i-tabler-check text-indigo-600 opacity-0 scale-50 transition-all duration-200 group-hover/item:opacity-100 group-hover/item:scale-100"
              />
            </li>
          </ul>

          <!-- Bottom shadow -->
          <div
            class="absolute bottom-0 inset-x-0 h-4 bg-gradient-to-t from-white to-transparent pointer-events-none z-10 rounded-b-xl"
          ></div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { useSelectMenu, type SelectMenuProps, type SelectMenuEmits } from ".";

const props = defineProps<SelectMenuProps>();
const emit = defineEmits<SelectMenuEmits>();

const {
  isOpen,
  highlightedIndex,
  containerRef,
  listboxId,
  highlightedId,
  getOptionId,
  navigateOptions,
  selectHighlighted,
  toggleDropdown,
  selectOption,
  closeDropdown,
  isSelected,
} = useSelectMenu(props, emit);
</script>

<style scoped>
.select-menu-container {
  will-change: transform, opacity;
  animation: menuAppear 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes menuAppear {
  from {
    opacity: 0;
    transform: translateY(-8px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 高亮指示器动画 */
.highlighted-indicator {
  transform-origin: left;
  animation: indicatorSlide 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes indicatorSlide {
  from {
    transform: scaleY(0);
    opacity: 0;
  }
  to {
    transform: scaleY(1);
    opacity: 1;
  }
}
</style>
