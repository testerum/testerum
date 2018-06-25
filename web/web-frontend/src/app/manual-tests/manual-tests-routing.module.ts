import {NgModule} from '@angular/core';

import {Routes, RouterModule} from "@angular/router";
import {ManualTestsComponent} from "./tests/manual-tests.component";
import {SetupGuard} from "../service/guards/setup.guard";
import {ManualTestEditorComponent} from "./tests/test-editor/manual-test-editor.component";
import {ManualTestResolver} from "./tests/test-editor/manual-test.resolver";
import {ManualTestsRunnerComponent} from "./runner/manual-tests-runner.component";
import {ManualTestsRunnerEditorResolver} from "./runner/editor/manual-tests-runner-editor.resolver";
import {ManualTestsRunnerEditorComponent} from "./runner/editor/manual-tests-runner-editor.component";
import {ManualTestsExecutorResolver} from "./executer/manual-tests-executor.resolver";
import {ManualTestsExecutorComponent} from "./executer/manual-tests-executor.component";
import {ManualTestsExecutorEditorComponent} from "./executer/editor/manual-tests-executor-editor.component";
import {ManualTestsExecutorEditorResolver} from "./executer/editor/manual-tests-executor-editor.resolver";

const testsRoutes: Routes = [
    {
        path: "manual/execute",
        component: ManualTestsExecutorComponent,
        resolve: {manualTestRunner: ManualTestsExecutorResolver},
        canActivate: [SetupGuard], canActivateChild: [SetupGuard],
        children: [
            {
                path: 'test', component: ManualTestsExecutorEditorComponent, resolve: {manualTestRunner: ManualTestsExecutorEditorResolver}
            }
        ]
    },
    {
        path: "manual/tests", component: ManualTestsComponent, canActivate: [SetupGuard], canActivateChild: [SetupGuard],
        children: [
            {
                path: ':action', component: ManualTestEditorComponent, resolve: {manualTestModel: ManualTestResolver}
            }
        ]
    },
    {
        path: "manual/runner", component: ManualTestsRunnerComponent, canActivate: [SetupGuard], canActivateChild: [SetupGuard],
        children: [
            {
                path: ':action', component: ManualTestsRunnerEditorComponent, resolve: {manualTestRunner: ManualTestsRunnerEditorResolver}
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
export class ManualTestsRoutingModule {
}
