<template>
  <div>Edit{{ id }}</div>
</template>

<script setup lang="ts">
const { id } = defineProps<{
  id: string;
}>();
</script>

<style scoped></style>
