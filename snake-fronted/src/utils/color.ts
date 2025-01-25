/**
 * 生成一个随机的RGB颜色字符串
 *
 * 此函数通过随机生成三个数字（r、g、b），分别代表红色、绿色和蓝色的值，
 * 并将这些值格式化为一个RGB颜色字符串返回这个字符串可以用来在计算机程序中
 * 表示一个随机的颜色
 *
 * @returns {string} 一个形如 'rgb(r, g, b)' 的字符串，其中 r、g、b 是 0 到 255 之间的随机整数
 */
export const randomRGB = () => {
  const r = Math.floor(Math.random() * 255);
  const g = Math.floor(Math.random() * 255);
  const b = Math.floor(Math.random() * 255);
  return `rgb(${r}, ${g}, ${b})`;
};
