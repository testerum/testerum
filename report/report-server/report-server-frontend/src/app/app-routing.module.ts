import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ReportSelectionComponent} from "./components/report-selection/report-selection.component";
import {ReportSelectionResolver} from "./components/report-selection/report-selection.resolver";
import {ReportComponent} from "./components/report/report.component";
import {ReportResolver} from "./components/report/report.resolver";

const routes: Routes = [
  { path: "reports/:reportType/:projectId", component: ReportComponent, resolve: {selectedReport: ReportResolver}},
  { path: "reports", component: ReportSelectionComponent, resolve: {reports: ReportSelectionResolver}},
  { path: '**', redirectTo: "reports" }
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
