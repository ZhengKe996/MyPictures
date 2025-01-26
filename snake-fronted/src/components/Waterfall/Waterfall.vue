<template>
  <div
    class="relative"
    ref="containerTarget"
    :style="{
      height: containerHeight + 'px',
    }"
  >
    <!-- 数据渲染 -->
    <template v-if="columnWidth && data.length">
      <div
        v-for="(item, index) in data"
        :key="nodeKey ? item[nodeKey] : index"
        class="m-waterfall-item absolute duration-300"
        :style="{
          width: columnWidth + 'px',
          left: item._style?.left + 'px',
          top: item._style?.top + 'px',
        }"
      >
        <slot :item="item" :width="columnWidth" :index="index" />
      </div>
    </template>
    <!-- 加载中 -->
    <div v-else>加载中...</div>
  </div>
</template>

<script lang="ts">
export default {
  name: "waterfall",
};
</script>

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from "vue";
import {
  getImgElements,
  getAllImg,
  onComplateImgs,
  getMinHeight,
  getMaxHeight,
  getMinHeightColumn,
} from "./utils";
interface Props {
  data: any[];
  // 唯一标识 key
  nodeKey: string;
  // 列数
  column?: number;
  // 列间距
  columnSpacing?: number;
  // 行间距
  rowSpacing?: number;
  // 是否需要进行图片预读取
  picturePreReading?: boolean;
}

const {
  column = 2,
  columnSpacing = 20,
  rowSpacing = 20,
  picturePreReading = true,
  data = [],
} = defineProps<Props>();

// 容器总高度
const containerHeight = ref<number>(0);

// 记录每列高度的容器 key:列 value: 列高
const columnHeightObj = ref<any>({});

/**
 * 构建记录每列高度的对象
 */
const useColumnHeightObj = () => {
  columnHeightObj.value = {};
  for (let i = 0; i < column; i++) {
    columnHeightObj.value[i] = 0;
  }
};

// 容器实例
const containerTarget = ref<Element | undefined>();

// 容器总宽度 不包含 padding margin border
const containerWidth = ref<number>(0);

// 容器左边距，计算 item 的 left
const containerLeft = ref<number>(0);

/**
 * 计算容器宽度
 */
const useContainerWidth = () => {
  const { paddingLeft, paddingRight } = getComputedStyle(
    containerTarget.value!,
    null
  );

  // 容器左边距
  containerLeft.value = parseFloat(paddingLeft);

  // 容器的宽度
  containerWidth.value =
    (containerTarget.value as HTMLElement).offsetWidth -
    parseFloat(paddingLeft) -
    parseFloat(paddingRight);
};

// 列宽
const columnWidth = ref<number>(0);

// 列间距合计
const columnSpacingTotal = computed(() => {
  return (column - 1) * columnSpacing;
});

/**
 * 计算列宽
 */
const useColumnWidth = () => {
  // 计算容器宽度
  useContainerWidth();

  // 计算列宽
  columnWidth.value =
    (containerWidth.value - columnSpacingTotal.value) / column;
};

onMounted(() => {
  // 计算列宽
  useColumnWidth();
});

// item 高度集合
let itemHeights: any[] = [];

/**
 * 监听图片加载完成 需要图片预加载
 * */
const waitImgComplate = () => {
  itemHeights = [];

  // 拿到所有的元素
  let itemElements = Array.from(
    document.getElementsByClassName(
      "m-waterfall-item"
    ) as HTMLCollectionOf<HTMLElement>
  );

  // 获取到元素的 img 标签
  const imgElements = getImgElements(itemElements);

  // 获取所有 img标签的图片
  const allImgs = getAllImg(imgElements);

  // 等待图片加载完成
  onComplateImgs(allImgs).then(() => {
    // 图片加载完成
    itemElements.forEach((el) => {
      itemHeights.push(el.offsetHeight);
    });
    // 渲染位置
    useItemLocation();
  });
};

/**
 * 不需要图片预加载
 */
const useItemHeight = () => {
  itemHeights = [];
  // 拿到所有的元素
  let itemElements = Array.from(
    document.getElementsByClassName(
      "m-waterfall-item"
    ) as HTMLCollectionOf<HTMLElement>
  );

  // 计算 item 高度
  itemElements.forEach((el) => {
    itemHeights.push(el.offsetHeight);
  });
  useItemLocation();
};

// 渲染位置
const useItemLocation = () => {
  // 遍历数据源
  data.forEach((item, index) => {
    // 避免重复运算
    if (item._style) {
      return;
    }
    // 生成 style
    item._style = {};

    // left
    item._style.left = getItemLeft();

    // top
    item._style.top = getItemTop();

    // 指定列高度自增
    increasingHeight(index);
  });

  // 指定容器的高度
  containerHeight.value = getMaxHeight(columnHeightObj.value);
};

/**
 * 在组件销毁时,清楚所有 _style
 */
onUnmounted(() => {
  data.forEach((item) => {
    delete item._style;
  });
});

/**
 * 返回下一个 item 的 left
 */
const getItemLeft = () => {
  // 拿到最小宽度的列
  const column = parseFloat(getMinHeightColumn(columnHeightObj.value)!);

  return column * (columnWidth.value + columnSpacing) + containerLeft.value;
};

/**
 * 返回下一个 item 的 top
 */
const getItemTop = () => {
  return getMinHeight(columnHeightObj.value);
};

/**
 * 指定列高度自增
 */
const increasingHeight = (index: number) => {
  // 最小高度所在列
  const minHeightColumn = parseFloat(
    getMinHeightColumn(columnHeightObj.value)!
  );
  // 使该列自增
  columnHeightObj.value[minHeightColumn] += itemHeights[index] + rowSpacing;
};

/**
 * 触发计算
 */
watch(
  () => data,
  (newValue) => {
    nextTick(() => {
      // 第一次获取数据时，构建高度记录容器
      const resetColumnHeight = newValue.every((item) => !item._style);
      if (resetColumnHeight) {
        // 构建高度记录容器
        useColumnHeightObj();
      }
      if (picturePreReading) {
        waitImgComplate();
      } else {
        useItemHeight();
      }
    });
  },
  {
    deep: true,
    immediate: true,
  }
);

/**
 * 重新构建瀑布流
 */
const reset = () => {
  setTimeout(() => {
    // 重新计算列宽
    useColumnWidth();
    // 重置所有的定位数据
    data.forEach((item) => {
      item._style = null;
    });
  }, 200);
};

/**
 * 监听列数变化
 */
watch(
  () => column,
  () => {
    if (picturePreReading) {
      columnWidth.value = 0;
      // 数据改变后 视图变化后的回调
      reset();
    } else {
      reset();
    }
  }
);
</script>
