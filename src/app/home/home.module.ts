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
} from '@nebular/theme';

import { HomeRoutingModule } from './home-routing.module';
import { HomeComponent } from './home.component';

@NgModule({
  declarations: [HomeComponent],
  imports: [
    CommonModule,
    NbCardModule,
    NbButtonModule,
    NbDialogModule.forRoot(),
    HomeRoutingModule,
    NbIconModule,
    NbInputModule,
    NbTreeGridModule,
    NbAccordionModule,
    NbListModule,
    NbProgressBarModule
  ],
})
export class HomeModule {}
