<template>
  <div class="bg-fcfcfc text-12161e h-full overflow-hidden">
    <!-- 添加 SVG 背景 -->
    <div class="waves-background fixed inset-0 z-0">
      <svg
        class="waves"
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 1440 320"
        preserveAspectRatio="none"
      >
        <path
          class="wave1"
          d="M0,160L48,165.3C96,171,192,181,288,181.3C384,181,480,171,576,181.3C672,192,768,224,864,229.3C960,235,1056,213,1152,208C1248,203,1344,213,1392,218.7L1440,224L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"
        />
        <path
          class="wave2"
          d="M0,192L48,197.3C96,203,192,213,288,229.3C384,245,480,267,576,266.7C672,267,768,245,864,229.3C960,213,1056,203,1152,208C1248,213,1344,235,1392,245.3L1440,256L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"
        />
        <path
          class="wave3"
          d="M0,224L48,229.3C96,235,192,245,288,261.3C384,277,480,299,576,293.3C672,288,768,256,864,245.3C960,235,1056,245,1152,250.7C1248,256,1344,256,1392,256L1440,256L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"
        />
      </svg>
    </div>
    <div
      class="bg-transparent text-12161e h-screen overflow-y-auto relative z-10"
    >
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

<style scoped>
.waves-background {
  transform: rotate(180deg);
}

.waves {
  width: 100%;
  height: 100%;
  position: absolute;
}

.wave1 {
  fill: rgba(100, 149, 237, 0.03);
  animation: wave-animation 15s infinite linear;
}

.wave2 {
  fill: rgba(100, 149, 237, 0.05);
  animation: wave-animation 10s infinite linear;
}

.wave3 {
  fill: rgba(100, 149, 237, 0.07);
  animation: wave-animation 7s infinite linear;
}

@keyframes wave-animation {
  0% {
    transform: translateX(0);
  }
  50% {
    transform: translateX(-5%);
  }
  100% {
    transform: translateX(-10%) translateZ(0);
  }
}
</style>
