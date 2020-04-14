export function getFileName(path: string): string {
  const index = path.lastIndexOf('\\');
  return index < 0 ? path : path.substring(index + 1);
}

export function getFileSize(sizeInByte: number): string {
  if (sizeInByte < 1024) {
    return formatSize(sizeInByte) + 'B';
  }
  sizeInByte /= 1024;
  if (sizeInByte < 1024) {
    return formatSize(sizeInByte) + 'KB';
  }
  sizeInByte /= 1024;
  if (sizeInByte < 1024) {
    return formatSize(sizeInByte) + 'MB';
  }
  sizeInByte /= 1024;
  return formatSize(sizeInByte) + 'GB';
}

export function formatSize(size: number): string {
  return size.toFixed(2);
}
