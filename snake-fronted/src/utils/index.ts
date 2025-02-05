/**
 * 将标签参数转换为字符串数组
 * 参数 tags 可以是 JSON 字符串、字符串数组或未定义
 * 如果是 JSON 字符串，尝试解析为数组；如果是数组，确保每个元素都是字符串；如果不是数组或解析失败，返回空数组
 *
 * @param tags {string | string[] | undefined} - 标签参数，可以是 JSON 字符串、字符串数组或未定义
 * @returns {string[]} 转换后的字符串数组
 */
export const convertTagsToStringArray = (
  tags: string | string[] | undefined
): string[] => {
  // 检查 tags 是否为字符串类型
  if (typeof tags === "string") {
    try {
      // 尝试解析 JSON 字符串
      const parsedTags = JSON.parse(tags);
      // 检查解析后的结果是否为数组
      if (Array.isArray(parsedTags)) {
        return parsedTags.map((tag) => String(tag)); // 确保每个元素都是字符串
      } else {
        // 如果解析结果不是数组，抛出错误
        throw new Error("Parsed tags is not an array");
      }
    } catch (error) {
      // 捕获解析错误，输出错误信息并返回空数组
      console.error("Failed to parse tags JSON string:", error);
      return [];
    }
    // 检查 tags 是否为数组类型
  } else if (Array.isArray(tags)) {
    return tags.map((tag) => String(tag)); // 确保每个元素都是字符串
  } else {
    // 如果 tags 不是字符串或数组，返回空数组
    return [];
  }
};

/**
 * 格式化文件大小
 * @param size
 */
export const formatSize = (size?: number) => {
  if (!size) return "未知";
  if (size < 1024) return size + " B";
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + " KB";
  return (size / (1024 * 1024)).toFixed(2) + " MB";
};
