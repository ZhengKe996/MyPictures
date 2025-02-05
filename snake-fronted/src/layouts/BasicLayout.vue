<template>
  <div class="flex flex-col min-h-screen">
    <SideNav />
    <div class="lg:pl-48 flex-grow">
      <div
        class="sticky top-0 z-40 flex h-16 shrink-0 items-center gap-x-4 border-b border-gray-200 bg-white px-2 shadow-sm sm:gap-x-6"
      >
        <GlobalHeader></GlobalHeader>
      </div>

      <main class="pt-6 flex-grow pb-24">
        <div class="mx-auto max-w-[1280px] w-full h-full">
          <router-view class="w-full h-full"></router-view>
        </div>
      </main>
    </div>
    <footer class="fixed bottom-0 left-0 right-0 bg-white shadow-md">
      <div class="w-full flex justify-center">
        <GlobalFooter></GlobalFooter>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import GlobalFooter from "@/components/GlobalFooter";
import GlobalHeader from "@/components/GlobalHeader";
import SideNav from "@/components/SideNav";
</script>
