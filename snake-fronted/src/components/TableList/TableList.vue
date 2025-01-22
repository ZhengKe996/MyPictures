<template>
  <table class="min-w-full divide-y divide-gray-300">
    <thead>
      <tr>
        <th
          v-for="item in columns"
          scope="col"
          class="sticky top-0 z-10 border-b border-gray-300 bg-white/75 px-3 py-3.5 text-left text-sm font-semibold text-gray-900"
        >
          {{ item.label }}
        </th>
      </tr>
    </thead>
    <tbody class="bg-white">
      <slot name="tr" class="even:bg-gray-50"></slot>
    </tbody>
  </table>
</template>

<script setup lang="ts">
import type { TableListProps } from "./index";
const { columns = [] } = defineProps<TableListProps<any>>();
</script>
