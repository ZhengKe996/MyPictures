<template>
  <div class="bg-fcfcfc text-12161e h-full">
    <div class="bg-fcfcfc text-12161e h-screen overflow-y-auto relative">
      <div
        class="relative min-h-screen flex justify-center items-center box-border"
      >
        <AppBackToLogin />
        <div class="relative flex-grow">
          <div class="max-w-100 w-full my-0 mx-auto py-0 px-5 box-border">
            <RouterView />
          </div>
        </div>
        <p
          class="transition duration-200 m-0 absolute z-10 left-2/4 text-center translate-x-[-50%] bottom-6 text-[10px] max-w-[260px] text-[#c2c3c7]"
        >
          This site is authenticate by supabase.com and the Supabase

          <a
            class="no-underline transition duration-200 text-[#a3a5ab] hover:text-[#656976]"
            target="_blank"
            href="#"
            rel="noreferrer"
          >
            Privacy Policy
          </a>
          and
          <a
            class="no-underline transition duration-200 text-[#a3a5ab] hover:text-[#656976]"
            target="_blank"
            href="#"
            rel="noreferrer"
          >
            Terms of Service
          </a>
          apply.
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { RouterView } from "vue-router";
import AppBackToLogin from "@/components/AppBackToLogin";
</script>
