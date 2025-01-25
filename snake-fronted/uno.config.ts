import { defineConfig, presetUno, presetIcons } from "unocss";
import { animatedUno } from "animated-unocss";
export default defineConfig({
  presets: [
    presetUno(),
    presetIcons({
      warn: true,
      prefix: ["i-"],
      extraProperties: {
        display: "inline-block",
      },
    }),
    animatedUno(),
  ],
  theme: {
    colors: {
      "custom-gradient-start": "#d53369",
      "custom-gradient-end": "#daae51",
      "success-100": "#F2F9EC",
      "success-200": "#E4F2DB",
      "success-300": "#7EC050",
      "warn-100": "#FCF6ED",
      "warn-200": "#F8ECDA",
      "warn-300": "#DCA550",
      "error-100": "#ED7456",
      "error-200": "#f3471c",
      "error-300": "#ffffff",
    },
  },
});
