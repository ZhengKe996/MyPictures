/**
 * 将0x开头的颜色值转换为#开头的标准十六进制颜色值
 * @param color 0x开头的颜色值字符串
 * @returns 标准的#开头的十六进制颜色值
 */
export const convertOxToHex = (color: string | undefined): string => {
  if (!color) return "#000000";
  if (color.startsWith("0x")) {
    return "#" + color.slice(2);
  }
  if (color.startsWith("#")) {
    return color;
  }
  return "#000000";
};
