<template>
  <div
    class="relative"
    ref="containerTarget"
    :style="{
      height: containerHeight + 'px',
    }"
  >
    <template v-if="columnWidth && data.length">
      <div
        v-for="(item, index) in data"
        :key="nodeKey ? item[nodeKey] : index"
        class="m-waterfall-item absolute transition-all duration-300 ease-in-out hover:shadow-lg"
        :class="{ 'opacity-0': !item._style }"
        :style="{
          width: columnWidth + 'px',
          left: item._style?.left + 'px',
          top: item._style?.top + 'px',
          transform: item._style ? 'translateY(0)' : 'translateY(20px)',
          opacity: item._style ? 1 : 0,
        }"
      >
        <slot :item="item" :width="columnWidth" :index="index" />
      </div>
    </template>
    <div v-else class="flex items-center justify-center p-4">
      <div class="animate-pulse flex space-x-4">
        <div class="rounded-full bg-slate-200 h-10 w-10"></div>
        <div class="flex-1 space-y-6 py-1">
          <div class="h-2 bg-slate-200 rounded"></div>
          <div class="space-y-3">
            <div class="grid grid-cols-3 gap-4">
              <div class="h-2 bg-slate-200 rounded col-span-2"></div>
              <div class="h-2 bg-slate-200 rounded col-span-1"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: "Waterfall",
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

// 优化类型定义
interface WaterfallItem {
  _style?: {
    left: number;
    top: number;
  };
  [key: string]: any;
}

interface Props {
  data: WaterfallItem[];
  nodeKey: string;
  column?: number;
  columnSpacing?: number;
  rowSpacing?: number;
  picturePreReading?: boolean;
  animation?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  column: 2,
  columnSpacing: 20,
  rowSpacing: 20,
  picturePreReading: true,
  animation: true,
});

// 容器总高度
const containerHeight = ref<number>(0);

// 记录每列高度的容器 key:列 value: 列高
const columnHeightObj = ref<any>({});

/**
 * 构建记录每列高度的对象
 */
const useColumnHeightObj = () => {
  columnHeightObj.value = {};
  for (let i = 0; i < props.column; i++) {
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
  return (props.column - 1) * props.columnSpacing;
});

/**
 * 计算列宽
 */
const useColumnWidth = () => {
  // 计算容器宽度
  useContainerWidth();

  // 计算列宽
  columnWidth.value =
    (containerWidth.value - columnSpacingTotal.value) / props.column;
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
  props.data.forEach((item, index) => {
    // 避免重复运算
    if (item._style) {
      return;
    }
    // 生成 style
    item._style = { left: 0, top: 0 };

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
  props.data.forEach((item) => {
    delete item._style;
  });
});

/**
 * 返回下一个 item 的 left
 */
const getItemLeft = () => {
  // 拿到最小宽度的列
  const column = parseFloat(getMinHeightColumn(columnHeightObj.value)!);

  return (
    column * (columnWidth.value + props.columnSpacing) + containerLeft.value
  );
};

/**
 * 返回下一个 item 的 top
 */
const getItemTop = () => {
  return getMinHeight(columnHeightObj.value) ?? 0;
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
  columnHeightObj.value[minHeightColumn] +=
    itemHeights[index] + props.rowSpacing;
};

/**
 * 触发计算
 */
watch(
  () => props.data,
  (newValue) => {
    nextTick(() => {
      // 第一次获取数据时，构建高度记录容器
      const resetColumnHeight = newValue.every((item) => !item._style);
      if (resetColumnHeight) {
        // 构建高度记录容器
        useColumnHeightObj();
      }
      if (props.picturePreReading) {
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
  return new Promise<void>((resolve) => {
    useColumnWidth();
    props.data.forEach((item) => {
      item._style = undefined;
    });
    nextTick(() => {
      setTimeout(() => {
        resolve();
      }, 300); // 匹配过渡动画时长
    });
  });
};

// 暴露方法给父组件
defineExpose({
  reset,
});

/**
 * 监听列数变化
 */
watch(
  () => props.column,
  () => {
    if (props.picturePreReading) {
      columnWidth.value = 0;
      // 数据改变后 视图变化后的回调
      reset();
    } else {
      reset();
    }
  }
);
</script>

<style scoped>
.m-waterfall-item {
  backface-visibility: hidden;
  will-change: transform, opacity;
  transform-origin: center center;
}

.m-waterfall-item:hover {
  transform: translateY(-5px) !important;
}

@media (prefers-reduced-motion: reduce) {
  .m-waterfall-item {
    transition-duration: 0.1s;
  }
}

.waterfall-container {
  will-change: transform;
  backface-visibility: hidden;
}
</style>
