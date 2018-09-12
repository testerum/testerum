import { NgModule } from '@angular/core';
import {BrowserModule} from "@angular/platform-browser";
import {AreYouSureModalComponent} from "./components/are_you_sure_modal/are-you-sure-modal.component";
import {PopoverModule} from "ngx-bootstrap";
import {ModalModule } from 'ngx-bootstrap/modal';
import {TreeComponent} from "./components/tree/tree.component";
import {TreeContainerComponent} from "./components/tree/tree-container/tree-container.component";
import {TreeNodeComponent} from "./components/tree/tree-container/tree-node/tree-node.component";
import {StepTextComponent} from "./components/step-text/step-text.component";
import {TreeService} from "./components/tree/tree.service";
import {SplitPanelContainerComponent} from "./components/panels/split-panel-container/split-panel-container.component";
import {StepChooserComponent} from "./components/step-chooser/step-chooser.component";
import {StepChooserService} from "./components/step-chooser/step-chooser.service";
import {DndModule} from "ng2-dnd";
import {StepTextParamComponent} from "./components/step-text/step-text-param/step-text-param.component";
import {FormsModule} from "@angular/forms";
import {ResourcesContainerComponent} from "../functionalities/resources/tree/container/resources-container.component";
import {ResourceNodeComponent} from "../functionalities/resources/tree/container/node/resource-node.component";
import {PortValidatorDirective} from "./validators/port-validator.directive";
import {InfoModalComponent} from "./components/info_modal/info-modal.component";
import {InputErrorComponent} from "./components/form/input_error/input-error.component";
import {TreeNodeCompareModeComponent} from "./components/tree-input/tree-node-compare-mode/tree-node-compare-mode.component";
import {JsonTreeComponent} from "./components/json-tree/json-tree.component";
import {JsonTreeNodeComponent} from "./components/json-tree/tree-node/json-tree-node.component";
import {JsonChildrenComponent} from "./components/json-tree/json-children/json-children.component";
import {JsonContainerNodeComponent} from "./components/json-tree/tree-node/container/json-container-node.component";
import {JsonLeafNodeComponent} from "./components/json-tree/tree-node/leaf/json-leaf-node.component";
import {JsonTreeService} from "./components/json-tree/json-tree.service";
import {JsonTreeContainerEditor} from "./components/json-tree/container-editor/json-tree-container-editor.component";
import {ErrorComponent} from "./error/error.component";
import {StepChooserContainerComponent} from "./components/step-chooser/step-chooser-container/step-chooser-container.component";
import {StepChooserNodeComponent} from "./components/step-chooser/step-chooser-container/step-chooser-node/step-chooser-node.component";
import {FeatureContainerComponent} from "../functionalities/features/features-tree/container/feature-container.component";
import {TestNodeComponent} from "../functionalities/features/features-tree/container/node/test-node.component";
import {CollapsablePanelComponent} from "./components/panels/collapsable-panel/collapsable-panel.component";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {IsNumberValidatorDirective} from "./validators/is-number-validator.directive";
import {FileDirectoryChooserService} from "./components/form/file_dir_chooser/file-directory-chooser.service";
import {FileDirectoryChooserContainerComponent} from "./components/form/file_dir_chooser/container/file-directory-chooser-container.component";
import {FileDirChooserComponent} from "./components/form/file_dir_chooser/file-dir-chooser.component";
import {DirectoryChooserDialogComponent} from "./components/form/file_dir_chooser/dialog/directory-chooser-dialog.component";
import {SafeHtmlPipe} from "./pipes/safe-html.pipe";
import {ChartModule} from "primeng/chart";
import {ExecutionPieComponent} from "./components/charts/execution-pie/execution-pie.component";
import {ExecutionPieService} from "./components/charts/execution-pie/execution-pie.service";
import {StepCallTreeComponent} from "./components/step-call-tree/step-call-tree.component";
import {StepCallContainerComponent} from "./components/step-call-tree/nodes/step-call-container/step-call-container.component";
import {SubStepsContainerComponent} from "./components/step-call-tree/nodes/sub-steps-container/sub-steps-container.component";
import {ArgsContainerComponent} from "./components/step-call-tree/nodes/args-container/args-container.component";
import {ArgNodeComponent} from "./components/step-call-tree/nodes/arg-node/arg-node.component";
import {HttpRequestComponent} from "../functionalities/resources/editors/http/request/http-request.component";
import {ArgNodePanelComponent} from "./components/step-call-tree/nodes/arg-node/arg-node-panel/arg-node-panel.component";
import {ArgModalComponent} from "./components/step-call-tree/arg-modal/arg-modal.component";
import {NewSharedResourcePathModalComponent} from "./components/step-call-tree/new-shared-resource-path-modal/new-shared-resource-path-modal.component";
import {PathChooserComponent} from "./components/path-chooser/path-chooser.component";
import {PathChooserContainerComponent} from "./components/path-chooser/container/path-chooser-container.component";
import {PathChooserNodeComponent} from "./components/path-chooser/container/node/path-chooser-node.component";
import {PathChooserService} from "./components/path-chooser/path-chooser.service";
import {SelectSharedResourceModalComponent} from "./components/step-call-tree/select-shared-resource-modal/select-shared-resource-modal.component";
import {
    AutoCompleteModule,
    FileUploadModule,
    InputTextModule, MessageService,
    SelectButtonModule, ToggleButtonModule,
    ToolbarModule,
    TooltipModule
} from "primeng/primeng";
import {MarkdownEditorComponent} from "./components/markdown-editor/markdown-editor.component";
import {AttachmentsComponent} from "./components/form/attachments/attachments.component";
import {StepCallEditorContainerComponent} from "./components/step-call-tree/nodes/step-call-editor-container/step-call-editor-container.component";
import {StepChooserTreeFilterComponent} from "./components/step-chooser/step-chooser-tree-filter/step-chooser-tree-filter.component";
import {FocusDirective} from "./directives/focus.directive";
import {StepModalComponent} from "./components/step-call-tree/step-modal/step-modal.component";
import {StepModalService} from "./components/step-call-tree/step-modal/step-modal.service";
import {ComposedStepViewComponent} from "./components/step/composed-step-view/composed-step-view.component";
import {ComposedStepParametersComponent} from "./components/step/composed-step-view/composed-step-parameters/composed-step-parameters.component";
import {StepPathModalService} from "./components/step/composed-step-view/step-path-chooser-modal/step-path-modal.service";
import {StepPathModalComponent} from "./components/step/composed-step-view/step-path-chooser-modal/step-path-modal.component";
import {StepPathContainerComponent} from "./components/step/composed-step-view/step-path-chooser-modal/nodes/step-path-container.component";
import {JsonPrimitiveVerifyNodeComponent} from "./components/json-verify/json-verify-tree/node-component/primitive-node/json-primitive-verify-node.component";
import {JsonEmptyVerifyNodeComponent} from "./components/json-verify/json-verify-tree/node-component/emtpy-node/json-empty-verify-node.component";
import {JsonObjectVerifyNodeComponent} from "./components/json-verify/json-verify-tree/node-component/object-node/json-object-verify-node.component";
import {JsonArrayVerifyNodeComponent} from "./components/json-verify/json-verify-tree/node-component/array-node/json-array-verify-node.component";
import {JsonFieldVerifyNodeComponent} from "./components/json-verify/json-verify-tree/node-component/field-node/json-field-verify-node.component";
import {JsonEditorComponent} from "./components/json-verify/editor/json-editor.component";
import {JsonVerifyTreeComponent} from "./components/json-verify/json-verify-tree/json-verify-tree.component";
import {JsonVerifyTreeService} from "./components/json-verify/json-verify-tree/json-verify-tree.service";
import {JsonVerifyEditorComponent} from "./components/json-verify/json-verify-editor/json-verify-editor.component";
import {JsonVerifyComponent} from "./components/json-verify/json-verify.component";
import {AceEditorModule} from "ng2-ace-editor";
import {AreYouSureModalService} from "./components/are_you_sure_modal/are-you-sure-modal.service";

