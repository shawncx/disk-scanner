declare module Model {
  export interface StartScanRequest {
    threadNum: number;
    baseDir: string;
    excludedPaths: string[];
    outputTypes: string[];
    fileOutputLoc: string;
    fileSizeUnit: string;
    dirSizeUnit: string;
    fileTopCount: number;
  }

  export interface StartScanResponse {
    uuid: string;
    message: null;
    succeeded: boolean;
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
    info: SummaryInfo;
  }

  export interface SummaryInfo {
    timeCostInSecond: number;
    fileCount: number;
    dirCount: number;
    sizeInByte: number;
    size: string;
    baseDir: string;
    excludedPaths: string[];
  }

  export interface GetDirectoryInfoResponse {
    info: DirectoryInfo;
  }

  export interface DirectoryInfo {
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
    info: FileInfo;
  }

  export interface FileInfo {
    files: FileItem[];
  }

  export interface FileItem {
    absolutePath: string;
    sizeInByte: number;
    size: string;
  }
}
