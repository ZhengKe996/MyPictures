<template>
  <div class="tags-list-container">
    <TransitionGroup name="tag-list" tag="div" class="flex flex-wrap gap-2">
      <Tag
        v-for="(tag, index) in tags"
        :key="tag.id"
        :text="tag.name"
        :variant="tag.variant"
        :custom-color="getTagColor(index)"
        :clickable="true"
        :class="[
          'transform transition-all duration-200',
          isSelected(tag) ? 'scale-105 ring-2 ring-offset-2' : '',
        ]"
        @click="handleTagClick(tag)"
      />
    </TransitionGroup>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import Tag from "./Tag.vue";
import { getRandomColor } from "./index";
import type { EnhancedTag } from "./index";

interface Props {
  tags: EnhancedTag[];
  modelValue?: EnhancedTag[];
  multiple?: boolean;
  randomColors?: boolean;
}

interface Emits {
  (e: "update:modelValue", value: EnhancedTag[]): void;
  (e: "change", value: EnhancedTag[]): void;
  (e: "tag-click", value: EnhancedTag): void;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => [],
  multiple: false,
  randomColors: true,
});

const emit = defineEmits<Emits>();
const selectedTags = ref<EnhancedTag[]>(props.modelValue);
const colorCache = ref<Record<number, string>>({});

const getTagColor = (index: number) => {
  if (!props.randomColors) return undefined;
  if (!colorCache.value[index]) {
    colorCache.value[index] = getRandomColor();
  }
  return colorCache.value[index];
};

const isSelected = (tag: EnhancedTag): boolean => {
  return selectedTags.value.some((t) => t.id === tag.id);
};

const handleTagClick = (tag: EnhancedTag) => {
  emit("tag-click", tag);

  if (props.multiple) {
    const index = selectedTags.value.findIndex((t) => t.id === tag.id);
    if (index === -1) {
      selectedTags.value = [...selectedTags.value, tag];
    } else {
      selectedTags.value = selectedTags.value.filter((t) => t.id !== tag.id);
    }
  } else {
    selectedTags.value = [tag];
  }

  emit("update:modelValue", selectedTags.value);
  emit("change", selectedTags.value);
};

watch(
  () => props.modelValue,
  (newValue) => {
    selectedTags.value = newValue;
  },
  { deep: true }
);
</script>

<style scoped>
.tag-list-enter-active,
.tag-list-leave-active {
  transition: all 0.4s ease;
}

.tag-list-enter-from,
.tag-list-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

.tag-list-move {
  transition: transform 0.4s ease;
}
</style>
