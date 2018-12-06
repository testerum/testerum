import {Component, Input} from '@angular/core';
import {SubStepsContainerModel} from "../../model/sub-steps-container.model";
import {StepCallTreeComponentService} from "../../step-call-tree.component-service";
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";
import {StepCallContainerModel} from "../../model/step-call-container.model";
import {ContextService} from "../../../../../service/context.service";
import {AreYouSureModalEnum} from "../../../are_you_sure_modal/are-you-sure-modal.enum";
import {AreYouSureModalService} from "../../../are_you_sure_modal/are-you-sure-modal.service";

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
                private areYouSureModalService: AreYouSureModalService) {
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
        let destinationStepCall = parentContainer.stepCall;
        if (this.contextService.stepToCopy) {
            let stepCallContainerComponent = this.contextService.stepToCopy;
            this.areYouSureModalService.showAreYouSureModal(
                "Copy Step",
                "Are you sure you want to copy step\n" +
                "<b><span>"+stepCallContainerComponent.model.stepCall.getTextWithParamValues(null, true)+"</span></b>\n" +
                "as a child of step\n" +
                "<b><span>"+destinationStepCall.getTextWithParamValues(null, true) +"</span></b>"
            ).subscribe((action: AreYouSureModalEnum) => {
                if (action == AreYouSureModalEnum.OK) {
                    let parentSubStepsContainerModel = parentContainer.getSubStepsContainerModel();
                    this.stepCallTreeComponentService.addStepCallToParentContainer(stepCallContainerComponent.model.stepCall, parentSubStepsContainerModel);
                    parentSubStepsContainerModel.jsonTreeNodeState.showChildren = true;
                    this.afterPasteOperation();
                }
            })
        }
        if (this.contextService.stepToCut) {
            let stepCallContainerComponent = this.contextService.stepToCut;
            this.areYouSureModalService.showAreYouSureModal(
                "Move Step",
                "Are you sure you want to move step\n" +
                "<b><span>"+stepCallContainerComponent.model.stepCall.getTextWithParamValues(null, true)+"</span></b>\n" +
                "as a child of step\n" +
                "<b><span>"+destinationStepCall.getTextWithParamValues(null, true)+"</span></b>"
            ).subscribe((action: AreYouSureModalEnum) => {
                if (action == AreYouSureModalEnum.OK) {
                    let parentSubStepsContainerModel = parentContainer.getSubStepsContainerModel();
                    stepCallContainerComponent.moveStep(parentSubStepsContainerModel);

                    parentSubStepsContainerModel.jsonTreeNodeState.showChildren = true;
                    this.afterPasteOperation();
                }
            })
        }
    }

    private afterPasteOperation() {
        this.contextService.stepToCut = null;
        this.contextService.stepToCopy = null;
    }

    canPaste(): boolean {
        let destinationContainer = this.model.parentContainer as StepCallContainerModel;
        return this.contextService.canPaste(destinationContainer);
    }
}
