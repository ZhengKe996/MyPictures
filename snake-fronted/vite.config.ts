import { defineConfig } from "vite";
import { resolve } from "path";
import UnoCSS from "unocss/vite";
import vue from "@vitejs/plugin-vue";

// https://vite.dev/config/
export default defineConfig(({ command }) => {
  return {
    plugins: [vue(), UnoCSS()],
    resolve: {
      alias: {
        "@": resolve(__dirname, "src"), // 路径别名
      },
    },
    server: {
      proxy:
        command === "serve"
          ? {
              "/api": {
                target: "http://localhost:8080",
                changeOrigin: true,
              },
            }
          : undefined,
    },
    build: {
      outDir: "../snake-backed/src/main/resources/static",
      emptyOutDir: true,
      rollupOptions: {
        output: {
          manualChunks(id) {
            if (id.includes("node_modules")) {
              return id
                .toString()
                .split("node_modules/")[1]
                .split("/")[0]
                .toString();
            }
          },
        },
      },
      chunkSizeWarningLimit: 1000, // 将警告阈值设置为 1000 kB
    },
  };
});
