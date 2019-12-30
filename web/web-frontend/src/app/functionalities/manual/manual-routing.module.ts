import {NgModule} from '@angular/core';

import {RouterModule, Routes} from "@angular/router";
import {ManualTestPlansComponent} from "./plans/manual-test-plans.component";
import {ManualTestPlanEditorComponent} from "./plans/editor/manual-test-plan-editor.component";
import {ManualTestPlanEditorResolver} from "./plans/editor/manual-test-plan-editor-resolver.service";
import {ManualRunnerComponent} from "./runner/manual-runner.component";
import {UnsavedChangesGuard} from "../../service/guards/unsaved-changes.guard";
import {CurrentProjectGuard} from "../../service/guards/current-project.guard";

const manualRoutes: Routes = [
    {
        path: ":project/manual/plans/runner",
        component: ManualRunnerComponent,
        canActivate: [CurrentProjectGuard],
        canDeactivate: [UnsavedChangesGuard]
    },
    {
        path: ":project/manual/plans",
        component: ManualTestPlansComponent,
        canActivate: [CurrentProjectGuard],
        children: [
            {
                path: ':action',
                component: ManualTestPlanEditorComponent,
                resolve: {manualExecPlan: ManualTestPlanEditorResolver},
                canDeactivate: [UnsavedChangesGuard]
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
