import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {ReportComponent} from './report/report.component';
import {ReportPieComponent} from './report/pie/report-pie.component';
import {ReportService} from "./report/report.service";
import {ChartModule} from "primeng/chart";
import {TreeTableModule} from "primeng/primeng";
import {ReportGridComponent} from './report/grid/report-grid.component';
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";

@NgModule({
    imports: [
        BrowserModule,
        AppRoutingModule,

        FontAwesomeModule,
        ChartModule,
        TreeTableModule,
    ],
    declarations: [
        AppComponent,
        ReportComponent,
        ReportPieComponent,
        ReportGridComponent,
    ],

    providers: [
        ReportService,
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
