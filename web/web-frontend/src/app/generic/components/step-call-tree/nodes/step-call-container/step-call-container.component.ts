import {Component, Input, OnDestroy, OnInit, ViewChild, ViewContainerRef} from '@angular/core';
import {StepCallContainerModel} from "../../model/step-call-container.model";
import {StepTextComponent} from "../../../step-text/step-text.component";
import {UndefinedStepDef} from "../../../../../model/undefined-step-def.model";
import {StepModalService} from "../../step-modal/step-modal.service";
import {ComposedStepDef} from "../../../../../model/composed-step-def.model";
import {ArrayUtil} from "../../../../../utils/array.util";
import {Arg} from "../../../../../model/arg/arg.model";
import {ResourceMapEnum} from "../../../../../functionalities/resources/editors/resource-map.enum";
import {StepCallTreeState} from "../../step-call-tree.state";

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
    @Input() treeState: StepCallTreeState;

    @ViewChild(StepTextComponent) stepTextComponent: StepTextComponent<any>;

    hasMouseOver: boolean = false;
    showParameters: boolean = true;

    constructor(private stepModalService: StepModalService) {
    }

    private editModeSubscription: any;
    private stepCallOrderChangeSubscription: any;
    ngOnInit() {
        if(!this.isEditMode()) this.showParameters = false;
        this.editModeSubscription = this.treeState.editModeEventEmitter.subscribe(
            (editMode: boolean) => {
                this.showParameters = editMode;
            }
        );

        this.stepCallOrderChangeSubscription = this.treeState.stepCallOrderChangeEventEmitter.subscribe(
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
        return this.treeState.isEditMode;
    }

    isUndefinedStep(): boolean {
        return this.model.stepCall.stepDef instanceof UndefinedStepDef;
    }

    onStepOrderChangedEvent(): void {
        this.initPropertiesThatDependsOnStepOrder()
    }

    editStep() {
        let stepToEdit;
        if (this.model.stepCall.stepDef instanceof ComposedStepDef) {
            stepToEdit = this.model.stepCall.stepDef;
        }else
        if (this.isUndefinedStep()) {
            let currentStep: UndefinedStepDef = this.model.stepCall.stepDef;
            stepToEdit = new ComposedStepDef();
            stepToEdit.phase = currentStep.phase;
            stepToEdit.stepPattern = currentStep.stepPattern;
        } else {
            throw new Error("This step is not editable");
        }

        this.stepModalService.showStepModal(
            stepToEdit
        ).subscribe((newStepDef: ComposedStepDef) => {
            this.model.stepCall.stepDef = newStepDef;
            this.refreshStepCallArgsBasedOnStepDef();

            this.treeState.triggerWarningRecalculationChangesEvent();
        });
    }

    private refreshStepCallArgsBasedOnStepDef() {
        let oldArgs: Arg[] = ArrayUtil.copyArrayOfObjects(this.model.stepCall.args);
        this.model.stepCall.args.length = 0;

        for (let stepParam of this.model.stepCall.stepDef.stepPattern.getParamParts()) {
            let newArg = null;

            let oldArg = this.getArgFromListByNameAndType(oldArgs, stepParam.name, stepParam.uiType);
            if (oldArg) {
                newArg = oldArg;
            } else {
                newArg = new Arg();
                newArg.name = stepParam.name;
                newArg.serverType = stepParam.serverType;
                newArg.uiType = stepParam.uiType;
                newArg.content = ResourceMapEnum.getResourceMapEnumByUiType(stepParam.uiType).getNewInstance();

            }

            this.model.stepCall.args.push(newArg);
        }
    }

    private getArgFromListByNameAndType(args: Array<Arg>, name: string, uiType: string): Arg {
        for (const arg of args) {
            if (arg.name == name && arg.uiType == uiType) {
                return arg;
            }
        }

        return null;
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
        this.treeState.removeStepCall(this.model);
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
        this.treeState.triggerStepCallOrderChangeEvent();
    }

    hasWarnings(): boolean {
        return this.model.stepCall.getAnyDescendantsHaveWarnings() || this.model.stepCall.getAllWarnings().length > 0;
    }

    hasOwnWarnings(): boolean {
        return this.model.stepCall.getAllWarnings().length > 0;
    }
}
