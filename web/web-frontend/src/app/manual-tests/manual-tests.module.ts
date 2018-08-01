import { NgModule } from '@angular/core';

import {ManualTestsRoutingModule} from "./manual-tests-routing.module";
import {GenericModule} from "../generic/generic.module";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {DndModule} from "ng2-dnd";
import {ManualTestsComponent} from "./tests/manual-tests.component";
import {AngularSplitModule} from "angular-split-ng6";
import {ManualTestsTreeService} from "./tests/tests-tree/manual-tests-tree.service";
import {ManualTestsService} from "./tests/service/manual-tests.service";
import {ManualTestTreeContainerComponent} from "./tests/tests-tree/container/manual-test-tree-container.component";
import {ManualTestTreeNodeComponent} from "./tests/tests-tree/container/node/manual-test-tree-node.component";
import {ManualTestEditorComponent} from "./tests/test-editor/manual-test-editor.component";
import {ManualTestResolver} from "./tests/test-editor/manual-test.resolver";
import {AutoCompleteModule, DropdownModule, EditorModule, InputTextModule, TooltipModule} from "primeng/primeng";
import {TableModule} from "primeng/table";
import {CollapseModule, ModalModule} from "ngx-bootstrap";
import {CardModule} from "primeng/card";
import {PanelModule} from "primeng/panel";
import {ChartModule} from "primeng/chart";
import {ManualTestsRunnerComponent} from "./runner/manual-tests-runner.component";
import {SelectTestTreeRunnerContainerComponent} from "./runner/editor/select-tests-tree/container/select-test-tree-runner-container.component";
import {ManualTestsOverviewRunnerComponent} from "./runner/overview/runner/manual-tests-overview-runner.component";
import {SelectTestTreeRunnerNodeComponent} from "./runner/editor/select-tests-tree/container/node/select-test-tree-runner-node.component";
import {ManualTestsOverviewComponent} from "./runner/overview/manual-tests-overview.component";
import {ManualTestsRunnerEditorComponent} from "./runner/editor/manual-tests-runner-editor.component";
import {ManualTestsRunnerEditorResolver} from "./runner/editor/manual-tests-runner-editor.resolver";
import {SelectTestsTreeRunnerService} from "./runner/editor/select-tests-tree/select-tests-tree-runner.service";
import {ManualTestsRunnerService} from "./runner/service/manual-tests-runner.service";
import {ManualTestsExecutorComponent} from "./executer/manual-tests-executor.component";
import {StepStatusComponent} from "./executer/editor/step_status/step-status.component";
import {ManualTestsExecutorTreeContainerComponent} from "./executer/tree/container/manual-tests-executor-tree-container.component";
import {ManualTestsExecutorTreeNodeComponent} from "./executer/tree/container/node/manual-tests-executor-tree-node.component";
import {ManualTestsExecutorEditorComponent} from "./executer/editor/manual-tests-executor-editor.component";
import {ManualTestsExecutorResolver} from "./executer/manual-tests-executor.resolver";
import {ManualTestsExecutorTreeService} from "./executer/tree/manual-tests-executor-tree.service";
import {ManualTestsExecutorEditorResolver} from "./executer/editor/manual-tests-executor-editor.resolver";
import {ManualTestsOverviewService} from "./runner/overview/manual-tests-overview.service";

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        ManualTestsRoutingModule,
        ModalModule.forRoot(),
        CollapseModule.forRoot(),
        DndModule.forRoot(),
        AngularSplitModule,

        AutoCompleteModule,
        CardModule,
        ChartModule,
        DropdownModule,
        EditorModule,
        InputTextModule,
        PanelModule,
        TooltipModule,
        TableModule,

        GenericModule,
    ],
    exports: [
    ],
    declarations: [
        ManualTestsComponent,
        ManualTestTreeContainerComponent,
        ManualTestTreeNodeComponent,

        ManualTestEditorComponent,

        ManualTestsRunnerComponent,
        ManualTestsOverviewComponent,
        ManualTestsRunnerEditorComponent,
        ManualTestsOverviewRunnerComponent,

        SelectTestTreeRunnerContainerComponent,
        SelectTestTreeRunnerNodeComponent,

        ManualTestsExecutorComponent,
        ManualTestsExecutorTreeContainerComponent,
        ManualTestsExecutorTreeNodeComponent,
        ManualTestsExecutorEditorComponent,

        StepStatusComponent,
    ],
    entryComponents: [

    ],
    providers: [
        ManualTestsService,
        ManualTestsTreeService,

        ManualTestsRunnerService,
        ManualTestsOverviewService,
        SelectTestsTreeRunnerService,

        ManualTestsExecutorTreeService,

        ManualTestResolver,
        ManualTestsRunnerEditorResolver,

        ManualTestsExecutorResolver,
        ManualTestsExecutorEditorResolver,
    ],
})
export class ManualTestsModule { }
