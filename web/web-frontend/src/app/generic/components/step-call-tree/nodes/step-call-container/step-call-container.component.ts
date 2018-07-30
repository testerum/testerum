import {Component, Input, OnDestroy, OnInit, ViewChild, ViewContainerRef} from '@angular/core';
import {StepCallContainerModel} from "../../model/step-call-container.model";
import {StepCallTreeService} from "../../step-call-tree.service";
import {StepTextComponent} from "../../../step-text/step-text.component";
import {UndefinedStepDef} from "../../../../../model/undefined-step-def.model";
import {StepModalService} from "../../step-modal/step-modal.service";
import {ComposedStepDef} from "../../../../../model/composed-step-def.model";

@Component({
    selector: 'step-call-container',
    templateUrl: 'step-call-container.component.html',
    styleUrls: [
        'step-call-container.component.scss',
        '../step-call-tree.scss',
        '../../../json-tree/json-tree.generic.scss',
        '../../../../../generic/css/tree.scss',
    ]
})
export class StepCallContainerComponent implements OnInit, OnDestroy {

    @Input() model: StepCallContainerModel;

    @ViewChild(StepTextComponent) stepTextComponent: StepTextComponent<any>;

    hasMouseOver: boolean = false;
    showParameters: boolean = true;

    constructor(private stepCallTreeService: StepCallTreeService,
                private stepModalService: StepModalService,
                private viewContainerRef: ViewContainerRef) {
    }

    private editModeSubscription: any;
    private stepCallOrderChangeSubscription: any;
    ngOnInit() {
        if(!this.isEditMode()) this.showParameters = false;
        this.editModeSubscription = this.stepCallTreeService.editModeEventEmitter.subscribe(
            (editMode: boolean) => {
                this.showParameters = editMode;
            }
        );

        this.stepCallOrderChangeSubscription = this.stepCallTreeService.stepCallOrderChangeEventEmitter.subscribe(
            event => {
                this.onStepOrderChangedEvent()
            }
        );
        this.initPropertiesThatDependsOnStepOrder();
    }

    ngOnDestroy() {
        if (this.editModeSubscription) {
            this.editModeSubscription.unsubscribe();
        }
        if (this.stepCallOrderChangeSubscription) {
            this.stepCallOrderChangeSubscription.unsubscribe();
        }
    }

    private initPropertiesThatDependsOnStepOrder() {
        let indexInParent = this.findStepIndex();
        if (indexInParent == 0) {
            this.stepTextComponent.showPhaseAsAnd = false;
            return;
        }

        let previewsStep: StepCallContainerModel = this.findSiblingStepByIndex(indexInParent-1);
        this.stepTextComponent.showPhaseAsAnd = previewsStep.stepCall.stepDef.phase == this.model.stepCall.stepDef.phase;
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    isEditMode(): boolean {
        return this.stepCallTreeService.isEditMode;
    }

    isUndefinedStep(): boolean {
        return this.model.stepCall.stepDef instanceof UndefinedStepDef;
    }

    onStepOrderChangedEvent(): void {
        this.initPropertiesThatDependsOnStepOrder()
    }

    editStep() {
        this.stepModalService.showStepModal(
            this.model.stepCall.stepDef as ComposedStepDef,
            this.viewContainerRef
        );
    }

    public moveStepUp(): void {
        let stepIndex = this.findStepIndex();
        if(stepIndex > 0) {
            let previewsStep = this.findSiblingStepByIndex(stepIndex-1);
            this.model.parentContainer.getChildren()[stepIndex-1] = this.model.parentContainer.getChildren()[stepIndex];
            this.model.parentContainer.getChildren()[stepIndex] = previewsStep;

            this.triggerStepOrderChangedEvent();
        }
    }

    public moveStepDown(): void {
        let stepIndex = this.findStepIndex();
        if(0 <= stepIndex && stepIndex < this.getTotalCountOfSiblings()-1) {
            let nextStep = this.findSiblingStepByIndex(stepIndex+1);
            this.model.parentContainer.getChildren()[stepIndex+1] = this.model.parentContainer.getChildren()[stepIndex];
            this.model.parentContainer.getChildren()[stepIndex] = nextStep;
            this.triggerStepOrderChangedEvent();
        }
    }

    public removeStep(): void {
        if (this.stepCallOrderChangeSubscription) {
            this.stepCallOrderChangeSubscription.unsubscribe();
        }
        this.stepCallTreeService.removeStepCall(this.model);
    }

    private findStepIndex(): number {
        return this.model.parentContainer.getChildren().indexOf(this.model);
    }

    private getTotalCountOfSiblings(): number {
        return this.model.parentContainer.getChildren().length;
    }

    private findSiblingStepByIndex(indexInParent: number): StepCallContainerModel {
        if (indexInParent >= this.getTotalCountOfSiblings() ||
            indexInParent < 0) {
            return null
        }
        return this.model.parentContainer.getChildren()[indexInParent] as StepCallContainerModel;
    }

    public triggerStepOrderChangedEvent():void {
        this.stepCallTreeService.triggerStepCallOrderChangeEvent();
    }

    hasWarnings(): boolean {
        return this.model.stepCall.getAnyDescendantsHaveWarnings() || this.model.stepCall.getAllWarnings().length > 0;
    }

    hasOwnWarnings(): boolean {
        return this.model.stepCall.getAllWarnings().length > 0;
    }
}
