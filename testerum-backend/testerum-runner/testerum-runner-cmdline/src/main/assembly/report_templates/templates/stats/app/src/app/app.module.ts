import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {StatsComponent} from './stats/stats.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {FormsModule} from "@angular/forms";
import { TestsStatsComponent } from './stats/tests-stats/tests-stats.component';
import {ChartModule} from "primeng/chart";
import {ModalModule} from "ngx-bootstrap";
import {CalendarModule, TooltipModule, TreeTableModule} from "primeng/primeng";
import {StatsService} from "./service/stats.service";

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
    ],
    declarations: [
        AppComponent,
        StatsComponent,
        TestsStatsComponent
    ],
    entryComponents: [],
    providers: [
        StatsService,
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
