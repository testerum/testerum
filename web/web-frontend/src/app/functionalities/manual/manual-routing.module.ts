import {NgModule} from '@angular/core';

import {RouterModule, Routes} from "@angular/router";
import {LicenseGuard} from "../../service/guards/license.guard";
import {ManualTestPlansComponent} from "./plans/manual-test-plans.component";
import {ManualTestPlanEditorComponent} from "./plans/editor/manual-test-plan-editor.component";
import {ManualTestPlanEditorResolver} from "./plans/editor/manual-test-plan-editor-resolver.service";
import {ManualRunnerComponent} from "./runner/manual-runner.component";
import {UnsavedChangesGuard} from "../../service/guards/unsaved-changes.guard";

const manualRoutes: Routes = [
    {
        path: ":project/manual/plans/runner",
        component: ManualRunnerComponent,
        canActivate: [LicenseGuard],
        canActivateChild: [LicenseGuard],
        canDeactivate: [UnsavedChangesGuard]
    },
    {
        path: ":project/manual/plans",
        component: ManualTestPlansComponent,
        canActivate: [LicenseGuard],
        canActivateChild: [LicenseGuard],
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
