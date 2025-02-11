import { createApp } from "vue";
import App from "./App.vue";
import { createPinia } from "pinia";
import router from "./router";

import piniaPluginPersistedstate from "pinia-plugin-persistedstate";

import "./assets/basic.css";
import "virtual:uno.css";
import "normalize.css";
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

createApp(App).use(pinia).use(router).mount("#app");
