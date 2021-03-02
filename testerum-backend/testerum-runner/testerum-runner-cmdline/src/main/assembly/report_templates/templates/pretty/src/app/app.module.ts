import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {ReportComponent} from './report/report.component';
import {ReportPieComponent} from './report/pie/report-pie.component';
import {ReportService} from "./service/report.service";
import {ChartModule} from "primeng/chart";
import {AutoCompleteModule} from 'primeng/autocomplete';
import {ReportGridComponent} from './report/grid/report-grid.component';
import {LogsModalService} from "./report/grid/logs-modal/logs-modal.service";
import {LogsModalComponent} from "./report/grid/logs-modal/logs-modal.component";
import {ModalModule} from "ngx-bootstrap";
import {LogsComponent} from "./report/grid/logs-modal/logs/logs.component";
import {TitleComponent} from './report/title/title.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {TagOverviewComponent} from './tag-overview/tag-overview.component';
import {ReportUrlService} from "./service/report-url.service";
import {FormsModule} from "@angular/forms";
import {ReportTagPieComponent} from "./tag-overview/pie/report-tag-pie.component";
import {TooltipModule} from "primeng/tooltip";
import {TreeTableModule} from "primeng/treetable";

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
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

        TagOverviewComponent,
        ReportTagPieComponent,
    ],
    entryComponents: [
        LogsModalComponent,
    ],
    providers: [
        ReportUrlService,
        ReportService,
        LogsModalService,
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
