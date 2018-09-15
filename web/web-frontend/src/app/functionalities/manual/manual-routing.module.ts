import {NgModule} from '@angular/core';

import {RouterModule, Routes} from "@angular/router";
import {SetupGuard} from "../../service/guards/setup.guard";
import {ManualExecPlansComponent} from "./plans/manual-exec-plans.component";
import {ManualExecPlanEditorComponent} from "./plans/editor/manual-exec-plan-editor.component";
import {ManualExecPlanEditorResolver} from "./plans/editor/manual-exec-plan-editor.resolver";
import {ManualRunnerComponent} from "./runner/manual-runner.component";

const manualRoutes: Routes = [

    {
        path: "manual/plans/runner",
        component: ManualRunnerComponent,
        canActivate: [SetupGuard],
        canActivateChild: [SetupGuard],
    },
    {
        path: "manual/plans",
        component: ManualExecPlansComponent,
        canActivate: [SetupGuard],
        canActivateChild: [SetupGuard],
        children: [
            {
                path: ':action',
                component: ManualExecPlanEditorComponent,
                resolve: {manualExecPlan: ManualExecPlanEditorResolver}
            }
        ]
    },
];

@NgModule({
    imports: [
        RouterModule.forChild(manualRoutes)
    ],
    exports: [RouterModule],
    providers: [],
})
export class ManualRoutingModule {
}