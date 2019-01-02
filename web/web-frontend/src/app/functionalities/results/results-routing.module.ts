import {NgModule} from '@angular/core';
import {RouterModule, Routes} from "@angular/router";
import {SetupGuard} from "../../service/guards/setup.guard";
import {ResultsComponent} from "./results.component";
import {RunnerResultTabsComponent} from "./main/runner-result-tabs.component";

const testsRoutes: Routes = [
    {
        path: "automated/results", component: ResultsComponent, canActivate: [SetupGuard], canActivateChild: [SetupGuard],
        children: [
            {
                path: ':action', component: RunnerResultTabsComponent
            }
        ]
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
