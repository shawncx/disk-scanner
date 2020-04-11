import { Component, OnInit, Input } from '@angular/core';
import { FileItem } from 'src/app/services/scan-result.service';

@Component({
  selector: 'app-file-info',
  templateUrl: './file-info.component.html',
  styleUrls: ['./file-info.component.scss']
})
export class FileInfoComponent implements OnInit {

  @Input()
  public files: FileItem[];

  constructor() { }

  ngOnInit() {
  }

}
