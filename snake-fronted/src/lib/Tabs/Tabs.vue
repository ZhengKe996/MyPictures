<template>
  <div>
    <!-- Mobile View -->
    <div class="grid grid-cols-1 sm:hidden">
      <select
        :value="modelValue"
        @change="handleSelectChange"
        aria-label="Select a tab"
        class="col-start-1 row-start-1 w-full appearance-none rounded-md bg-white py-2 pr-8 pl-3 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600"
      >
        <option v-for="tab in normalizedTabs" :key="tab.name" :value="tab.name">
          {{ tab.name }}
        </option>
      </select>
    </div>

    <!-- Desktop View -->
    <div class="hidden sm:block">
      <div class="border-b border-gray-200">
        <nav class="-mb-px flex" aria-label="Tabs" role="tablist">
          <a
            v-for="tab in normalizedTabs"
            :key="tab.name"
            :href="tab.href"
            role="tab"
            :aria-selected="isActive(tab.name)"
            :aria-controls="`panel-${tab.name}`"
            :class="[
              isActive(tab.name)
                ? 'border-indigo-500 text-indigo-600'
                : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700',
              'w-1/4 border-b-2 px-1 py-4 text-center text-sm font-medium transition-all duration-200',
              { 'cursor-not-allowed opacity-50': tab.disabled },
            ]"
            @click.prevent="handleTabClick(tab)"
          >
            <slot name="tab" :tab="tab">
              {{ tab.name }}
            </slot>
          </a>
        </nav>
      </div>
    </div>

    <!-- Tab Panels -->
    <div v-if="showContent" class="mt-4">
      <div
        v-for="tab in normalizedTabs"
        :key="tab.name"
        :id="`panel-${tab.name}`"
        role="tabpanel"
        :class="[
          'transition-opacity duration-200',
          isActive(tab.name) ? 'opacity-100' : 'hidden opacity-0',
        ]"
      >
        <slot :name="tab.name" :tab="tab">
          {{ tab.content }}
        </slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import type { TabItem } from "./index";

// Props 定义
const props = withDefaults(
  defineProps<{
    modelValue?: string;
    tabs: (TabItem | string)[];
    showContent?: boolean;
    defaultTab?: string;
  }>(),
  {
    showContent: true,
    defaultTab: "",
  }
);

// Emits 定义
const emit = defineEmits<{
  (e: "update:modelValue", value: string): void;
  (e: "change", value: TabItem): void;
  (e: "click", tab: TabItem): void;
}>();

// 标准化 tabs 数据
const normalizedTabs = computed(() => {
  return props.tabs.map((tab) => {
    if (typeof tab === "string") {
      return {
        name: tab,
        href: `#${tab}`,
        content: "",
        disabled: false,
      };
    }
    return {
      href: `#${tab.name}`,
      disabled: false,
      content: "",
      ...tab,
    };
  });
});

// 判断标签是否激活
const isActive = (tabName: string) => {
  if (props.modelValue) {
    return props.modelValue === tabName;
  }
  if (props.defaultTab) {
    return props.defaultTab === tabName;
  }
  // 如果没有设置 modelValue 和 defaultTab，返回第一个标签
  return normalizedTabs.value[0]?.name === tabName;
};

// 处理标签点击
const handleTabClick = (tab: TabItem) => {
  if (tab.disabled) return;

  emit("click", tab);
  emit("update:modelValue", tab.name);
  emit("change", tab);
};

// 处理移动端选择
const handleSelectChange = (event: Event) => {
  const target = event.target as HTMLSelectElement;
  const selectedTab = normalizedTabs.value.find(
    (tab) => tab.name === target.value
  );
  if (selectedTab) {
    handleTabClick(selectedTab);
  }
};
</script>
