# Message 消息组件

一个灵活的 Vue 3 消息通知组件。

## 基础用法

```typescript
import { Message } from "./Message";

// 成功消息
Message.success("操作成功完成");

// 警告消息
Message.warning("请检查您的输入");

// 错误消息
Message.error("发生错误");
```

## 高级选项

```typescript
// 自定义显示时长（毫秒）
Message.success("自定义时长消息", {
  duration: 5000,
});

// 自定义位置
Message.warning("自定义位置消息", {
  position: "bottom",
});

// 不自动关闭的消息
Message.error("重要错误消息", {
  duration: 0,
});

// 带回调函数
Message.success("操作完成", {
  onClose: () => {
    console.log("消息已关闭");
  },
});
```

## API 参考

### 消息方法

- `Message.success(content, options?)` - 成功消息
- `Message.warning(content, options?)` - 警告消息
- `Message.error(content, options?)` - 错误消息

### 配置选项

| 参数     | 类型              | 默认值 | 说明             |
| -------- | ----------------- | ------ | ---------------- |
| duration | number            | 3000   | 显示时长（毫秒） |
| position | 'top' \| 'bottom' | 'top'  | 消息显示位置     |
| onClose  | () => void        | -      | 关闭时的回调函数 |
| closable | boolean           | false  | 是否显示关闭按钮 |
