import { Component, OnInit, Input } from '@angular/core';
import { getFileSize } from '../utilies';

interface FileItem {
  absolutePath: string;
  size: string;
}

@Component({
  selector: 'app-file-info',
  templateUrl: './file-info.component.html',
  styleUrls: ['./file-info.component.scss'],
})
export class FileInfoComponent implements OnInit {
  @Input()
  public set fileInfo(info: Model.FileInfo) {
    if (!info) {
      this.files = [];
      return;
    }
    this.files = info.files.map(
      (model) =>
        <FileItem>{
          absolutePath: model.absolutePath,
          size: getFileSize(model.sizeInByte),
        }
    );
  }

  public files: FileItem[];

  constructor() {}

  ngOnInit() {}
}
