<template>
  <div>DetailPicture</div>
</template>

<script setup lang="ts">
const { id } = defineProps<{
  id: string | number;
}>();
console.log("id", id);
</script>

<style scoped></style>
