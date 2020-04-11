import {
  Component,
  OnInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  ViewChild,
} from '@angular/core';
import {
  NbTreeGridDataSource,
  NbTreeGridDataSourceBuilder,
  NbToastrService,
  NbAccordionItemComponent,
} from '@nebular/theme';
import { ScanService, ScanProgress } from '../services/scan.service';
import {
  ScanResultService,
  SummaryInfo,
  FileItem,
  DirectoryTreeNode,
  DirectoryTreeEntry,
} from '../services/scan-result.service';
import {
  ElectronIpcService,
  ElectronIpcChannel,
} from '../services/electron-ipc.service';
import { Subscription } from 'rxjs';
import { interval } from 'rxjs';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeComponent implements OnInit {
  
  @ViewChild('summaryAccordion', { static: false })
  public summaryAccordion: NbAccordionItemComponent;
  @ViewChild('directoryAccordion', { static: false })
  public directoryAccordion: NbAccordionItemComponent;
  @ViewChild('fileAccordion', { static: false })
  public fileAccordion: NbAccordionItemComponent;

  public isScanning = false;

  public baseDirectory = '';

  public summaryInfo: SummaryInfo;
  public dirTree: NbTreeGridDataSource<DirectoryTreeNode<DirectoryTreeEntry>>;
  public files: FileItem[];

  public scanProgressValue = 0;
  public scanProgress = '';
  public scanProgressStatus = 'info';

  private progressSub: Subscription;

  constructor(
    private cd: ChangeDetectorRef,
    private electronIpcService: ElectronIpcService,
    private toastrService: NbToastrService,
    private dataSourceBuilder: NbTreeGridDataSourceBuilder<
      DirectoryTreeNode<DirectoryTreeEntry>
    >,
    private scanService: ScanService,
    private scanResultService: ScanResultService
  ) {
    electronIpcService.on(
      ElectronIpcChannel.CloseDirecotryDialog,
      (event, args) => {
        this.baseDirectory = args ? args[0] : args;
        this.cd.detectChanges();
      }
    );
  }

  public startScan(): void {
    if (!this.baseDirectory) {
      this.toastrService.danger('Base directory cannot be empty!', {
        duration: 0,
      });
    }
    this.onScanStart();
    this.scanService
      .startScan(this.baseDirectory)
      .then((uuid) => {
        this.progressSub = interval(500).subscribe(() => {
          this.scanService
            .getProgress(uuid)
            .then((progress) => {
              this.updateProgress(progress);
              if (progress.done) {
                this.fetchScanResult(uuid)
                  .then(() => this.onScanSucceeded(uuid))
                  .catch((e) =>
                    this.onScanFailed(e, 'Fetch scan result failed!')
                  );
                this.progressSub.unsubscribe();
              }
            })
            .catch((e) => {
              this.onScanFailed(e, 'Get progress failed! Terminate');
              this.progressSub.unsubscribe();
            });
        });
      })
      .catch((e) => this.onScanFailed(e, 'Start scann failed!'));
  }

  private onScanStart(): void {
    this.scanProgressStatus = 'info';
    this.scanProgressValue = 0;
    this.scanProgress = '0% (0/0)';

    this.summaryAccordion.close();
    this.directoryAccordion.close();
    this.fileAccordion.close();

    this.isScanning = true;

    this.cd.detectChanges();
  }

  private onScanSucceeded(uuid: string): void {
    Promise.all([
      this.scanResultService.getSummaryInfo(uuid),
      this.scanResultService.getDirectoryInfo(uuid),
      this.scanResultService.getFileInfo(uuid),
    ])
      .then(([summaryInfo, dirRoot, files]) => {
        this.summaryInfo = summaryInfo;
        this.dirTree = this.dataSourceBuilder.create([dirRoot]);
        this.files = files;

        this.scanProgressStatus = 'success';

        this.summaryAccordion.open();
        this.directoryAccordion.open();
        this.fileAccordion.open();

        this.isScanning = false;
      })
      .catch((ex) => {
        const error = ex && ex.error ? ex.error : ex;
        this.toastrService.danger(
          JSON.stringify(error, null, 2),
          'Fetch result failed!',
          {
            duration: 0,
          }
        );
      })
      .finally(() => this.cd.detectChanges());
  }

  private onScanFailed(ex: any, title: string): void {
    this.scanProgressStatus = 'danger';

    const error = ex && ex.error ? ex.error : ex;
    this.toastrService.danger(JSON.stringify(error, null, 2), title, {
      duration: 0,
    });

    this.isScanning = false;

    this.cd.detectChanges();
  }

  private async fetchScanResult(uuid: string): Promise<void> {
    this.scanResultService.getSummaryInfo(uuid).then((summary) => {
      this.summaryInfo = summary;
    });
    this.scanResultService.getDirectoryInfo(uuid).then((root) => {
      this.dirTree = this.dataSourceBuilder.create([root]);
    });
    this.scanResultService
      .getFileInfo(uuid)
      .then((files) => (this.files = files));
  }

  private async updateProgress(progress: ScanProgress): Promise<void> {
    const processedNum = progress.fileProcessed + progress.dirProcessed;
    const totalNum = processedNum + progress.candidateInQueue;
    this.scanProgressValue = (processedNum / totalNum) * 100;
    this.scanProgress =
      this.scanProgressValue.toFixed(2) +
      '% (' +
      processedNum +
      '/' +
      totalNum +
      ')';
    this.cd.detectChanges();
  }

  ngOnInit() {}

  public openDirectoryWindow() {
    this.electronIpcService.send(ElectronIpcChannel.OpenDirecotryDialog);
  }
}
