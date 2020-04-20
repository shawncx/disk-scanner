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
  color: string;
}

interface ChartItem {
  name: string;
  value: number;
  color: string;
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
      size: getFileSize(info.sizeInByte),
      baseDir: info.baseDir,
      excludedPaths: info.excludedPaths,
    };

    this.topFiles = info.topFiles.map(
      (modelItem) =>
        <FileItem>{
          absolutePath: modelItem.absolutePath,
          sizeInByte: modelItem.sizeInByte,
          size: getFileSize(modelItem.sizeInByte),
        }
    );
    if (this.extensionSizeChartRef && this.extensionCountChartRef) {
      const [
        topSizeExtensionItems,
        topCountExtensionItems,
      ] = this.prepareExtensionChartItems(info.extensionItems);
      if (this._extensionSizeChart) {
        this.updateChart(this._extensionSizeChart, topSizeExtensionItems);
      } else {
        this._extensionSizeChart = this.createChart(
          this.extensionSizeChartRef.nativeElement,
          topSizeExtensionItems
        );
      }
      if (this._extensionCountChart) {
        this.updateChart(this._extensionCountChart, topCountExtensionItems);
      } else {
        this._extensionCountChart = this.createChart(
          this.extensionCountChartRef.nativeElement,
          topCountExtensionItems
        );
      }
    }
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

  private _extensionSizeChart: Chart;
  private _extensionCountChart: Chart;

  constructor() {}

  ngOnInit() {}

  private prepareExtensionChartItems(
    modelItems: Model.ExtensionItem[]
  ): ChartItem[][] {
    const colorMap = new Map<string, string>();
    let colorIndex = 0;
    const topSizeExtensionItems = modelItems
      .sort((a, b) => b.sizeInByte - a.sizeInByte)
      .filter((modelItem, index) => index < 10)
      .map((modelItem) => {
        const color = colorMap.has(modelItem.extension)
          ? colorMap.get(modelItem.extension)
          : SummaryInfoComponent.COLORS[colorIndex++];
        colorMap.set(modelItem.extension, color);
        return <ChartItem>{
          name: modelItem.extension,
          value: modelItem.sizeInByte,
          color,
        };
      });

    const topCountExtensionItems = modelItems
      .sort((a, b) => b.count - a.count)
      .filter((modelItem, index) => index < 10)
      .map((modelItem) => {
        const color = colorMap.has(modelItem.extension)
          ? colorMap.get(modelItem.extension)
          : SummaryInfoComponent.COLORS[colorIndex++];
        colorMap.set(modelItem.extension, color);
        return <ChartItem>{
          name: modelItem.extension,
          value: modelItem.count,
          color,
        };
      });
    return [topSizeExtensionItems, topCountExtensionItems];
  }

  private createChart(element: any, chartItems: ChartItem[]): Chart {
    return new Chart(element, {
      type: 'pie',
      data: {
        labels: chartItems.map((item) => item.name),
        datasets: [
          {
            data: chartItems.map((item) => item.value),
            backgroundColor: chartItems.map((item) => item.color),
            borderWidth: 1,
          },
        ],
      },
    });
  }

  private updateChart(chart: Chart, chartItems: ChartItem[]): void {
    this.clearExistedChartData(chart);
    chart.data.labels = chartItems.map((item) => item.name);
    chart.data.datasets = [
      {
        data: chartItems.map((item) => item.value),
        backgroundColor: chartItems.map((item) => item.color),
        borderWidth: 1,
      },
    ];
    chart.update();
  }

  private clearExistedChartData(chart: Chart): void {
    chart.data.labels = [];
    chart.data.datasets.forEach((dataset) => {
      dataset.data = [];
      dataset.backgroundColor = [];
    });
  }
}
