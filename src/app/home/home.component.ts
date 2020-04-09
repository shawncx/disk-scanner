import {
  Component,
  OnInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import {
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
import { ElectronIpcService, ElectronIpcChannel } from '../services/electron-ipc.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeComponent implements OnInit {
  public baseDirectory = 'C:\\Users\\xichen\\Documents\\Visual Studio 2012\\Templates\\ProjectTemplates';

  public customColumn = 'name';
  public defaultColumns = ['size', 'items'];
  public allColumns = [this.customColumn, ...this.defaultColumns];

  public summaryInfo: SummaryInfo;
  public dirTree: NbTreeGridDataSource<DirectoryTreeNode<DirectoryTreeEntry>>;
  public files: FileItem[];

  public scanProgressValue = 0;
  public scanProgress = '';

  constructor(
    private cd: ChangeDetectorRef,
    private electronIpcService: ElectronIpcService,
    private dataSourceBuilder: NbTreeGridDataSourceBuilder<
      DirectoryTreeNode<DirectoryTreeEntry>
    >,
    private scanService: ScanService,
    private scanResultService: ScanResultService
  ) {
    electronIpcService.on(
      ElectronIpcChannel.CloseDirecotryDialog,
      (event, args) => {
        this.baseDirectory = args;
        this.cd.detectChanges();
      }
    );
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

  public openDirectoryWindow() {
    this.electronIpcService.send(ElectronIpcChannel.OpenDirecotryDialog);
  }
}
