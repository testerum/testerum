import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {ReportComponent} from './report/report.component';
import {ReportPieComponent} from './report/pie/report-pie.component';
import {ReportService} from "./report/report.service";
import {ChartModule} from "primeng/chart";
import {TooltipModule, TreeTableModule} from "primeng/primeng";
import {AutoCompleteModule} from 'primeng/autocomplete';
import {ReportGridComponent} from './report/grid/report-grid.component';
import {LogsModalService} from "./report/grid/logs-modal/logs-modal.service";
import {LogsModalComponent} from "./report/grid/logs-modal/logs-modal.component";
import {ModalModule} from "ngx-bootstrap";
import {LogsComponent} from "./report/grid/logs-modal/logs/logs.component";
import { TitleComponent } from './report/title/title.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

@NgModule({
    imports: [
        BrowserModule,
        AppRoutingModule,
        BrowserAnimationsModule,

        ModalModule.forRoot(),
        ChartModule,
        TreeTableModule,
        TooltipModule,
        AutoCompleteModule,
    ],
    declarations: [
        AppComponent,
        ReportComponent,
        ReportPieComponent,
        ReportGridComponent,
        LogsModalComponent,
        LogsComponent,
        TitleComponent,
    ],
    entryComponents: [
        LogsModalComponent,
    ],
    providers: [
        ReportService,
        LogsModalService,
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
