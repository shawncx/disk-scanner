import { Component, OnInit, Input } from '@angular/core';


interface ExtensionItem {
    extension: string;
    sizeInByte: number;
    count: number;
}

interface SummaryInfo {
  timeCostInSecond: number;
  fileCount: number;
  dirCount: number;
  size: string;
  baseDir: string;
  excludedPaths: string[];
}

@Component({
  selector: 'app-summary-info',
  templateUrl: './summary-info.component.html',
  styleUrls: ['./summary-info.component.scss'],
})
export class SummaryInfoComponent implements OnInit {
  @Input()
  public set summaryInfo(info: Model.SummaryInfo) {
    if (!info) {
      this.info = null;
      return;
    }
    this.info = {
      timeCostInSecond: info.timeCostInSecond,
      fileCount: info.fileCount,
      dirCount: info.dirCount,
      size: info.size,
      baseDir: info.baseDir,
      excludedPaths: info.excludedPaths,
    };
    this.extensionItems = info.extensionItems.map(
      (modelItem) =>
        <ExtensionItem>{
          extension: modelItem.extension,
          sizeInByte: modelItem.sizeInByte,
          count: modelItem.count,
        }
    );
  }

  public info: SummaryInfo;
  public extensionItems: ExtensionItem[];

  constructor() {}

  ngOnInit() {}
}
