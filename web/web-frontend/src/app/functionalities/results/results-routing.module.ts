import {NgModule} from '@angular/core';
import {RouterModule, Routes} from "@angular/router";
import {SetupGuard} from "../../service/guards/setup.guard";
import {ResultsComponent} from "./results.component";

const testsRoutes: Routes = [
    {
        path: ":project/automated/results", component: ResultsComponent, canActivate: [SetupGuard], canActivateChild: [SetupGuard],
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
