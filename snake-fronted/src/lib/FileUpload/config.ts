export const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB in bytes

export interface FileValidationConfig {
  maxSize: number;
  acceptedTypes: string[];
}

export const DEFAULT_CONFIG: FileValidationConfig = {
  maxSize: MAX_FILE_SIZE,
  acceptedTypes: ["image/jpeg", "image/png", "image/gif", "image/svg+xml"],
};

export const ERROR_MESSAGES = {
  SIZE_EXCEEDED: "File size exceeds maximum limit",
  INVALID_TYPE: "Invalid file type",
};
