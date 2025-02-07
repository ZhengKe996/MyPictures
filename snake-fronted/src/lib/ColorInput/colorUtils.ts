export type ColorFormat = "hex" | "rgb" | "0x" | "rgba";

export interface ColorUtilsOptions {
  /** 输出的颜色格式 */
  outputFormat: ColorFormat;
  /** 是否包含 alpha 通道 */
  includeAlpha?: boolean;
}

// Improve hexToRgb function with better validation
export function hexToRgb(hex: string): [number, number, number] {
  try {
    // Remove 0x or # prefix and convert to lowercase
    hex = hex.toLowerCase().replace(/^(0x|#)/, "");

    // Handle shorthand hex format (e.g., #fff)
    if (hex.length === 3) {
      hex = hex
        .split("")
        .map((char) => char + char)
        .join("");
    }

    // Ensure valid hex length
    if (hex.length !== 6) {
      throw new Error("Invalid hex color length");
    }

    // Validate hex characters
    if (!/^[0-9a-f]{6}$/.test(hex)) {
      throw new Error("Invalid hex color characters");
    }

    const r = parseInt(hex.slice(0, 2), 16);
    const g = parseInt(hex.slice(2, 4), 16);
    const b = parseInt(hex.slice(4, 6), 16);

    // Validate RGB values are within bounds
    if ([r, g, b].some((v) => isNaN(v) || v < 0 || v > 255)) {
      throw new Error("Invalid RGB values");
    }

    return [r, g, b];
  } catch (e) {
    console.warn("Error in hexToRgb:", e);
    throw e;
  }
}

// 将RGB转换为十六进制
export function rgbToHex(r: number, g: number, b: number): string {
  return "#" + [r, g, b].map((x) => x.toString(16).padStart(2, "0")).join("");
}

// 修改 isValidColor 函数，使其更宽容
export function isValidColor(color: string): boolean {
  if (!color || typeof color !== "string") return false;

  try {
    const normalized = formatColor(color, { outputFormat: "hex" });
    return normalized !== "#000000" || color.toLowerCase().endsWith("000000");
  } catch (e) {
    return false;
  }
}

// 添加颜色格式规范化函数
export function normalizeColor(color: string): string {
  console.log("normalizeColor", color);
  const trimmed = color.trim().toLowerCase();

  // 处理 rgb 格式
  if (trimmed.startsWith("rgb")) {
    const match = trimmed.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
    if (match) {
      const [r, g, b] = match.slice(1).map(Number);
      return rgbToHex(r, g, b);
    }
  }

  // 处理 0x 格式
  if (trimmed.startsWith("0x")) {
    return "#" + trimmed.slice(2);
  }

  // 处理 hex 格式或纯数字格式
  if (trimmed.startsWith("#") || /^[0-9a-f]{6}$/.test(trimmed)) {
    return trimmed.startsWith("#") ? trimmed : "#" + trimmed;
  }

  throw new Error("Unsupported color format");
}

// 更新 formatColor 函数
export function formatColor(color: string, options: ColorUtilsOptions): string {
  try {
    let rgb: [number, number, number];

    // 处理空值情况
    if (!color) {
      return options.outputFormat === "0x" ? "0x000000" : "#000000";
    }

    // 标准化输入颜色
    color = color.trim().toLowerCase();

    // 处理各种输入格式
    if (color.startsWith("0x")) {
      // 处理 0x 格式
      const hex = color.slice(2);
      if (!/^[0-9a-f]{6}$/i.test(hex)) {
        const fixedHex = hex.replace(/[^0-9a-f]/gi, "0").padEnd(6, "0");
        rgb = [
          parseInt(fixedHex.slice(0, 2), 16),
          parseInt(fixedHex.slice(2, 4), 16),
          parseInt(fixedHex.slice(4, 6), 16),
        ];
      } else {
        rgb = [
          parseInt(hex.slice(0, 2), 16),
          parseInt(hex.slice(2, 4), 16),
          parseInt(hex.slice(4, 6), 16),
        ];
      }
    } else if (color.startsWith("#")) {
      rgb = hexToRgb(color);
    } else if (color.startsWith("rgb")) {
      const match = color.match(
        /^rgb\s*\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*\)$/
      );
      if (!match) {
        throw new Error("Invalid rgb format");
      }
      rgb = match.slice(1).map((n) => parseInt(n)) as [number, number, number];
    } else {
      // 尝试作为纯十六进制处理
      rgb = hexToRgb("#" + color);
    }

    // 规范化 RGB 值
    rgb = rgb.map((v) => Math.min(255, Math.max(0, Math.round(v)))) as [
      number,
      number,
      number
    ];

    // 转换为目标格式
    switch (options.outputFormat) {
      case "hex":
        return rgbToHex(...rgb);
      case "0x":
        return "0x" + rgbToHex(...rgb).slice(1);
      case "rgb":
        return `rgb(${rgb.join(", ")})`;
      case "rgba":
        return `rgba(${rgb.join(", ")}, 1)`;
      default:
        return rgbToHex(...rgb);
    }
  } catch (e) {
    console.warn("Color format error:", e, "| Input color:", color);
    return options.outputFormat === "0x" ? "0x000000" : "#000000";
  }
}
