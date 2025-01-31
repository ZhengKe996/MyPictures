<template>
  <div>DetailPicture</div>
</template>

<script setup lang="ts">
const { id } = defineProps<{
  id: string;
}>();
console.log("id", id);
</script>

<style scoped></style>
