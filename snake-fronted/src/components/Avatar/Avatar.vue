<template>
  <a href="#" class="group block shrink-0">
    <div class="flex items-center">
      <div>
        <img
          class="inline-block size-9 rounded-full"
          :src="userAvatar"
          alt=""
        />
      </div>
      <div class="ml-3 max-w-24 overflow-hidden text-nowrap">
        <p
          class="text-sm font-medium text-gray-700 group-hover:text-gray-900 truncate"
        >
          {{ userName }}
        </p>
        <p
          class="text-xs font-medium text-gray-500 group-hover:text-gray-700 truncate"
        >
          {{ userRole }}
        </p>
      </div>
    </div>
  </a>
</template>

<script setup lang="ts">
import { ACCESSENUM, NOLOGIN } from "@/access";
import { DefaultUserAvatar } from "@/config";
interface UserInfo {
  userName: string;
  userRole: ACCESSENUM;
  userAvatar: string;
}
const {
  userName = NOLOGIN,
  userRole = ACCESSENUM.NOLOGIN,
  userAvatar = DefaultUserAvatar,
} = defineProps<UserInfo>();
</script>
