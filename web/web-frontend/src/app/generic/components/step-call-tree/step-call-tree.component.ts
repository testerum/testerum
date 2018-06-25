import {Component, DoCheck, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from '@angular/core';
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
import {StepCallTreeService} from "./step-call-tree.service";

@Component({
    selector: 'step-call-tree',
    templateUrl: 'step-call-tree.component.html'
})
export class StepCallTreeComponent implements OnInit, OnChanges, DoCheck {

    @Input() stepCalls: Array<StepCall> = [];

    jsonTreeModel: JsonTreeModel = new JsonTreeModel();

    @ViewChild(ArgModalComponent) argModal: ArgModalComponent;

    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(StepCallContainerModel, StepCallContainerComponent)
        .addPair(SubStepsContainerModel, SubStepsContainerComponent)
        .addPair(ParamsContainerModel, ArgsContainerComponent)
        .addPair(ArgNodeModel, ArgNodeComponent);

    constructor(private stepCallTreeService: StepCallTreeService) {
    }

    ngOnInit(): void {
        this.stepCallTreeService.argModal = this.argModal;
    }

    ngOnChanges(changes: SimpleChanges): void {
        StepCallTreeUtil.mapStepCallsToJsonTreeModel(this.stepCalls, this.jsonTreeModel);
    }

    ngDoCheck(): void {
        if (this.stepCalls.length != this.jsonTreeModel.children.length) {
            let isAddStepAction = this.stepCalls.length > this.jsonTreeModel.children.length;
            StepCallTreeUtil.mapStepCallsToJsonTreeModel(this.stepCalls, this.jsonTreeModel);

            if (isAddStepAction) {
                let lastStepCallNode: StepCallContainerModel = this.jsonTreeModel.children[this.jsonTreeModel.children.length-1] as StepCallContainerModel;
                if (lastStepCallNode.stepCall.args.length != 0) {
                    lastStepCallNode.jsonTreeNodeState.showChildren = true;
                }
            }
        }
    }
}
