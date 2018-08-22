import { NgModule } from '@angular/core';

import {Routes, RouterModule} from "@angular/router";
import {StepsComponent} from "./steps.component";
import {TestResolver} from "../features/test-editor/test.resolver";
import {ComposedStepEditorComponent} from "./composed-step-editor/composed-step-editor.component";
import {ComposedStepEditorResolver} from "./composed-step-editor/composed-step-editor.resolver";
import {SetupGuard} from "../../service/guards/setup.guard";
import {BasicStepEditorComponent} from "./basic-step-editor/basic-step-editor.component";
import {BasicStepEditorResolver} from "./basic-step-editor/basic-step-editor.resolver";

const stepsRoutes: Routes = [
    {
        path:"steps", component:StepsComponent, canActivate: [SetupGuard], canActivateChild: [SetupGuard],
        children: [
            {
                path: 'composed', component: ComposedStepEditorComponent, resolve: {composedStepDef: ComposedStepEditorResolver}
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
