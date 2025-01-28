import Dialog from "./Dialog.vue";

export interface DialogProps {
  modelValue: boolean;
  title?: string;
  cancelText?: string;
  confirmText?: string;
  cancelHandler?: () => void;
  confirmHandler?: () => void;
  close?: () => void;
  showClose?: boolean;
  cancelButtonColor?: string;
  confirmButtonColor?: string;
}

export default Dialog;
