<template>
  <div>
    <p>Count: {{ counterStore.count }}</p>
    <p>Double Count: {{ counterStore.doubleCount }}</p>
    <button @click="counterStore.increment">Increment</button>
  </div>
</template>

<script setup lang="ts">
import { useCounterStore } from "@/store";
const counterStore = useCounterStore();
</script>

<style scoped></style>
