import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {StatsComponent} from './stats/stats.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {FormsModule} from "@angular/forms";
import {LineStatsComponent} from './stats/line-stats/line-stats.component';
import {ChartModule} from "primeng/chart";
import {ModalModule} from "ngx-bootstrap";
import {StatsService} from "./service/stats.service";
import {TagUptimeComponent} from './stats/tag-uptime/tag-uptime.component';
import {TableModule} from "primeng/table";
import {CalendarModule} from "primeng/calendar";
import {CheckboxModule, ToggleButtonModule, TooltipModule, TreeTableModule} from "primeng/primeng";

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
        CalendarModule,
        ToggleButtonModule,
        CheckboxModule,
        TableModule,
    ],
    declarations: [
        AppComponent,
        StatsComponent,
        LineStatsComponent,
        TagUptimeComponent,
    ],
    entryComponents: [],
    providers: [
        StatsService,
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
