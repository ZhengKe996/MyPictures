export type ColorFormat = "hex" | "rgb" | "0x" | "rgba";

export interface ColorUtilsOptions {
  /** 输出的颜色格式 */
  outputFormat: ColorFormat;
  /** 是否包含 alpha 通道 */
  includeAlpha?: boolean;
}

// 将十六进制颜色转换为RGB数组
export function hexToRgb(hex: string): [number, number, number] {
  // 处理 0x 格式
  if (hex.startsWith("0x")) {
    hex = "#" + hex.slice(2);
  }
  // 移除 # 号
  hex = hex.replace("#", "");

  const r = parseInt(hex.slice(0, 2), 16);
  const g = parseInt(hex.slice(2, 4), 16);
  const b = parseInt(hex.slice(4, 6), 16);

  return [r, g, b];
}

// 将RGB转换为十六进制
export function rgbToHex(r: number, g: number, b: number): string {
  return "#" + [r, g, b].map((x) => x.toString(16).padStart(2, "0")).join("");
}

// 验证颜色格式
export function isValidColor(color: string): boolean {
  // 支持 #RRGGBB 格式
  if (/^#[0-9A-Fa-f]{6}$/.test(color)) return true;
  // 支持 0xRRGGBB 格式
  if (/^0x[0-9A-Fa-f]{6}$/.test(color)) return true;
  // 支持 rgb(r,g,b) 格式
  if (/^rgb\(\s*\d+\s*,\s*\d+\s*,\s*\d+\s*\)$/.test(color)) return true;

  return false;
}

// 统一颜色格式
export function formatColor(color: string, options: ColorUtilsOptions): string {
  let rgb: [number, number, number];

  // 解析输入颜色
  if (color.startsWith("#")) {
    rgb = hexToRgb(color);
  } else if (color.startsWith("0x")) {
    rgb = hexToRgb(color);
  } else if (color.startsWith("rgb")) {
    const match = color.match(/\d+/g);
    if (match) {
      rgb = [parseInt(match[0]), parseInt(match[1]), parseInt(match[2])];
    } else {
      throw new Error("Invalid color format");
    }
  } else {
    throw new Error("Unsupported color format");
  }

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
}
