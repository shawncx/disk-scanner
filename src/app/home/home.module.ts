import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  NbButtonModule,
  NbCardModule,
  NbDialogModule,
  NbTreeGridModule,
  NbIconModule,
  NbInputModule,
  NbAccordionModule,
  NbListModule,
  NbProgressBarModule,
  NbLayoutModule,
  NbCheckboxModule,
} from '@nebular/theme';

import { HomeRoutingModule } from './home-routing.module';
import { HomeComponent } from './home.component';
import { DirectoryInfoComponent } from '../components/directory-info/directory-info.component';
import { FileInfoComponent } from '../components/file-info/file-info.component';
import { SummaryInfoComponent } from '../components/summary-info/summary-info.component';
import { ChartsModule } from 'ng2-charts';

@NgModule({
  declarations: [
    HomeComponent,
    SummaryInfoComponent,
    FileInfoComponent,
    DirectoryInfoComponent,
  ],
  imports: [
    CommonModule,
    ChartsModule,
    NbCardModule,
    NbButtonModule,
    NbDialogModule.forRoot(),
    NbLayoutModule,
    HomeRoutingModule,
    NbIconModule,
    NbInputModule,
    NbTreeGridModule,
    NbAccordionModule,
    NbListModule,
    NbProgressBarModule,
    NbCheckboxModule
  ],
})
export class HomeModule {}
