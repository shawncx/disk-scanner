import {
  Component,
  OnInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
} from '@angular/core';
import { NbToastrService } from '@nebular/theme';
import { ScanService, ScanProgress } from '../services/scan.service';
import { ScanResultService } from '../services/scan-result.service';
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

  public summaryInfo: Model.SummaryInfo;
  public fileInfo: Model.FileInfo;
  public directoryInfo: Model.DirectoryInfo;

  public isScanning = false;

  public baseDirectory = '';

  public scanProgressValue = 0;
  public scanProgress = '';
  public scanProgressStatus = 'info';

  private _progressSub: Subscription;

  constructor(
    private _cd: ChangeDetectorRef,
    private _electronIpcService: ElectronIpcService,
    private _toastrService: NbToastrService,
    private _scanService: ScanService,
    private _scanResultService: ScanResultService
  ) {
    this._electronIpcService.on(
      ElectronIpcChannel.CloseDirecotryDialog,
      (event, args) => {
        this.baseDirectory = args ? args[0] : args;
        this._cd.detectChanges();
      }
    );
  }

  public startScan(): void {
    if (!this.baseDirectory) {
      this._toastrService.danger('Base directory cannot be empty!', {
        duration: 0,
      });
    }
    this.onScanStart();
    this._scanService
      .startScan(this.baseDirectory)
      .then((uuid) => {
        this._progressSub = interval(500).subscribe(() => {
          this._scanService
            .getProgress(uuid)
            .then((progress) => {
              this.updateProgress(progress);
              if (progress.done) {
                this.onScanSucceeded(uuid);
                this._progressSub.unsubscribe();
              }
            })
            .catch((e) => {
              this.onScanFailed(e, 'Get progress failed! Terminate');
              this._progressSub.unsubscribe();
            });
        });
      })
      .catch((e) => this.onScanFailed(e, 'Start scann failed!'));
  }

  private onScanStart(): void {
    this.scanProgressStatus = 'info';
    this.scanProgressValue = 0;
    this.scanProgress = '0% (0/0)';

    this.isScanning = true;

    this._cd.detectChanges();
  }

  private onScanSucceeded(uuid: string): void {
    Promise.all([
      this._scanResultService.getSummaryInfo(uuid),
      this._scanResultService.getDirectoryInfo(uuid),
      this._scanResultService.getFileInfo(uuid),
    ])
      .then(([summaryInfo, directoryInfo, fileInfo]) => {
        this.summaryInfo = summaryInfo;
        this.directoryInfo = directoryInfo;
        this.fileInfo = fileInfo;

        this.scanProgressStatus = 'success';
        this.scanProgress += ' Done';

        this.isScanning = false;
      })
      .catch((ex) => {
        const error = ex && ex.error ? ex.error : ex;
        this._toastrService.danger(
          JSON.stringify(error, null, 2),
          'Fetch result failed!',
          {
            duration: 0,
          }
        );
      })
      .finally(() => this._cd.detectChanges());
  }

  private onScanFailed(ex: any, title: string): void {
    this.scanProgressStatus = 'danger';

    const error = ex && ex.error ? ex.error : ex;
    this._toastrService.danger(JSON.stringify(error, null, 2), title, {
      duration: 0,
    });

    this.isScanning = false;

    this._cd.detectChanges();
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
    this._cd.detectChanges();
  }

  ngOnInit() {
    // For test
    // Promise.all([
    //   this.scanResultService.getFakeDirectoryInfo(),
    //   this.scanResultService.getFakeSummaryInfo(),
    //   this.scanResultService.getFakeFileInfo()
    // ]).then(([dirInfo, summaryInfo, fileInfo]) => {
    //   this.directoryInfo = dirInfo;
    //   this.summaryInfo = summaryInfo;
    //   this.fileInfo = fileInfo;
    //   this.cd.detectChanges();
    // });
  }

  public openDirectoryWindow() {
    this._electronIpcService.send(ElectronIpcChannel.OpenDirecotryDialog);
  }
}
