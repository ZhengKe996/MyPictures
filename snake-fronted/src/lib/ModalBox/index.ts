import { h, render } from "vue";
import ModalBox from "./ModalBox.vue";

interface ModalOptions {
  onClose?: () => void;
}

interface ShowImageModalOptions extends ModalOptions {
  imageUrl: string;
  altText?: string;
}

export function showImageModal({
  imageUrl,
  altText,
  onClose,
}: ShowImageModalOptions) {
  const container = document.createElement("div");

  let isVisible = true;

  const close = () => {
    isVisible = false;
    render(null, container);
    onClose?.();
  };

  const vnode = h(
    ModalBox,
    {
      modelValue: isVisible,
      "onUpdate:modelValue": (value: boolean) => {
        if (!value) close();
      },
      closeOnClickOverlay: true,
    },
    {
      default: () =>
        h("img", {
          src: imageUrl,
          alt: altText || "Modal Image",
          style: {
            maxWidth: "100%",
            maxHeight: "100%",
            objectFit: "contain",
          },
        }),
    }
  );

  render(vnode, container);
}

export default ModalBox;
