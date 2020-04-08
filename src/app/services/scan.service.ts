import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface ScanProgress {
  dirProcessed: number;
  fileProcessed: number;
  timeCostInSecond: number;
  candidateInQueue: number;
  done: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ScanService {

  constructor(private client: HttpClient) {}

  public async getProgress(): Promise<ScanProgress> {
    return this.client
      .get<Model.GetScanProgressResponse>('./assets/progress-sample.json')
      .toPromise()
      .then((resp) =>
        <ScanProgress> {
          dirProcessed: resp.progress.dirProcessed,
          fileProcessed: resp.progress.fileProcessed,
          timeCostInSecond: resp.progress.timeCostInSecond,
          candidateInQueue: resp.progress.candidateInQueue,
          done: resp.progress.done
        }
      )
      .catch((e) => {
        return null;
      });
  }
}
