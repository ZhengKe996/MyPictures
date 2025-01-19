import { createApp } from "vue";
import App from "./App.vue";
import { createPinia } from "pinia";
import router from "./router";

import "virtual:uno.css";
import "normalize.css";
const pinia = createPinia();

createApp(App).use(pinia).use(router).mount("#app");
