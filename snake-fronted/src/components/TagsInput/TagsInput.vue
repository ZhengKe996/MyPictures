<template>
  <div class="flex flex-col">
    <div class="flex items-center mb-2">
      <input
        v-model="newTag"
        @keydown.enter="addTag"
        @keydown.backspace="removeLastTag"
        placeholder="请输入标签"
        class="border p-2 rounded w-full focus:outline-none focus:ring-2 focus:ring-blue-500"
      />
    </div>
    <div class="flex flex-wrap">
      <BadgeRemove
        v-for="(tag, index) in tags"
        :key="index"
        :index="index"
        :text="tag"
        @remove="removeTag"
        class="my-1"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import BadgeRemove from "../BadgeRemove";
import { ref } from "vue";

const { tags = [] } = defineProps<{ tags: string[] }>();
const emits = defineEmits(["add", "remove"]);

// 定义新标签输入框的值
const newTag = ref<string>("");

// 添加标签的方法
const addTag = () => {
  if (newTag.value != "") emits("add", newTag.value);
};

// 删除标签的方法
const removeTag = (index: number) => {
  if (index >= 0 && index < tags.length) emits("remove", index);
};
// 删除最后一个标签的方法
const removeLastTag = (event: KeyboardEvent) => {
  if (event.key === "Backspace" && newTag.value === "" && tags.length > 0)
    emits("remove", tags.length - 1);
};
</script>
