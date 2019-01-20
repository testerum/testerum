import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {StatsComponent} from './stats/stats.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {FormsModule} from "@angular/forms";
import { LineStatsComponent } from './stats/line-stats/line-stats.component';
import {ChartModule} from "primeng/chart";
import {ModalModule} from "ngx-bootstrap";
import {CalendarModule, CheckboxModule, ToggleButtonModule, TooltipModule, TreeTableModule} from "primeng/primeng";
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
        ToggleButtonModule,
        CheckboxModule,
    ],
    declarations: [
        AppComponent,
        StatsComponent,
        LineStatsComponent,
    ],
    entryComponents: [],
    providers: [
        StatsService,
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
