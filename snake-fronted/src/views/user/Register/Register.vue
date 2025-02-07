<template>
  <div class="grid gap-8 text-center">
    <h1
      class="font-[inherit] m-0 leading-[1.4] tracking-[-0.125px] text-[#12161e] font-semibold text-xl"
    >
      Create an account
    </h1>
    <div
      class="flex min-h-full flex-1 flex-col justify-center px-2 py-4 lg:px-8"
    >
      <div class="sm:mx-auto sm:w-full sm:max-w-sm">
        <div class="space-y-6">
          <!-- Account Input -->
          <div>
            <label
              for="Account"
              class="block text-sm/6 text-left font-medium text-gray-900"
              >Account</label
            >
            <div class="mt-2">
              <input
                type="text"
                placeholder="please enter your account..."
                v-model="form.userAccount"
                class="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline focus:outline-2 focus:-outline-offset-2 focus:outline-blue-600 sm:text-sm/6"
              />
            </div>
          </div>

          <!-- Password Input -->
          <div>
            <div class="flex items-center justify-between">
              <label
                for="password"
                class="block text-sm/6 font-medium text-gray-900"
                >Password</label
              >
            </div>
            <div class="mt-2 relative flex items-center">
              <button
                class="absolute right-0 focus:outline-none"
                @click="togglePasswordVisibility('password')"
              >
                <div
                  class="w-4 h-4 mx-4 text-gray-400 transition-colors duration-300 dark:text-gray-500 hover:text-gray-500 dark:hover:text-gray-400"
                >
                  <i
                    v-if="showPasswordMode"
                    class="i-tabler:eye-off w-4 h-4"
                  ></i>
                  <i v-else class="i-tabler:eye w-4 h-4"></i>
                </div>
              </button>
              <input
                :type="showPasswordMode ? 'text' : 'password'"
                placeholder="please enter your password"
                v-model="form.userPassword"
                class="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline focus:outline-2 focus:-outline-offset-2 focus:outline-blue-600 sm:text-sm/6"
              />
            </div>
          </div>

          <!-- Check Password Input -->
          <div>
            <div class="flex items-center justify-between">
              <label
                for="password"
                class="block text-sm/6 font-medium text-gray-900"
                >CheckPassword</label
              >
            </div>
            <div class="mt-2 relative flex items-center">
              <button
                class="absolute right-0 focus:outline-none"
                @click="togglePasswordVisibility('checkPassword')"
              >
                <div
                  class="w-4 h-4 mx-4 text-gray-400 transition-colors duration-300 dark:text-gray-500 hover:text-gray-500 dark:hover:text-gray-400"
                >
                  <i
                    v-if="showCheckPasswordMode"
                    class="i-tabler:eye-off w-4 h-4"
                  ></i>
                  <i v-else class="i-tabler:eye w-4 h-4"></i>
                </div>
              </button>
              <input
                :type="showCheckPasswordMode ? 'text' : 'password'"
                placeholder="please enter your password"
                v-model="form.checkPassword"
                class="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline focus:outline-2 focus:-outline-offset-2 focus:outline-blue-600 sm:text-sm/6"
              />
            </div>
          </div>

          <!-- Submit Button -->
          <div>
            <button
              @click="handleSubmit"
              class="flex w-full justify-center rounded-md bg-blue-600 px-3 py-1.5 text-sm/6 font-semibold text-white shadow-sm hover:bg-blue-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600"
            >
              Submit
            </button>
          </div>
        </div>

        <p class="mt-10 text-center text-sm/6 text-gray-500">
          <RouterLink
            to="/user/login"
            class="font-semibold text-blue-600 hover:text-blue-500"
          >
            Already have an account?
          </RouterLink>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { RouterLink } from "vue-router";
import { useRegisterForm, usePasswordVisibility } from "./hooks";

const { form, handleSubmit } = useRegisterForm();
const { showPasswordMode, showCheckPasswordMode, togglePasswordVisibility } =
  usePasswordVisibility();
</script>
