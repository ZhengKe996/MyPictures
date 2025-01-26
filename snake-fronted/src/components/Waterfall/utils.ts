/**
 * 获取图像元素数组
 * 该函数接受一个HTMLElement数组，返回一个HTMLImageElement数组，包含所有子图像元素
 * 如果输入数组为空或未提供，则返回一个空数组
 *
 * @param itemElements HTMLElement数组，代表可能包含图像的元素集合
 * @returns HTMLImageElement数组，包含所有找到的图像元素
 */
export const getImgElements = (
  itemElements: HTMLElement[]
): HTMLImageElement[] => {
  // 检查输入数组是否为空或未提供，如果是，则返回一个空数组
  if (!itemElements || itemElements.length === 0) return [];

  // 初始化一个空数组，用于存储所有图像元素
  const imgElements: HTMLImageElement[] = [];

  // 遍历输入的元素数组
  for (const el of itemElements) {
    // 检查当前元素是否为HTMLElement，如果不是，则输出警告并跳过当前元素
    if (!(el instanceof HTMLElement)) {
      console.warn("Non-HTMLElement found in itemElements, skipping...");
      continue;
    }

    // 将当前元素中的所有图像元素添加到图像元素数组中
    imgElements.push(...Array.from(el.querySelectorAll("img")));
  }

  // 返回包含所有图像元素的数组
  return imgElements;
};

/**
 * 获取所有图片元素的 src 属性数组
 *
 * 此函数接受一个 HTMLImageElement 类型的数组作为输入，返回一个包含这些元素 src 属性的字符串数组
 * 主要用途是快速收集页面上一组 img 标签的图片地址
 *
 * @param itemElements HTMLImageElement 类型的数组，代表一组 img 标签
 * @returns 返回一个字符串数组，包含每个 img 标签的 src 属性
 * @throws 如果输入不是数组，则抛出错误
 */
export const getAllImg = (itemElements: HTMLImageElement[]): string[] => {
  // 检查输入是否为数组，避免意外传入非数组类型
  if (!Array.isArray(itemElements)) {
    throw new Error("Input must be an array of HTMLImageElement");
  }

  // 如果数组为空，直接返回空数组
  if (itemElements.length === 0) return [];

  // 获取所有图片元素的 src 属性
  return itemElements.map((imgElement) => imgElement.src);
};

/**
 * 等待一组图像加载完成
 *
 * 此函数接收一个图像URL数组，并返回一个Promise，该Promise在所有图像加载成功时解析，
 * 解析值为一个对象数组，每个对象包含加载成功的图像URL和其在原数组中的索引如果某个图像加载失败，
 * Promise将被拒绝，并提供加载失败的图像URL和错误信息这使得调用者可以处理图像加载过程中的错误
 *
 * @param imgs 图像URL数组，代表需要加载的图像
 * @returns 返回一个Promise，该Promise解析为一个对象数组，每个对象包含图像URL和索引
 */
export const onComplateImgs = (
  imgs: string[]
): Promise<{ img: string; index: number }[]> => {
  // 如果图像数组为空，直接返回一个解析为空数组的Promise
  if (imgs.length === 0) return Promise.resolve([]);

  // 使用map创建一个Promise数组，用于加载每个图像
  const promiseAll = imgs.map((img, index) => {
    // 返回一个新的Promise，用于处理单个图像的加载
    return new Promise<{ img: string; index: number }>((resolve, reject) => {
      // 创建一个新的Image对象
      const imageObj = new Image();
      // 设置Image对象的src属性为当前图像URL
      imageObj.src = img;
      // 当图像加载成功时，解析Promise
      imageObj.onload = () => {
        resolve({ img, index });
      };
      // 当图像加载失败或中止时，拒绝Promise
      imageObj.onerror = imageObj.onabort = () => {
        reject(new Error(`Failed to load image: ${img}`));
      };
    });
  });

  // 使用Promise.all等待所有图像加载完成，如果加载过程中有图像失败，则捕获错误并重新抛出
  return Promise.all(promiseAll).catch((error) => {
    // 打印错误信息到控制台
    console.error("Error loading images:", error);
    // 抛出错误，让调用者处理
    throw error;
  });
};

/**
 * 获取对象中最小的数值
 * 该函数旨在找出给定对象中所有数值属性的最小值，用于场景如布局调整等
 * @param columnHeightObj 一个包含数值的非空对象，键值对中的值应为数字类型
 * @returns 返回对象中最小的数值，如果对象为空或不包含数字类型，则返回null
 * @throws 如果输入不是非空对象，则抛出错误
 */
export const getMinHeight = (
  columnHeightObj: Record<string, number>
): number | null => {
  // 参数验证
  if (typeof columnHeightObj !== "object" || columnHeightObj === null) {
    throw new Error("Input must be a non-null object");
  }

  // 获取对象值并过滤非数字类型
  const columnHeightArr: number[] = Object.values(columnHeightObj).filter(
    (value): value is number => typeof value === "number"
  );

  // 处理空对象或过滤后为空的情况
  if (columnHeightArr.length === 0) {
    return null;
  }

  // 计算最小值
  return Math.min(...columnHeightArr);
};

/**
 * 获取最大高度
 *
 * 该函数旨在找出给定对象中所有有效高度值的最大值它首先提取对象中的所有高度值，
 * 过滤掉无效的数值，然后找出剩余值中的最大值如果没有任何有效的高度值，则返回0
 *
 * @param columnHeightObj 一个键值对对象，其中包含不同列的高度值
 * @returns 返回最大的高度值如果没有任何有效的高度值，则返回0
 */
export const getMaxHeight = (
  columnHeightObj: Record<string, number>
): number => {
  // 将对象中的高度值提取到一个数组中，并过滤掉非有限数值
  const columnHeightArr: number[] = Object.values(columnHeightObj).filter(
    Number.isFinite
  );

  // 如果数组为空，则返回0
  if (columnHeightArr.length === 0) return 0;

  // 返回数组中的最大值
  return Math.max(...columnHeightArr);
};

export interface ColumnHeightObj {
  [key: string]: number;
}

/**
 * 获取具有最小高度的列的键
 *
 * 此函数通过遍历一个包含列高度的对象，找到高度最小的列，并返回其键
 * 如果输入对象为空或无效，函数将返回undefined
 * 如果高度值不是数字，函数将抛出错误
 *
 * @param columnHeightObj 一个包含列键和对应高度的对象
 * @returns 返回具有最小高度的列的键，如果没有有效输入则返回undefined
 */
export const getMinHeightColumn = (
  columnHeightObj: ColumnHeightObj
): string | undefined => {
  // 验证输入是否为空对象或无效输入
  if (Object.keys(columnHeightObj).length === 0) {
    return undefined;
  }

  // 获取最小高度
  let minHeight = Infinity;
  for (const key in columnHeightObj) {
    if (columnHeightObj.hasOwnProperty(key)) {
      const height = columnHeightObj[key];
      // 验证高度值是否为数字
      if (typeof height !== "number") {
        throw new Error("Invalid height value, expected a number");
      }
      // 更新最小高度
      if (height < minHeight) {
        minHeight = height;
      }
    }
  }

  // 找到最小高度对应的键
  for (const key in columnHeightObj) {
    if (columnHeightObj.hasOwnProperty(key)) {
      // 返回具有最小高度的键
      if (columnHeightObj[key] === minHeight) {
        return key;
      }
    }
  }

  // 理论上不会到达这里，但为了类型系统完整
  return undefined;
};
