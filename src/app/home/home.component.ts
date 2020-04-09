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
import { ScanService } from '../services/scan.service';
import {
  ScanResultService,
  SummaryInfo,
  FileItem,
  DirectoryTreeNode,
  DirectoryTreeEntry,
} from '../services/scan-result.service';

import { IpcRenderer } from 'electron';


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

  public summaryInfo: SummaryInfo;
  public dirTree: NbTreeGridDataSource<DirectoryTreeNode<DirectoryTreeEntry>>;
  public files: FileItem[];

  public scanProgressValue: number = 0;
  public scanProgress = '';

  private electronIpc: IpcRenderer;

  constructor(
    private dialogService: NbDialogService,
    private dataSourceBuilder: NbTreeGridDataSourceBuilder<
      DirectoryTreeNode<DirectoryTreeEntry>
    >,
    private scanService: ScanService,
    private scanResultService: ScanResultService
  ) {
    if ((<any>window).require) {
      try {
        this.electronIpc = (<any>window).require('electron').ipcRenderer;
      } catch (e) {
        alert(e);
      }
    } else {
      alert('App not running inside Electron!');
    }
  }

  public getShowOn(index: number) {
    const minWithForMultipleColumns = 400;
    const nextColumnStep = 100;
    return minWithForMultipleColumns + nextColumnStep * index;
  }

  public onBaseDirectoryChanged(evt: any): void {
    console.log(evt);
  }

  ngOnInit() {
    this.scanService.getProgress().then((progress) => {
      const processedNum = progress.fileProcessed + progress.dirProcessed;
      const totalNum = processedNum + progress.candidateInQueue;
      this.scanProgressValue = (processedNum / totalNum) * 100;
      this.scanProgress = processedNum + '/' + totalNum;
    });
    this.scanResultService.getSummary().then((summary) => {
      this.summaryInfo = summary;
    });
    this.scanResultService.getDirectoryTree().then((root) => {
      this.dirTree = this.dataSourceBuilder.create([root]);
    });
    this.scanResultService.getFiles().then((files) => (this.files = files));
  }

  openDialog() {
    // this.electronIpc.send("openDirectoryWindow");
    const { BrowserWindow } = require('electron').remote
    let win = new BrowserWindow({ width: 800, height: 600 })
win.loadURL('https://github.com')
  }
}
