import { Component, OnInit, Input } from '@angular/core';
import { SummaryInfo } from 'src/app/services/scan-result.service';

@Component({
  selector: 'app-summary-info',
  templateUrl: './summary-info.component.html',
  styleUrls: ['./summary-info.component.scss']
})
export class SummaryInfoComponent implements OnInit {

  @Input()
  public summaryInfo: SummaryInfo;

  constructor() { }

  ngOnInit() {
  }

}
