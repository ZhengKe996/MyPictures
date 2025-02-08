<template>
  <div
    class="flex min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-300"
  >
    <SideNav />
    <div class="flex-1 flex flex-col lg:ml-48 overflow-hidden">
      <header
        class="sticky top-0 z-40 bg-white/80 dark:bg-gray-800/80 backdrop-blur-md shadow-subtle transition-all duration-300 ease-in-out"
      >
        <div class="flex h-16 items-center px-4 sm:px-6 lg:px-8">
          <GlobalHeader />
        </div>
      </header>

      <main
        class="flex-1 overflow-y-auto scroll-smooth px-4 sm:px-6 lg:px-8 py-6 space-y-6 relative"
      >
        <router-view v-slot="{ Component }">
          <transition
            mode="out-in"
            enter-active-class="transition ease-out duration-200"
            enter-from-class="opacity-0 scale-95"
            enter-to-class="opacity-100 scale-100"
            leave-active-class="transition ease-in duration-150"
            leave-from-class="opacity-100 scale-100"
            leave-to-class="opacity-0 scale-95"
          >
            <component
              :is="Component"
              :key="$route.path"
              class="w-full h-full"
            />
          </transition>
        </router-view>
      </main>

      <footer
        class="w-full bg-white/60 dark:bg-gray-800/60 backdrop-blur-sm p-4 text-center transition-all duration-300 ease-in-out hover:shadow-lg"
      >
        <GlobalFooter />
      </footer>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineAsyncComponent } from "vue";
import GlobalFooter from "@/components/GlobalFooter/GlobalFooter.vue";
import GlobalHeader from "@/components/GlobalHeader/GlobalHeader.vue";
import SideNav from "@/components/SideNav/SideNav.vue";

defineProps({
  title: {
    type: String,
    default: "MyPictures",
  },
});
</script>

<style scoped>
/* Additional subtle animations or transitions can be added here if needed */
.shadow-subtle {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

footer {
  animation: fadeInUp 0.5s ease-out;
}
</style>
