import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface SummaryInfo {
  timeCostInSecond: number;
  fileCount: number;
  dirCount: number;
  size: string;
  baseDir: string;
  excludedPaths: string[];
}

export interface FileItem {
  absolutePath: string;
  size: string;
}

export interface DirectoryTreeNode<T> {
  data: T;
  children: DirectoryTreeNode<T>[];
  expanded?: boolean;
}

export interface DirectoryTreeEntry {
  name: string;
  absolutePath: string;
  size: string;
  isDir: boolean;
  items?: number;
}

@Injectable({
  providedIn: 'root',
})
export class ScanResultService {
  private static readonly GetSummaryInfoUrl =
    'http://localhost:8080/scanner/summary-info?uuid=';
  private static readonly GetDirectoryInfoUrl =
    'http://localhost:8080/scanner/directory-info?uuid=';
  private static readonly GetFileInfoUrl =
    'http://localhost:8080/scanner/file-info?uuid=';

  constructor(private client: HttpClient) {}

  public async getSummaryInfo(uuid: string): Promise<SummaryInfo> {
    return this.client
      .get<Model.GetSummaryInfoResponse>(
        ScanResultService.GetSummaryInfoUrl + uuid
      )
      .toPromise()
      .then(
        (resp) =>
          <SummaryInfo>{
            timeCostInSecond: resp.info.timeCostInSecond,
            fileCount: resp.info.fileCount,
            dirCount: resp.info.dirCount,
            size: resp.info.size,
            baseDir: resp.info.baseDir,
            excludedPaths: resp.info.excludedPaths,
          }
      )
      .catch((e) => {
        return null;
      });
  }

  public async getFileInfo(uuid: string): Promise<FileItem[]> {
    return this.client
      .get<Model.GetFileInfoResponse>(ScanResultService.GetFileInfoUrl + uuid)
      .toPromise()
      .then((resp) =>
        resp.info.files.map(
          (model) =>
            <FileItem>{
              absolutePath: model.absolutePath,
              size: model.size,
            }
        )
      )
      .catch((e) => {
        return new Array<FileItem>();
      });
  }

  public async getDirectoryInfo(
    uuid: string
  ): Promise<DirectoryTreeNode<DirectoryTreeEntry>> {
    return this.client
      .get<Model.GetDirectoryInfoResponse>(
        ScanResultService.GetDirectoryInfoUrl + uuid
      )
      .toPromise()
      .then((resp) => {
        const entityRoot: DirectoryTreeNode<DirectoryTreeEntry> = {
          data: {
            name: resp.info.root.absolutePath,
            absolutePath: '',
            size: resp.info.root.size,
            isDir: resp.info.root.directory,
            items: resp.info.root.children.length,
          },
          children: [],
          expanded: true,
        };
        this.convertToDirectoryTree(entityRoot, resp.info.root);
        return entityRoot;
      })
      .catch((e) => {
        return {
          data: null,
          children: [],
          expanded: false,
        };
      });
  }

  private convertToDirectoryTree(
    entity: DirectoryTreeNode<DirectoryTreeEntry>,
    model: Model.DirectoryTreeNode
  ): void {
    if (model.children.length === 0) {
      return;
    }
    model.children.forEach((modelChild) => {
      const entityChild: DirectoryTreeNode<DirectoryTreeEntry> = {
        data: {
          name: this.getName(modelChild.absolutePath),
          absolutePath: modelChild.absolutePath,
          size: modelChild.size,
          isDir: modelChild.directory,
          items: modelChild.children.length,
        },
        children: [],
        expanded: false,
      };
      entity.children.push(entityChild);
    });
    entity.children.forEach((entityChild, index) => {
      this.convertToDirectoryTree(entityChild, model.children[index]);
    });
  }

  private getName(path: string): string {
    const index = path.lastIndexOf('\\');
    return index < 0 ? path : path.substring(index + 1);
  }
}
