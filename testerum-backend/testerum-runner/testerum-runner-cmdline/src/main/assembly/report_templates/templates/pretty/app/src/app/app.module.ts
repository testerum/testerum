import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {ReportComponent} from './report/report.component';
import {ReportPieComponent} from './report/pie/report-pie.component';
import {ReportService} from "./report/report.service";
import {ChartModule} from "primeng/chart";

@NgModule({
    imports: [
        BrowserModule,
        AppRoutingModule,

        ChartModule,
    ],
    declarations: [
        AppComponent,
        ReportComponent,
        ReportPieComponent,
    ],

    providers: [
        ReportService,
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
