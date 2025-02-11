<template>
  <div>
    <Disclosure
      as="div"
      class="relative overflow-hidden bg-sky-700 pb-32"
      v-slot="{ open }"
    >
      <div
        aria-hidden="true"
        :class="[
          open ? 'bottom-0' : 'inset-y-0',
          'absolute inset-x-0 left-1/2 w-full -translate-x-1/2 transform overflow-hidden lg:inset-y-0',
        ]"
      >
        <div class="absolute inset-0 flex">
          <div class="h-full w-1/2" style="background-color: #0a527b" />
          <div class="h-full w-1/2" style="background-color: #065d8c" />
        </div>
        <div class="relative flex justify-center">
          <svg
            class="shrink-0"
            width="1750"
            height="308"
            viewBox="0 0 1750 308"
          >
            <path
              d="M284.161 308H1465.84L875.001 182.413 284.161 308z"
              fill="#0369a1"
            />
            <path d="M1465.84 308L16.816 0H1750v308h-284.16z" fill="#065d8c" />
            <path d="M1733.19 0L284.161 308H0V0h1733.19z" fill="#0a527b" />
            <path
              d="M875.001 182.413L1733.19 0H16.816l858.185 182.413z"
              fill="#0a4f76"
            />
          </svg>
        </div>
      </div>
      <header class="relative py-10">
        <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <h1 class="text-3xl font-bold tracking-tight text-white">Settings</h1>
        </div>
      </header>
    </Disclosure>

    <main class="relative -mt-32">
      <div class="mx-auto max-w-screen-xl px-4 pb-6 sm:px-6 lg:px-8 lg:pb-16">
        <div class="overflow-hidden rounded-lg bg-white shadow">
          <div
            class="divide-y divide-gray-200 lg:grid lg:grid-cols-12 lg:divide-x lg:divide-y-0"
          >
            <form
              class="divide-y divide-gray-200 lg:col-span-9"
              action="#"
              method="POST"
            >
              <!-- Profile section -->
              <div class="px-4 py-6 sm:p-6 lg:pb-8">
                <div>
                  <h2 class="text-lg/6 font-medium text-gray-900">Profile</h2>
                  <p class="mt-1 text-sm text-gray-500">
                    This information will be displayed publicly so be careful
                    what you share.
                  </p>
                </div>

                <div class="mt-6 flex flex-col lg:flex-row">
                  <div class="grow space-y-6">
                    <div>
                      <label
                        for="username"
                        class="block text-sm/6 font-medium text-gray-900"
                        >Username</label
                      >
                      <div class="mt-2">
                        <div
                          class="flex items-center rounded-md bg-white pl-3 outline outline-1 -outline-offset-1 outline-gray-300 focus-within:outline focus-within:outline-2 focus-within:-outline-offset-2 focus-within:outline-sky-600"
                        >
                          <div
                            class="shrink-0 select-none text-base text-gray-500 sm:text-sm/6"
                          >
                            workcation.com/
                          </div>
                          <input
                            type="text"
                            name="username"
                            id="username"
                            class="block min-w-0 grow py-1.5 pl-1 pr-3 text-base text-gray-900 placeholder:text-gray-400 focus:outline focus:outline-0 sm:text-sm/6"
                            placeholder="janesmith"
                            :value="user.handle"
                          />
                        </div>
                      </div>
                    </div>

                    <div>
                      <label
                        for="about"
                        class="block text-sm/6 font-medium text-gray-900"
                        >About</label
                      >
                      <div class="mt-2">
                        <textarea
                          name="about"
                          id="about"
                          rows="3"
                          class="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline focus:outline-2 focus:-outline-offset-2 focus:outline-sky-600 sm:text-sm/6"
                        />
                      </div>
                      <p class="mt-3 text-sm/6 text-gray-500">
                        Write a few sentences about yourself.
                      </p>
                    </div>
                  </div>

                  <div class="mt-6 grow lg:ml-6 lg:mt-0 lg:shrink-0 lg:grow-0">
                    <p
                      class="text-sm/6 font-medium text-gray-900"
                      aria-hidden="true"
                    >
                      Photo
                    </p>
                    <div class="mt-2 lg:hidden">
                      <div class="flex items-center">
                        <div
                          class="inline-block size-12 shrink-0 overflow-hidden rounded-full"
                          aria-hidden="true"
                        >
                          <img
                            class="size-full rounded-full"
                            :src="user.imageUrl"
                            alt=""
                          />
                        </div>
                        <div class="relative ml-5">
                          <input
                            id="mobile-user-photo"
                            name="user-photo"
                            type="file"
                            class="peer absolute size-full rounded-md opacity-0"
                          />
                          <label
                            for="mobile-user-photo"
                            class="pointer-events-none block rounded-md px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 peer-hover:ring-gray-400 peer-focus:ring-2 peer-focus:ring-sky-500"
                          >
                            <span>Change</span>
                            <span class="sr-only"> user photo</span>
                          </label>
                        </div>
                      </div>
                    </div>

                    <div
                      class="relative hidden overflow-hidden rounded-full lg:block"
                    >
                      <img
                        class="relative size-40 rounded-full"
                        :src="user.imageUrl"
                        alt=""
                      />
                      <label
                        for="desktop-user-photo"
                        class="absolute inset-0 flex size-full items-center justify-center bg-black/75 text-sm font-medium text-white opacity-0 focus-within:opacity-100 hover:opacity-100"
                      >
                        <span>Change</span>
                        <span class="sr-only"> user photo</span>
                        <input
                          type="file"
                          id="desktop-user-photo"
                          name="user-photo"
                          class="absolute inset-0 size-full cursor-pointer rounded-md border-gray-300 opacity-0"
                        />
                      </label>
                    </div>
                  </div>
                </div>

                <div class="mt-6 grid grid-cols-12 gap-6">
                  <div class="col-span-12 sm:col-span-6">
                    <label
                      for="first-name"
                      class="block text-sm/6 font-medium text-gray-900"
                      >First name</label
                    >
                    <div class="mt-2">
                      <input
                        type="text"
                        name="first-name"
                        id="first-name"
                        autocomplete="given-name"
                        class="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline focus:outline-2 focus:-outline-offset-2 focus:outline-sky-600 sm:text-sm/6"
                      />
                    </div>
                  </div>

                  <div class="col-span-12 sm:col-span-6">
                    <label
                      for="last-name"
                      class="block text-sm/6 font-medium text-gray-900"
                      >Last name</label
                    >
                    <div class="mt-2">
                      <input
                        type="text"
                        name="last-name"
                        id="last-name"
                        autocomplete="family-name"
                        class="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline focus:outline-2 focus:-outline-offset-2 focus:outline-sky-600 sm:text-sm/6"
                      />
                    </div>
                  </div>

                  <div class="col-span-12">
                    <label
                      for="url"
                      class="block text-sm/6 font-medium text-gray-900"
                      >URL</label
                    >
                    <div class="mt-2">
                      <input
                        type="text"
                        name="url"
                        id="url"
                        class="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline focus:outline-2 focus:-outline-offset-2 focus:outline-sky-600 sm:text-sm/6"
                      />
                    </div>
                  </div>

                  <div class="col-span-12 sm:col-span-6">
                    <label
                      for="company"
                      class="block text-sm/6 font-medium text-gray-900"
                      >Company</label
                    >
                    <div class="mt-2">
                      <input
                        type="text"
                        name="company"
                        id="company"
                        autocomplete="organization"
                        class="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline focus:outline-2 focus:-outline-offset-2 focus:outline-sky-600 sm:text-sm/6"
                      />
                    </div>
                  </div>
                </div>
              </div>

              <!-- Privacy section -->
              <div class="divide-y divide-gray-200 pt-6">
                <div class="px-4 sm:px-6">
                  <div>
                    <h2 class="text-lg/6 font-medium text-gray-900">Privacy</h2>
                    <p class="mt-1 text-sm text-gray-500">
                      Ornare eu a volutpat eget vulputate. Fringilla commodo
                      amet.
                    </p>
                  </div>
                  <ul role="list" class="mt-2 divide-y divide-gray-200">
                    <SwitchGroup
                      as="li"
                      class="flex items-center justify-between py-4"
                    >
                      <div class="flex flex-col">
                        <SwitchLabel
                          as="p"
                          class="text-sm/6 font-medium text-gray-900"
                          passive
                          >Available to hire</SwitchLabel
                        >
                        <SwitchDescription class="text-sm text-gray-500"
                          >Nulla amet tempus sit accumsan. Aliquet turpis sed
                          sit lacinia.</SwitchDescription
                        >
                      </div>
                      <Switch
                        v-model="availableToHire"
                        :class="[
                          availableToHire ? 'bg-teal-500' : 'bg-gray-200',
                          'relative ml-4 inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-sky-500 focus:ring-offset-2',
                        ]"
                      >
                        <span
                          aria-hidden="true"
                          :class="[
                            availableToHire ? 'translate-x-5' : 'translate-x-0',
                            'inline-block size-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out',
                          ]"
                        />
                      </Switch>
                    </SwitchGroup>
                    <SwitchGroup
                      as="li"
                      class="flex items-center justify-between py-4"
                    >
                      <div class="flex flex-col">
                        <SwitchLabel
                          as="p"
                          class="text-sm/6 font-medium text-gray-900"
                          passive
                          >Make account private</SwitchLabel
                        >
                        <SwitchDescription class="text-sm text-gray-500"
                          >Pharetra morbi dui mi mattis tellus sollicitudin
                          cursus pharetra.</SwitchDescription
                        >
                      </div>
                      <Switch
                        v-model="privateAccount"
                        :class="[
                          privateAccount ? 'bg-teal-500' : 'bg-gray-200',
                          'relative ml-4 inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-sky-500 focus:ring-offset-2',
                        ]"
                      >
                        <span
                          aria-hidden="true"
                          :class="[
                            privateAccount ? 'translate-x-5' : 'translate-x-0',
                            'inline-block size-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out',
                          ]"
                        />
                      </Switch>
                    </SwitchGroup>
                    <SwitchGroup
                      as="li"
                      class="flex items-center justify-between py-4"
                    >
                      <div class="flex flex-col">
                        <SwitchLabel
                          as="p"
                          class="text-sm/6 font-medium text-gray-900"
                          passive
                          >Allow commenting</SwitchLabel
                        >
                        <SwitchDescription class="text-sm text-gray-500"
                          >Integer amet, nunc hendrerit adipiscing nam.
                          Elementum ame</SwitchDescription
                        >
                      </div>
                      <Switch
                        v-model="allowCommenting"
                        :class="[
                          allowCommenting ? 'bg-teal-500' : 'bg-gray-200',
                          'relative ml-4 inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-sky-500 focus:ring-offset-2',
                        ]"
                      >
                        <span
                          aria-hidden="true"
                          :class="[
                            allowCommenting ? 'translate-x-5' : 'translate-x-0',
                            'inline-block size-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out',
                          ]"
                        />
                      </Switch>
                    </SwitchGroup>
                    <SwitchGroup
                      as="li"
                      class="flex items-center justify-between py-4"
                    >
                      <div class="flex flex-col">
                        <SwitchLabel
                          as="p"
                          class="text-sm/6 font-medium text-gray-900"
                          passive
                          >Allow mentions</SwitchLabel
                        >
                        <SwitchDescription class="text-sm text-gray-500"
                          >Adipiscing est venenatis enim molestie commodo eu
                          gravid</SwitchDescription
                        >
                      </div>
                      <Switch
                        v-model="allowMentions"
                        :class="[
                          allowMentions ? 'bg-teal-500' : 'bg-gray-200',
                          'relative ml-4 inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-sky-500 focus:ring-offset-2',
                        ]"
                      >
                        <span
                          aria-hidden="true"
                          :class="[
                            allowMentions ? 'translate-x-5' : 'translate-x-0',
                            'inline-block size-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out',
                          ]"
                        />
                      </Switch>
                    </SwitchGroup>
                  </ul>
                </div>
                <div class="mt-4 flex justify-end gap-x-3 px-4 py-4 sm:px-6">
                  <button
                    type="button"
                    class="inline-flex justify-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    class="inline-flex justify-center rounded-md bg-sky-700 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-sky-600 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-sky-700"
                  >
                    Save
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import {
  Disclosure,
  DisclosureButton,
  DisclosurePanel,
  Menu,
  MenuButton,
  MenuItem,
  MenuItems,
  Switch,
  SwitchDescription,
  SwitchGroup,
  SwitchLabel,
} from "@headlessui/vue";

const user = {
  name: "Debbie Lewis",
  handle: "deblewis",
  email: "debbielewis@example.com",
  imageUrl:
    "https://images.unsplash.com/photo-1517365830460-955ce3ccd263?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=4&w=320&h=320&q=80",
};
const navigation = [
  { name: "Dashboard", href: "#", current: true },
  { name: "Jobs", href: "#", current: false },
  { name: "Applicants", href: "#", current: false },
  { name: "Company", href: "#", current: false },
];

const userNavigation = [
  { name: "Your Profile", href: "#" },
  { name: "Settings", href: "#" },
  { name: "Sign out", href: "#" },
];

const availableToHire = ref(true);
const privateAccount = ref(false);
const allowCommenting = ref(true);
const allowMentions = ref(true);
</script>
