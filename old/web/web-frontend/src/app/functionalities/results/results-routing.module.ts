import {NgModule} from '@angular/core';
import {RouterModule, Routes} from "@angular/router";
import {ResultsComponent} from "./results.component";
import {CurrentProjectGuard} from "../../service/guards/current-project.guard";

const testsRoutes: Routes = [
    {
        path: ":project/automated/results", component: ResultsComponent, canActivate: [CurrentProjectGuard],
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
