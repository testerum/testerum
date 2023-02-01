import {NgModule} from '@angular/core';
import {ManualRoutingModule} from "./manual-routing.module";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {TableModule} from "primeng/table";
import {CollapseModule, ModalModule} from "ngx-bootstrap";
import {CardModule} from "primeng/card";
import {PanelModule} from "primeng/panel";
import {ChartModule} from "primeng/chart";
import {GenericModule} from "../../generic/generic.module";
import {ManualTestPlansComponent} from "./plans/manual-test-plans.component";
import {ManualTestPlansOverviewComponent} from "./plans/overview/manual-test-plans-overview.component";
import {ManualTestPlansService} from "./service/manual-test-plans.service";
import {ManualTestPlanOverviewComponent} from './plans/overview/item/manual-test-plan-overview.component';
import {ManualTestPlanEditorComponent} from './plans/editor/manual-test-plan-editor.component';
import {ManualTestPlanEditorResolver} from "./plans/editor/manual-test-plan-editor-resolver.service";
import {ManualSelectTestsTreeComponent} from "./plans/editor/manual-select-tests-tree/manual-select-tests-tree.component";
import {ManualSelectTestsContainerComponent} from "./plans/editor/manual-select-tests-tree/container/manual-select-tests-container.component";
import {ManualSelectTestsNodeComponent} from "./plans/editor/manual-select-tests-tree/container/node/manual-select-tests-node.component";
import {ManualTestsStatusTreeComponent} from "./common/manual-tests-status-tree/manual-tests-status-tree.component";
import {ManualTestsStatusTreeNodeComponent} from "./common/manual-tests-status-tree/nodes/runner-tree-node/manual-tests-status-tree-node.component";
import {ManualTestsStatusTreeService} from "./common/manual-tests-status-tree/manual-tests-status-tree.service";
import {ManualTestsStatusTreeToolbarComponent} from "./common/manual-tests-status-tree/toolbar/manual-tests-status-tree-toolbar.component";
import {ManualRunnerComponent} from "./runner/manual-runner.component";
import {ManualRunnerEditorComponent} from "./runner/editor/manual-runner-editor.component";
import {ManualRunnerStepStatusComponent} from "./runner/editor/step_status/manual-runner-step-status.component";
import {ManualTestPlansOverviewService} from "./plans/overview/manual-test-plans-overview.service";
import {AngularSplitModule} from "angular-split";
import {AutoCompleteModule} from "primeng/autocomplete";
import {DropdownModule} from "primeng/dropdown";
import {InputTextModule} from "primeng/inputtext";
import {TooltipModule} from "primeng/tooltip";
import {ToolbarModule} from "primeng/toolbar";
import {MessagesModule} from "primeng/messages";
import {MessageModule} from "primeng/message";

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        ManualRoutingModule,
        ModalModule.forRoot(),
        CollapseModule.forRoot(),
        AngularSplitModule.forRoot(),

        AutoCompleteModule,
        CardModule,
        ChartModule,
        DropdownModule,
        InputTextModule,
        PanelModule,
        TooltipModule,
        ToolbarModule,
        TableModule,
        MessagesModule,
        MessageModule,

        GenericModule,
    ],
    exports: [
    ],
    declarations: [
        ManualTestPlansComponent,
        ManualTestPlansOverviewComponent,
        ManualTestPlanOverviewComponent,
        ManualTestPlanEditorComponent,
        ManualSelectTestsTreeComponent,

        ManualSelectTestsContainerComponent,
        ManualSelectTestsNodeComponent,

        ManualTestsStatusTreeComponent,
        ManualTestsStatusTreeNodeComponent,
        ManualTestsStatusTreeToolbarComponent,

        ManualRunnerComponent,
        ManualRunnerEditorComponent,
        ManualRunnerStepStatusComponent,
    ],
    entryComponents: [
        ManualSelectTestsContainerComponent,
        ManualSelectTestsNodeComponent,

        ManualTestsStatusTreeNodeComponent,
    ],
    providers: [
        ManualTestPlansOverviewService,
        ManualTestPlansService,
        ManualTestsStatusTreeService,

        ManualTestPlanEditorResolver,
    ],
})
export class ManualModule { }
