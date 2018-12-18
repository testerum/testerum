import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {APP_BASE_HREF} from "@angular/common";
import {ReportComponent} from "./report/report.component";

const routes: Routes = [
  { path: '', component: ReportComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule],
  providers: [
    {
      provide: APP_BASE_HREF,
      useValue: "/"
    }
  ]
})
export class AppRoutingModule { }
