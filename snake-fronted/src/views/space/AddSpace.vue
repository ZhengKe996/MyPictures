<template>
  <div>
    { path: "/add/picture", name: "ADD Picture", component: () =>
    import("@/views/pictures/UploadPicture.vue"), meta: { layout:
    LayoutMenu.BasicLayout, access: ACCESSENUM.USER, isHeader: true, }, },
  </div>
</template>

<script setup lang="ts"></script>

<style scoped></style>
