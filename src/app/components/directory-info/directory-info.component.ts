import { Component, OnInit, Input } from '@angular/core';
import { NbTreeGridDataSource } from '@nebular/theme';
import {
  DirectoryTreeNode,
  DirectoryTreeEntry,
} from 'src/app/services/scan-result.service';

@Component({
  selector: 'app-directory-info',
  templateUrl: './directory-info.component.html',
  styleUrls: ['./directory-info.component.scss'],
})
export class DirectoryInfoComponent implements OnInit {
  @Input()
  public dirTree: NbTreeGridDataSource<DirectoryTreeNode<DirectoryTreeEntry>>;

  public dirTreeCustomColumn = 'name';
  public dirTreeDefaultColumns = ['size', 'items'];
  public dirTreeAllColumns = [
    this.dirTreeCustomColumn,
    ...this.dirTreeDefaultColumns,
  ];

  constructor() {}

  ngOnInit() {}

  public getDirTreeShowOn(index: number) {
    const minWithForMultipleColumns = 400;
    const nextColumnStep = 100;
    return minWithForMultipleColumns + nextColumnStep * index;
  }
}
