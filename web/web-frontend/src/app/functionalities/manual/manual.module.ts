import {NgModule} from '@angular/core';
import {ManualRoutingModule} from "./manual-routing.module";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {AngularSplitModule} from "angular-split-ng6";
import {AutoCompleteModule, DropdownModule, EditorModule, InputTextModule, TooltipModule} from "primeng/primeng";
import {TableModule} from "primeng/table";
import {CollapseModule, ModalModule} from "ngx-bootstrap";
import {CardModule} from "primeng/card";
import {PanelModule} from "primeng/panel";
import {ChartModule} from "primeng/chart";
import {GenericModule} from "../../generic/generic.module";
import {ManualExecPlansComponent} from "./plans/manual-exec-plans.component";
import {ManualExecPlansOverviewComponent} from "./plans/overview/manual-exec-plans-overview.component";
import {ManualExecPlansService} from "./service/manual-exec-plans.service";
import {ManualExecPlanOverviewComponent} from './plans/overview/item/manual-exec-plan-overview.component';
import {ManualExecPlanEditorComponent} from './plans/editor/manual-exec-plan-editor.component';
import {ManualExecPlanEditorResolver} from "./plans/editor/manual-exec-plan-editor.resolver";
import {ManualSelectTestsTreeComponent} from "./plans/editor/manual-select-tests-tree/manual-select-tests-tree.component";
import {ManualSelectTestsContainerComponent} from "./plans/editor/manual-select-tests-tree/container/manual-select-tests-container.component";
import {ManualSelectTestsNodeComponent} from "./plans/editor/manual-select-tests-tree/container/node/manual-select-tests-node.component";

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        ManualRoutingModule,
        ModalModule.forRoot(),
        CollapseModule.forRoot(),
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
        ManualExecPlansComponent,
        ManualExecPlansOverviewComponent,
        ManualExecPlanOverviewComponent,
        ManualExecPlanEditorComponent,
        ManualSelectTestsTreeComponent,

        ManualSelectTestsContainerComponent,
        ManualSelectTestsNodeComponent,
    ],
    entryComponents: [
        ManualSelectTestsContainerComponent,
        ManualSelectTestsNodeComponent,
    ],
    providers: [
        ManualExecPlansService,

        ManualExecPlanEditorResolver,
    ],
})
export class ManualModule { }
