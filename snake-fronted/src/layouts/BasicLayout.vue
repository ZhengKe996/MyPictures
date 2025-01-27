<template>
  <div class="flex flex-col min-h-screen">
    <!-- Static sidebar for desktop -->
    <div
      class="hidden lg:fixed lg:inset-y-0 lg:z-50 lg:flex lg:w-56 lg:flex-col"
    >
      <!-- Sidebar component, swap this element with another sidebar if you like -->
      <div
        class="flex grow flex-col gap-y-5 overflow-y-auto border-r border-gray-200 bg-white px-6 pb-4"
      >
        <div class="flex h-16 shrink-0 items-center">
          <a href="#" class="-m-1.5 p-1.5">
            <span class="text-sm/6 mx-2 font-semibold text-gray-900"
              ><span class="text-4xl">Snake</span>Images</span
            >
          </a>
        </div>
        <nav class="flex flex-1 flex-col">
          <ul role="list" class="flex flex-1 flex-col gap-y-7">
            <li>
              <ul role="list" class="-mx-2 space-y-1">
                <li v-for="item in navigation" :key="item.name">
                  <a
                    :href="item.href"
                    :class="[
                      item.current
                        ? 'bg-gray-50 text-indigo-600'
                        : 'text-gray-700 hover:bg-gray-50 hover:text-indigo-600',
                      'group flex gap-x-3 rounded-md p-2 text-sm/6 font-semibold',
                    ]"
                  >
                    {{ item.name }}
                  </a>
                </li>
              </ul>
            </li>
            <li class="mt-auto">
              <a
                href="#"
                class="group -mx-2 flex gap-x-3 rounded-md p-2 text-sm/6 font-semibold text-gray-700 hover:bg-gray-50 hover:text-indigo-600"
              >
                <i
                  class="i-tabler:settings size-6 shrink-0 text-gray-400 group-hover:text-indigo-600"
                ></i>
                Settings
              </a>
            </li>
          </ul>
        </nav>
      </div>
    </div>

    <div class="lg:pl-56 flex-grow">
      <div
        class="sticky top-0 z-40 flex h-16 shrink-0 items-center gap-x-4 border-b border-gray-200 bg-white px-2 shadow-sm sm:gap-x-6 sm:px-6 lg:px-8"
      >
        <GlobalHeader></GlobalHeader>
      </div>

      <main class="pt-6 flex-grow pb-24">
        <div class="mx-auto max-w-[1280px] w-full h-full sm:px-6 lg:px-8">
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

const navigation = [
  { name: "Dashboard", href: "#", icon: "", current: true },
  { name: "Team", href: "#", icon: "", current: false },
  { name: "Projects", href: "#", icon: "", current: false },
  { name: "Calendar", href: "#", icon: "", current: false },
  { name: "Documents", href: "#", icon: "", current: false },
  { name: "Reports", href: "#", icon: "", current: false },
];
</script>

<style scoped>
main {
  padding-bottom: 6rem; /* 为 footer 预留一些空间 */
}
</style>
