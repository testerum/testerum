import {Component, Input} from '@angular/core';
import {SubStepsContainerModel} from "../../model/sub-steps-container.model";
import {StepCallTreeComponentService} from "../../step-call-tree.component-service";
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";
import {StepCallContainerModel} from "../../model/step-call-container.model";
import {ContextService} from "../../../../../service/context.service";
import {InfoModalService} from "../../../info_modal/info-modal.service";
import {JsonTreeContainer} from "../../../json-tree/model/json-tree-container.model";
import {StepCallContainerComponent} from "../step-call-container/step-call-container.component";

@Component({
    selector: 'sub-steps-container',
    templateUrl: 'sub-steps-container.component.html',
    styleUrls: [
        'sub-steps-container.component.scss',
        '../step-call-tree.scss',
        '../../../../../generic/css/tree.scss',
    ]
})
export class SubStepsContainerComponent {
    @Input() model: SubStepsContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    hasMouseOver: boolean = false;
    showChildren: boolean = true;

    constructor(private stepCallTreeComponentService: StepCallTreeComponentService,
                private contextService: ContextService,
                private infoModalService: InfoModalService) {
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    isEditMode(): boolean {
        return this.stepCallTreeComponentService.isEditMode;
    }

    addSubStep() {
        this.stepCallTreeComponentService.addStepCallEditor(this.model);
        this.showChildren = true;
        this.model.jsonTreeNodeState.showChildren = true;
    }

    onPasteStep() {
        let parentContainer = this.model.parentContainer as StepCallContainerModel;

        if (this.isPasteOperationCreatingACycle(this.model)) {
            this.infoModalService.showInfoModal(
                "Operation not allowed",
                "You are not allowed to paste a step as its own child/descendant.\n" +
                "This operation would create a cycle and the world as we know it might end!"
            );

            return;
        }

        if (this.contextService.stepToCopy) {
            let stepCallContainerComponent = this.contextService.stepToCopy;
            let parentSubStepsContainerModel = parentContainer.getSubStepsContainerModel();

            this.stepCallTreeComponentService.addStepCallToParentContainer(stepCallContainerComponent.model.stepCall, parentSubStepsContainerModel);
            parentSubStepsContainerModel.jsonTreeNodeState.showChildren = true;
            this.afterPasteOperation();
        }
        if (this.contextService.stepToCut) {
            let stepCallContainerComponent = this.contextService.stepToCut;
            let parentSubStepsContainerModel = parentContainer.getSubStepsContainerModel();

            stepCallContainerComponent.moveStep(parentSubStepsContainerModel);

            parentSubStepsContainerModel.jsonTreeNodeState.showChildren = true;
            this.afterPasteOperation();
        }
    }

    private afterPasteOperation() {
        this.contextService.stepToCut = null;
        this.contextService.stepToCopy = null;
        this.stepCallTreeComponentService.setSelectedNode(null);
    }

    canPaste(): boolean {
        let destinationContainer = this.model.parentContainer as StepCallContainerModel;
        return this.contextService.canPaste(destinationContainer);
    }

    private isPasteOperationCreatingACycle(parent: JsonTreeContainer): boolean {
        let stepToCopyOrCut: StepCallContainerComponent = this.contextService.stepToCopy ? this.contextService.stepToCopy: this.contextService.stepToCut;
        if(!stepToCopyOrCut) {
            return false;
        }

        while (parent) {
            if(parent == stepToCopyOrCut.model) {
                return true
            }
            parent = parent.parentContainer;
        }
        return false;
    }
}
