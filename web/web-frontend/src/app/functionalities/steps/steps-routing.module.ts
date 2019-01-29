import {NgModule} from '@angular/core';

import {RouterModule, Routes} from "@angular/router";
import {StepsComponent} from "./steps.component";
import {ComposedStepEditorComponent} from "./composed-step-editor/composed-step-editor.component";
import {ComposedStepEditorResolver} from "./composed-step-editor/composed-step-editor.resolver";
import {LicenseGuard} from "../../service/guards/license.guard";
import {BasicStepEditorComponent} from "./basic-step-editor/basic-step-editor.component";
import {BasicStepEditorResolver} from "./basic-step-editor/basic-step-editor.resolver";
import {UnsavedChangesGuard} from "../../service/guards/unsaved-changes.guard";
import {CurrentProjectGuard} from "../../service/guards/current-project.guard";

const stepsRoutes: Routes = [
    {
        path:":project/steps", component:StepsComponent, canActivate: [LicenseGuard, CurrentProjectGuard], canActivateChild: [LicenseGuard],
        children: [
            {
                path: 'composed', component: ComposedStepEditorComponent, resolve: {composedStepDef: ComposedStepEditorResolver}, canDeactivate: [UnsavedChangesGuard]
            },
            {
                path: 'composed/:action', component: ComposedStepEditorComponent, resolve: {composedStepDef: ComposedStepEditorResolver}
            },
            {
                path: 'basic', component: BasicStepEditorComponent, resolve: {basicStepDef: BasicStepEditorResolver}
            }
        ]
    }
];
@NgModule({
    imports: [
        RouterModule.forChild(stepsRoutes),

    ],
    exports: [RouterModule],
    providers: [],
})
export class StepsRoutingModule { }
