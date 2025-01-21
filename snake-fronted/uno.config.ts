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
    },
  },
});
