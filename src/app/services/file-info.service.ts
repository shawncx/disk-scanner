import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface FileItem {
  absolutePath: string;
  size: string;
}

@Injectable({
  providedIn: 'root',
})
export class FileInfoService {
  constructor(private client: HttpClient) {}

  public async getFiles(): Promise<FileItem[]> {
    return this.client
      .get<Model.GetFileInfoResponse>('./assets/file-info-sample.json')
      .toPromise()
      .then((resp) =>
        resp.files.map(
          (model) =>
            <FileItem>{
              absolutePath: model.absolutePath,
              size: model.size,
            }
        )
      )
      .catch((e) => {
        return new Array<FileItem>();
      });
  }
}
