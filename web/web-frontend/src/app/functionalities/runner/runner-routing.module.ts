import {NgModule} from '@angular/core';
import {Routes, RouterModule} from "@angular/router";
import {SetupGuard} from "../../service/guards/setup.guard";
import {RunnerComponent} from "./runner.component";
import {ResultResolver} from "./main/result/result.resolver";
import {RunnerResultTabsComponent} from "./main/runner-result-tabs.component";

const testsRoutes: Routes = [
    {
        path: ":project/automated/results", component: RunnerComponent, canActivate: [SetupGuard], canActivateChild: [SetupGuard],
        children: [
            {
                path: ':action', component: RunnerResultTabsComponent, resolve: {runnerEvents: ResultResolver}
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
export class RunnerRoutingModule {
}
