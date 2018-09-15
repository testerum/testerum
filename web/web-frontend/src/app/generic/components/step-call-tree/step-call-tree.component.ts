import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
import {StepCall} from "../../../model/step-call.model";
import {StepCallTreeUtil} from "./util/step-call-tree.util";
import {JsonTreeModel} from "../json-tree/model/json-tree.model";
import {ModelComponentMapping} from "../../../model/infrastructure/model-component-mapping.model";
import {StepCallContainerModel} from "./model/step-call-container.model";
import {StepCallContainerComponent} from "./nodes/step-call-container/step-call-container.component";
import {SubStepsContainerComponent} from "./nodes/sub-steps-container/sub-steps-container.component";
import {SubStepsContainerModel} from "./model/sub-steps-container.model";
import {ArgsContainerComponent} from "./nodes/args-container/args-container.component";
import {ParamsContainerModel} from "./model/params-container.model";
import {ArgNodeModel} from "./model/arg-node.model";
import {ArgNodeComponent} from "./nodes/arg-node/arg-node.component";
import {ArgModalComponent} from "./arg-modal/arg-modal.component";
import {StepCallEditorContainerComponent} from "./nodes/step-call-editor-container/step-call-editor-container.component";
import {StepCallEditorContainerModel} from "./model/step-call-editor-container.model";
import {StepCallTreeComponentService} from "./step-call-tree.component-service";

@Component({
    selector: 'step-call-tree',
    templateUrl: 'step-call-tree.component.html',
    providers: [StepCallTreeComponentService]
})
export class StepCallTreeComponent implements OnInit, OnChanges {

    @Input() stepCalls: Array<StepCall> = [];
    @Input() isEditMode: boolean;

    @Output() change = new EventEmitter<void>();

    @ViewChild(ArgModalComponent) argModal: ArgModalComponent;

    jsonTreeModel: JsonTreeModel = new JsonTreeModel();
    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(StepCallContainerModel, StepCallContainerComponent)
        .addPair(StepCallEditorContainerModel, StepCallEditorContainerComponent)
        .addPair(SubStepsContainerModel, SubStepsContainerComponent)
        .addPair(ParamsContainerModel, ArgsContainerComponent)
        .addPair(ArgNodeModel, ArgNodeComponent);

    constructor(public stepCallTreeComponentService: StepCallTreeComponentService) { }

    ngOnInit(): void {
        this.stepCallTreeComponentService.jsonTreeModel = this.jsonTreeModel;
        this.stepCallTreeComponentService.argModal = this.argModal;
        this.stepCallTreeComponentService.isEditMode = this.isEditMode;
        this.stepCallTreeComponentService.changeEventEmitter = this.change;
        this.initTree();
    }

    initTree() {
        StepCallTreeUtil.mapStepCallsToJsonTreeModel(this.stepCalls, this.jsonTreeModel);
        this.stepCallTreeComponentService.stepCalls = this.stepCalls
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (this.stepCalls != this.stepCallTreeComponentService.stepCalls) {
            this.initTree();
        }
    }
}