@NgModule({
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        ModalModule.forRoot(),
        DndModule.forRoot(),
        PopoverModule.forRoot(),
        FormsModule,

        AceEditorModule,

        ChartModule,
        FileUploadModule,
        AutoCompleteModule,
        TooltipModule,
        ToolbarModule,
        SelectButtonModule,
        InputTextModule,
        AutoCompleteModule,
        ToggleButtonModule,
        ToolbarModule,
    ],
    exports: [
        SafeHtmlPipe,

        FocusDirective,

        ErrorComponent,

        AreYouSureModalComponent,
        InfoModalComponent,

        TreeComponent,
        TreeContainerComponent,
        TreeNodeComponent,
        TreeNodeCompareModeComponent,

        JsonTreeComponent,
        JsonTreeNodeComponent,
        JsonChildrenComponent,
        JsonContainerNodeComponent,
        JsonLeafNodeComponent,
        JsonTreeContainerEditor,

        StepTextComponent,
        StepTextParamComponent,
        CollapsablePanelComponent,
        InputErrorComponent,

        SplitPanelContainerComponent,
        StepChooserComponent,
        StepChooserContainerComponent,
        StepChooserNodeComponent,

        PortValidatorDirective,
        IsNumberValidatorDirective,

        FileDirChooserComponent,

        ExecutionPieComponent,

        StepCallTreeComponent,

        ComposedStepViewComponent,

        PathChooserComponent,
        MarkdownEditorComponent,
        AttachmentsComponent,

        JsonVerifyComponent,
    ],
    declarations: [
        SafeHtmlPipe,

        FocusDirective,

        ErrorComponent,

        AreYouSureModalComponent,
        InfoModalComponent,

        TreeComponent,
        TreeContainerComponent,
        TreeNodeComponent,
        TreeNodeCompareModeComponent,

        JsonTreeComponent,
        JsonTreeNodeComponent,
        JsonChildrenComponent,
        JsonContainerNodeComponent,
        JsonLeafNodeComponent,
        JsonTreeContainerEditor,

        StepTextComponent,
        StepTextParamComponent,
        CollapsablePanelComponent,
        InputErrorComponent,

        SplitPanelContainerComponent,
        StepChooserTreeFilterComponent,
        StepChooserComponent,
        StepChooserContainerComponent,
        StepChooserNodeComponent,

        PortValidatorDirective,
        IsNumberValidatorDirective,

        FileDirChooserComponent,
        FileDirectoryChooserContainerComponent,
        DirectoryChooserDialogComponent,

        ExecutionPieComponent,

        StepCallTreeComponent,
        StepCallContainerComponent,
        StepCallEditorContainerComponent,
        SubStepsContainerComponent,
        ArgsContainerComponent,
        ArgNodeComponent,
        ArgNodePanelComponent,
        ArgModalComponent,
        NewSharedResourcePathModalComponent,
        SelectSharedResourceModalComponent,
        StepModalComponent,
        StepPathModalComponent,
        StepPathContainerComponent,

        ComposedStepViewComponent,
        ComposedStepParametersComponent,

        PathChooserComponent,
        PathChooserContainerComponent,
        PathChooserNodeComponent,

        MarkdownEditorComponent,
        AttachmentsComponent,

        JsonVerifyEditorComponent,
        JsonVerifyComponent,
        JsonVerifyTreeComponent,
        JsonEditorComponent,
        JsonArrayVerifyNodeComponent,
        JsonObjectVerifyNodeComponent,
        JsonFieldVerifyNodeComponent,
        JsonPrimitiveVerifyNodeComponent,
        JsonEmptyVerifyNodeComponent,
    ],
    providers: [
        TreeService,
        JsonTreeService,
        StepChooserService,
        FileDirectoryChooserService,
        ExecutionPieService,
        PathChooserService,
        StepModalService,
        StepPathModalService,
        MessageService,
        JsonVerifyTreeService,

        AreYouSureModalService,
    ],
    entryComponents: [
        FeatureContainerComponent,
        TestNodeComponent,
        StepChooserContainerComponent,
        StepChooserNodeComponent,
        ResourcesContainerComponent,
        ResourceNodeComponent,
        StepCallContainerComponent,
        StepCallEditorContainerComponent,
        SubStepsContainerComponent,
        ArgsContainerComponent,
        ArgNodeComponent,
        PathChooserContainerComponent,
        PathChooserNodeComponent,

        JsonContainerNodeComponent,
        JsonLeafNodeComponent,
        JsonTreeContainerEditor,
        StepPathContainerComponent,

        StepModalComponent,
        StepChooserComponent,
        StepPathModalComponent,

        // ARG renderer components
        HttpRequestComponent,

        JsonVerifyEditorComponent,

        JsonArrayVerifyNodeComponent,
        JsonObjectVerifyNodeComponent,
        JsonFieldVerifyNodeComponent,
        JsonPrimitiveVerifyNodeComponent,
        JsonEmptyVerifyNodeComponent,

        AreYouSureModalComponent,
    ]
})
export class GenericModule { }
