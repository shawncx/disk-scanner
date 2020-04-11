import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

export interface ScanProgress {
  dirProcessed: number;
  fileProcessed: number;
  timeCostInSecond: number;
  candidateInQueue: number;
  done: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class ScanService {

  private static readonly StartScanUrl = 'http://localhost:8080/scanner/start';
  private static readonly GetProgressUrl =
    'http://localhost:8080/scanner/progress?uuid=';

  constructor(private client: HttpClient) {}

  public async startScan(baseDir: string): Promise<string> {
    const body = JSON.stringify(this.buildStartScanRequest(baseDir));
    const headers: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'});
    return this.client
      .post<Model.StartScanResponse>(ScanService.StartScanUrl, body, {headers})
      .toPromise()
      .then((resp) => resp.uuid);
  }

  public async getProgress(uuid: string): Promise<ScanProgress> {
    return this.client
      .get<Model.GetScanProgressResponse>(ScanService.GetProgressUrl + uuid)
      .toPromise()
      .then(
        (resp) =>
          <ScanProgress>{
            dirProcessed: resp.progress.dirProcessed,
            fileProcessed: resp.progress.fileProcessed,
            timeCostInSecond: resp.progress.timeCostInSecond,
            candidateInQueue: resp.progress.candidateInQueue,
            done: resp.progress.done,
          }
      );
  }

  private buildStartScanRequest(baseDir: string): Model.StartScanRequest {
    return {
      threadNum: 5, // TODO: magic number. Detect processor
      baseDir,
      excludedPaths: [],
      outputTypes: ['Api'],
      fileOutputLoc: '',
      fileSizeUnit: 'Auto',
      dirSizeUnit: 'Auto',
      fileTopCount: 1000,
    };
  }
}
