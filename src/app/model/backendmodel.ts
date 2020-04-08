declare module Model {
  export interface ScanStartRequest {
    threadNum: number;
    baseDir: string;
    excludedPaths: string[];
    outputTypes: string[];
    fileOutputLoc: string;
    fileSizeUnit: string;
    dirSizeUnit: string;
    fileTopCount: string;
  }

  export interface ScanProgress {
    dirProcessed: number;
    fileProcessed: number;
    timeCostInSecond: number;
    candidateInQueue: number;
    done: boolean;
  }

  export interface GetScanProgressResponse {
    progress: ScanProgress;
  }

  export interface GetSummaryInfoResponse {
    timeCostInSecond: number;
    fileCount: number;
    dirCount: number;
    sizeInByte: number;
    size: string;
    baseDir: string;
    excludedPaths: string[]
  }

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
