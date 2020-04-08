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
  constructor(private client: HttpClient) {}

  public async getSummary(): Promise<SummaryInfo> {
    return this.client
      .get<Model.GetSummaryInfoResponse>('./assets/summary-info-sample.json')
      .toPromise()
      .then(
        (resp) =>
          <SummaryInfo>{
            timeCostInSecond: resp.timeCostInSecond,
            fileCount: resp.fileCount,
            dirCount: resp.dirCount,
            size: resp.size,
            baseDir: resp.baseDir,
            excludedPaths: resp.excludedPaths,
          }
      )
      .catch((e) => {
        return null;
      });
  }

  public async getFiles(): Promise<FileItem[]> {
    return this.client
      .get<Model.GetFileInfoResponse>('./assets/file-info-sample.json')
      .toPromise()
      .then((resp) =>
        resp.files.map(
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

  public async getDirectoryTree(): Promise<
    DirectoryTreeNode<DirectoryTreeEntry>
  > {
    return this.client
      .get<Model.GetDirectoryInfoResponse>('./assets/dir-info-sample.json')
      .toPromise()
      .then((resp) => {
        const entityRoot: DirectoryTreeNode<DirectoryTreeEntry> = {
          data: {
            name: resp.root.absolutePath,
            absolutePath: '',
            size: resp.root.size,
            isDir: resp.root.directory,
            items: resp.root.children.length,
          },
          children: [],
          expanded: true,
        };
        this.convertToDirectoryTree(entityRoot, resp.root);
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
