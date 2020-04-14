import { Component, OnInit, Input } from '@angular/core';
import {
  NbTreeGridDataSource,
  NbTreeGridDataSourceBuilder,
} from '@nebular/theme';
import { getFileSize, getFileName, formatSize } from '../utilies';

interface DirectoryTreeNode<T> {
  data: T;
  children: DirectoryTreeNode<T>[];
  expanded?: boolean;
}

interface DirectoryTreeEntry {
  name: string;
  absolutePath: string;
  sizeInByte: number;
  size: string;
  isDir: boolean;
  extension: string;
  items?: number;
}

interface ExtensionListItem {
  extension: string;
  size: string;
  sizeInByte: number;
  percentage: string;
  count: number;
  included: boolean;
}

@Component({
  selector: 'app-directory-info',
  templateUrl: './directory-info.component.html',
  styleUrls: ['./directory-info.component.scss'],
})
export class DirectoryInfoComponent implements OnInit {
  public dirTreeCustomColumn = 'name';
  public dirTreeDefaultColumns = ['size', 'extension', 'items'];
  public dirTreeAllColumns = [
    this.dirTreeCustomColumn,
    ...this.dirTreeDefaultColumns,
  ];

  public dirTreeDataSource: NbTreeGridDataSource<
    DirectoryTreeNode<DirectoryTreeEntry>
  >;

  public extensionListItems: ExtensionListItem[];

  private _directoryInfo: Model.DirectoryInfo;
  private _fileteredExtensions: Set<string>;

  private _expandedPaths: Set<string>;

  @Input()
  public set directoryInfo(info: Model.DirectoryInfo) {
    this._directoryInfo = info;
    this._fileteredExtensions = new Set<string>();
    this._expandedPaths = new Set<string>();

    if (!info) {
      this.dirTreeDataSource = null;
      return;
    }
    const dirRoot = this.buildDirectoryTree();
    this.dirTreeDataSource = this._dataSourceBuilder.create([dirRoot]);

    this.extensionListItems = info.extensionItems.map(
      (item) =>
        <ExtensionListItem>{
          extension: item.extension,
          sizeInByte: item.sizeInByte,
          count: item.count,
          size: getFileSize(item.sizeInByte),
          percentage:
            formatSize(item.sizeInByte / dirRoot.data.sizeInByte) + '%',
          included: true,
        }
    );
  }

  constructor(
    private _dataSourceBuilder: NbTreeGridDataSourceBuilder<
      DirectoryTreeNode<DirectoryTreeEntry>
    >
  ) {}

  ngOnInit() {}

  public getDirTreeShowOn(index: number) {
    const minWithForMultipleColumns = 400;
    const nextColumnStep = 100;
    return minWithForMultipleColumns + nextColumnStep * index;
  }

  public toggleExtensionItem(item: ExtensionListItem): void {
    if (item.included) {
      this._fileteredExtensions.delete(item.extension);
    } else {
      this._fileteredExtensions.add(item.extension);
    }
    const dirRoot = this.refreshDirectoryTree();
    this.dirTreeDataSource = this._dataSourceBuilder.create([dirRoot]);
  }

  public toggleDirectoryTreeRow(
    node: DirectoryTreeNode<DirectoryTreeEntry>
  ): void {
    // event happens before node.expanded change, so when node.expanded is false, it should be expanded
    if (node.data.isDir) {
      if (!node.expanded) {
        this._expandedPaths.add(node.data.absolutePath);
      } else {
        this._expandedPaths.delete(node.data.absolutePath);
      }
    }
  }

  private buildDirectoryTree(): DirectoryTreeNode<DirectoryTreeEntry> {
    const dirRoot: DirectoryTreeNode<DirectoryTreeEntry> = {
      data: {
        name: this._directoryInfo.root.absolutePath,
        absolutePath: '',
        sizeInByte: this._directoryInfo.root.sizeInByte,
        size: getFileSize(this._directoryInfo.root.sizeInByte),
        extension: this._directoryInfo.root.extension,
        isDir: this._directoryInfo.root.directory,
        items: 0 // set later
      },
      children: [],
      expanded: this._expandedPaths.has(this._directoryInfo.root.absolutePath),
    };
    const itemCount = this.convertToDirectoryTree(dirRoot, this._directoryInfo.root);
    dirRoot.data.items = itemCount;
    return dirRoot;
  }

  private convertToDirectoryTree(
    entity: DirectoryTreeNode<DirectoryTreeEntry>,
    model: Model.DirectoryTreeNode
  ): number {
    if (model.children.length === 0) {
      return 0;
    }
    let itemCount = model.children.length;
    model.children.forEach((modelChild) => {
      const entityChild: DirectoryTreeNode<DirectoryTreeEntry> = {
        data: {
          name: getFileName(modelChild.absolutePath),
          absolutePath: modelChild.absolutePath,
          sizeInByte: modelChild.sizeInByte,
          size: getFileSize(modelChild.sizeInByte),
          extension: modelChild.extension,
          isDir: modelChild.directory,
          items: 0 // set later
        },
        children: [],
        expanded: this._expandedPaths.has(modelChild.absolutePath),
      };
      entity.children.push(entityChild);
    });
    entity.children.forEach((entityChild, index) => {
      itemCount += this.convertToDirectoryTree(entityChild, model.children[index]);
    });
    entity.data.items = itemCount;
    return itemCount;
  }

  private refreshDirectoryTree(): DirectoryTreeNode<DirectoryTreeEntry> {
    const copyRoot = this.buildDirectoryTree();
    this.filterDirectoryTree(copyRoot);
    return copyRoot;
  }

  private filterDirectoryTree(
    node: DirectoryTreeNode<DirectoryTreeEntry>
  ): [number, number] {
    if (!node.data.isDir) {
      return [0, 0];
    }
    let filteredSize = 0;
    let filteredItem = 0;
    for (let i = node.children.length - 1; i >= 0; i--) {
      const child = node.children[i];
      if (child.data.isDir) {
        const [size, count] = this.filterDirectoryTree(child);
        filteredSize += size;
        filteredItem += count;
      } else if (this._fileteredExtensions.has(child.data.extension)) {
        filteredSize += child.data.sizeInByte;
        filteredItem++;
        node.children.splice(i, 1);
      }
    }
    node.data.sizeInByte -= filteredSize;
    node.data.size = getFileSize(node.data.sizeInByte);
    node.data.items -= filteredItem;
    node.expanded = this._expandedPaths.has(node.data.absolutePath);
    return [filteredSize, filteredItem];
  }
}
