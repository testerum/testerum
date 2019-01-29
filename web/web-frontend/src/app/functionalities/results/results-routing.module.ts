import {NgModule} from '@angular/core';
import {RouterModule, Routes} from "@angular/router";
import {LicenseGuard} from "../../service/guards/license.guard";
import {ResultsComponent} from "./results.component";

const testsRoutes: Routes = [
    {
        path: ":project/automated/results", component: ResultsComponent, canActivate: [LicenseGuard], canActivateChild: [LicenseGuard],
    },
];
@NgModule({
    imports: [
        RouterModule.forChild(testsRoutes)
    ],
    exports: [RouterModule],
    providers: [],
})
export class ResultsRoutingModule {
}
