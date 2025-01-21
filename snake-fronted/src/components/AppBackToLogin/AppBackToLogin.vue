<template>
  <div
    class="z-10 fixed left-0 top-0 right-0 p-6 box-border flex items-center justify-between transition-all duration-200"
  >
    <RouterLink
      :to="currentPath.patch"
      class="text-[var(--button-color,#91949b)] no-underline relative text-sm font-[inherit] tracking-[-0.125px] py-[10px] pr-4 pl-3 font-semibold transition duration-200 opacity-100 active:scale-50"
      ><div
        class="relative z-10 flex items-center gap-2 min-h-[20px] transition duration-200"
      >
        <i class="i-tabler:chevron-left size-5"></i>
        {{ currentPath.text }}
      </div>
    </RouterLink>
  </div>
</template>

<script setup lang="ts">
import { reactive, watch, onMounted } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();

const currentPath = reactive({ patch: "/user/register", text: "Register" });

const setCurrentPath = () => {
  if (route.path.includes("register")) {
    currentPath.patch = "/user/login";
    currentPath.text = "Login";
  } else {
    currentPath.patch = "/user/register";
    currentPath.text = "Register";
  }
};

watch(
  () => route.path,
  () => setCurrentPath()
);
onMounted(() => setCurrentPath());
</script>

<style scoped></style>
