import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MenuComponent} from './components/menu/menu.component';
import {LogoComponent} from "./components/menu/logo/logo.component";
import {ReportSelectionComponent} from './components/report-selection/report-selection.component';
import {ReportService} from "./service/report.service";
import {ReportSelectionResolver} from "./components/report-selection/report-selection.resolver";
import {HttpClientModule} from "@angular/common/http";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatTableModule} from "@angular/material/table";
import {MatButtonModule} from "@angular/material/button";
import {ReportComponent} from './components/report/report.component';
import {ReportResolver} from "./components/report/report.resolver";
import {RouterModule} from "@angular/router";
import {CommonModule} from "@angular/common";

@NgModule({
  declarations: [
    AppComponent,
    MenuComponent,
    LogoComponent,
    ReportSelectionComponent,
    ReportComponent,
  ],
  imports: [
    BrowserModule,
    CommonModule,
    RouterModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,

    MatTableModule,
    MatButtonModule,
  ],
  providers: [
    ReportSelectionResolver,
    ReportResolver,

    ReportService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
