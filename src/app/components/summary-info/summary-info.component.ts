import {
  Component,
  OnInit,
  Input,
  ViewChild,
  ChangeDetectorRef,
  ElementRef,
} from '@angular/core';
import { ChartOptions, ChartType } from 'chart.js';
import {
  Label,
  SingleDataSet,
  monkeyPatchChartJsTooltip,
  monkeyPatchChartJsLegend,
  BaseChartDirective,
} from 'ng2-charts';
import * as Chart from 'chart.js';
import { getFileSize } from '../utilies';

interface FileItem {
  absolutePath: string;
  sizeInByte: number;
  size: string;
}

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
  private static readonly COLORS: string[] = [
    '#3366cc',
    '#dc3912',
    '#ff9900',
    '#109618',
    '#990099',
    '#0099c6',
    '#dd4477',
    '#66aa00',
    '#b82e2e',
    '#316395',
    '#994499',
    '#22aa99',
    '#aaaa11',
    '#6633cc',
    '#e67300',
    '#8b0707',
    '#651067',
    '#329262',
    '#5574a6',
    '#3b3eac',
    '#b77322',
    '#16d620',
    '#b91383',
    '#f4359e',
    '#9c5935',
    '#a9c413',
    '#2a778d',
    '#668d1c',
    '#bea413',
    '#0c5922',
    '#743411',
  ];

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
    this.topFiles = info.topFiles.map(
      (modelItem) =>
        <FileItem>{
          absolutePath: modelItem.absolutePath,
          sizeInByte: modelItem.sizeInByte,
          size: getFileSize(modelItem.sizeInByte),
        }
    );

    if (this.extensionSizeChartRef) {
      console.log('a');
      this.extensionSizeChart = new Chart(
        this.extensionSizeChartRef.nativeElement,
        {
          type: 'pie',
          data: {
            labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
            datasets: [
              {
                label: '# of Votes',
                data: [12, 19, 3, 5, 2, 3],
                backgroundColor: SummaryInfoComponent.COLORS,
                borderWidth: 1,
              },
            ],
          },
        }
      );
    }

    if (this.extensionCountChartRef) {
      console.log('a');
      this.extensionCountChart = new Chart(
        this.extensionCountChartRef.nativeElement,
        {
          type: 'pie',
          data: {
            labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
            datasets: [
              {
                label: '# of Votes',
                data: [12, 19, 3, 5, 2, 3],
                backgroundColor: SummaryInfoComponent.COLORS,
                borderWidth: 1,
              },
            ],
          },
        }
      );
    }
    // this._cd.detectChanges();
  }

  @ViewChild('extensionSizeChart', { static: false })
  public extensionSizeChartRef: ElementRef;
  @ViewChild('extensionCountChart', { static: false })
  public extensionCountChartRef: ElementRef;

  public info: SummaryInfo;
  public extensionItems: ExtensionItem[];
  public topFiles: FileItem[];

  public pieChartOptions: ChartOptions = {
    responsive: true,
  };
  public pieChartLabels: Label[] = [];
  public pieChartData: SingleDataSet = [];
  public pieChartType: ChartType = 'pie';
  public pieChartLegend = true;
  public pieChartPlugins = [];

  private extensionSizeChart: Chart;
  private extensionCountChart: Chart;

  constructor(private _cd: ChangeDetectorRef) {
    monkeyPatchChartJsTooltip();
    monkeyPatchChartJsLegend();
  }

  ngOnInit() {}

  // private reloadExtensionChart(): void {
  //   if (this.extensionChart && this.extensionChart.chart) {
  //     this.extensionChart.chart.destroy();
  //     this.extensionChart.chart = null;

  //     this.extensionChart.data = this.pieChartData;
  //     this.extensionChart.labels = this.pieChartLabels;
  //     this.extensionChart.ngOnInit();
  //   }
  // }
}
