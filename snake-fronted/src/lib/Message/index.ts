import { h, render } from "vue";
import MessageComponent from "./Message.vue";

interface MessageOptions {
  duration?: number;
  position?: "top" | "bottom";
  closable?: boolean;
  onClose?: () => void;
}

let seed = 1;

const createMessage = (
  type: "success" | "warning" | "error",
  content: string,
  options: MessageOptions = {}
) => {
  const id = `message_${seed++}`;

  const destroy = () => {
    render(null, document.createElement("div"));
  };

  const vnode = h(MessageComponent, {
    id,
    type,
    content,
    destroy,
    ...options,
  });

  render(vnode, document.createElement("div"));
};

export const Message = {
  success(content: string, options?: MessageOptions) {
    return createMessage("success", content, options);
  },
  warning(content: string, options?: MessageOptions) {
    return createMessage("warning", content, options);
  },
  error(content: string, options?: MessageOptions) {
    return createMessage("error", content, options);
  },
};
