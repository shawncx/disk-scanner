import {
  Component,
  OnInit,
  TemplateRef,
  ChangeDetectionStrategy,
} from '@angular/core';
import {
  NbDialogService,
  NbTreeGridDataSource,
  NbTreeGridDataSourceBuilder,
} from '@nebular/theme';
import {
  DirectoryInfoService,
  DirectoryTreeNode,
  DirectoryTreeEntry,
} from '../services/directory-info.service';
import { FileInfoService, FileItem } from '../services/file-info.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeComponent implements OnInit {
  public customColumn = 'name';
  public defaultColumns = ['size', 'items'];
  public allColumns = [this.customColumn, ...this.defaultColumns];

  public dirTree: NbTreeGridDataSource<DirectoryTreeNode<DirectoryTreeEntry>>;

  public files: FileItem[];

  constructor(
    private dialogService: NbDialogService,
    private dataSourceBuilder: NbTreeGridDataSourceBuilder<
      DirectoryTreeNode<DirectoryTreeEntry>
    >,
    private dirInfoService: DirectoryInfoService,
    private fileInfoService: FileInfoService
  ) {}

  public getShowOn(index: number) {
    const minWithForMultipleColumns = 400;
    const nextColumnStep = 100;
    return minWithForMultipleColumns + nextColumnStep * index;
  }

  ngOnInit() {
    this.dirInfoService.getDirectoryTree().then((root) => {
      this.dirTree = this.dataSourceBuilder.create([root]);
    });
    this.fileInfoService.getFiles().then((files) => this.files = files);
  }

  openDialog(dialog: TemplateRef<any>) {
    this.dialogService.open(dialog);
  }
}
