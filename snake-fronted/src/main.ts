import { createApp } from "vue";
import App from "./App.vue";
import { createPinia } from "pinia";
import router from "./router";

import Antd from "ant-design-vue";
import "ant-design-vue/dist/reset.css";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";

import "virtual:uno.css";
import "normalize.css";
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

createApp(App).use(Antd).use(pinia).use(router).mount("#app");
