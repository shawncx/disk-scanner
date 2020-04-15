import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class ScanResultService {
  private static readonly GetSummaryInfoUrl =
    'http://localhost:8080/scanner/summary-info?uuid=';
  private static readonly GetDirectoryInfoUrl =
    'http://localhost:8080/scanner/directory-info?uuid=';
  private static readonly GetFileInfoUrl =
    'http://localhost:8080/scanner/file-info?uuid=';

  constructor(private client: HttpClient) {}

  public async getSummaryInfo(uuid: string): Promise<Model.SummaryInfo> {
    return this.client
      .get<Model.GetSummaryInfoResponse>(
        ScanResultService.GetSummaryInfoUrl + uuid
      )
      .toPromise()
      .then((resp) => resp.info)
      .catch((e) => {
        return null;
      });
  }

  public async getFileInfo(uuid: string): Promise<Model.FileInfo> {
    return this.client
      .get<Model.GetFileInfoResponse>(ScanResultService.GetFileInfoUrl + uuid)
      .toPromise()
      .then((resp) => resp.info)
      .catch((e) => {
        return null;
      });
  }

  public async getDirectoryInfo(uuid: string): Promise<Model.DirectoryInfo> {
    return this.client
      .get<Model.GetDirectoryInfoResponse>(
        ScanResultService.GetDirectoryInfoUrl + uuid
      )
      .toPromise()
      .then((resp) => resp.info)
      .catch((e) => {
        return null;
      });
  }

  public async getFakeDirectoryInfo(): Promise<Model.DirectoryInfo> {
    return this.client
      .get<Model.GetDirectoryInfoResponse>('./assets/dir-info-sample.json')
      .toPromise()
      .then((resp) => resp.info)
      .catch((e) => {
        return null;
      });
  }

  public async getFakeSummaryInfo(): Promise<Model.SummaryInfo> {
    return this.client
      .get<Model.GetSummaryInfoResponse>('./assets/summary-info-sample.json')
      .toPromise()
      .then((resp) => resp.info)
      .catch((e) => {
        return null;
      });
  }

  public async getFakeFileInfo(): Promise<Model.FileInfo> {
    return this.client
      .get<Model.GetFileInfoResponse>('./assets/file-info-sample.json')
      .toPromise()
      .then((resp) => resp.info)
      .catch((e) => {
        return null;
      });
  }
}
