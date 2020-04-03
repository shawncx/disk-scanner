declare module Model {
  export interface GetDirectoryInfoResponse {
    root: DirectoryTreeNode;
  }

  export interface DirectoryTreeNode {
    absolutePath: string;
    sizeInByte: number;
    size: string;
    directory: boolean;
    children: DirectoryTreeNode[];
  }

  export interface GetFileInfoResponse {
    files: FileItem[];
  }

  export interface FileItem {
    absolutePath: string;
    sizeInByte: number;
    size: string;
  }
}
