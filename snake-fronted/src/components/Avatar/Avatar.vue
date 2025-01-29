<template>
  <component
    :is="props.linkable ? 'a' : 'div'"
    :href="props.linkable ? props.href : undefined"
    :class="[
      'group block',
      props.linkable ? 'cursor-pointer' : 'cursor-default',
      props.customClass,
    ]"
  >
    <div class="flex items-center gap-3">
      <div
        class="relative overflow-hidden"
        :style="{
          width: avatarConfig.dimensions[props.size].width,
          height: avatarConfig.dimensions[props.size].height,
        }"
      >
        <img
          :class="[
            'rounded-full object-cover w-full h-full transition-all',
            avatarConfig.transitions.duration,
            avatarConfig.transitions.timing,
            'group-hover:ring-2 group-hover:ring-primary-300',
          ]"
          :src="props.userAvatar"
          :alt="props.userName"
        />
      </div>
      <div class="min-w-0 flex-1">
        <p
          class="truncate text-sm font-semibold text-gray-700 transition-colors group-hover:text-gray-900"
        >
          {{ props.userName }}
        </p>
        <p
          v-if="props.showRole"
          class="truncate text-xs font-medium text-gray-500 transition-colors group-hover:text-gray-700"
        >
          {{ props.userRole }}
        </p>
      </div>
    </div>
  </component>
</template>

<script setup lang="ts">
import { avatarConfig, defaultProps } from "./config";
import type { AvatarProps } from "./types";

const props = withDefaults(defineProps<AvatarProps>(), defaultProps);
</script>
