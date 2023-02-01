import {Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {StepCallContainerModel} from "../../model/step-call-container.model";
import {StepTextComponent} from "../../../step-text/step-text.component";
import {UndefinedStepDef} from "../../../../../model/step/undefined-step-def.model";
import {StepModalService} from "../../step-modal/step-modal.service";
import {ComposedStepDef} from "../../../../../model/step/composed-step-def.model";
import {ArrayUtil} from "../../../../../utils/array.util";
import {Arg} from "../../../../../model/arg/arg.model";
import {ResourceMapEnum} from "../../../../../functionalities/resources/editors/resource-map.enum";
import {StepCallTreeComponentService} from "../../step-call-tree.component-service";
import {StepCall} from "../../../../../model/step/step-call.model";
import {JsonTreeModel} from "../../../json-tree/model/json-tree.model";
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";
import {BasicStepDef} from "../../../../../model/step/basic-step-def.model";
import {SubStepsContainerModel} from "../../model/sub-steps-container.model";
import {StepCallTreeUtil} from "../../util/step-call-tree.util";
import {JsonTreeContainer} from "../../../json-tree/model/json-tree-container.model";
import {ContextService} from "../../../../../service/context.service";
import {StepContext} from "../../../../../model/step/context/step-context.model";
import {PathUtil} from "../../../../../utils/path.util";

@Component({
    selector: 'step-call-container',
    templateUrl: 'step-call-container.component.html',
    styleUrls: [
        'step-call-container.component.scss',
        '../step-call-tree.scss',
        '../../../../../generic/css/tree.scss',
    ]
})
export class StepCallContainerComponent implements OnInit, OnDestroy {

    @Input() model: StepCallContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    @ViewChild(StepTextComponent, { static: true }) stepTextComponent: StepTextComponent<any>;

    hasMouseOver: boolean = false;

    constructor(public stepCallTreeComponentService: StepCallTreeComponentService, //is public with a reason, to access is selected from ComposedStepViewComponent for afterPasteOperation()
                private stepModalService: StepModalService,
                private contextService: ContextService) {
    }

    private stepCallOrderChangeSubscription: any;
    ngOnInit() {

        this.stepCallOrderChangeSubscription = this.stepCallTreeComponentService.stepCallOrderChangeEventEmitter.subscribe(
            event => {
                this.onStepOrderChangedEvent()
            }
        );
        this.initPropertiesThatDependsOnStepOrder();
    }

    ngOnDestroy() {
        if (this.stepCallOrderChangeSubscription) {
            this.stepCallOrderChangeSubscription.unsubscribe();
        }
    }

    private initPropertiesThatDependsOnStepOrder() {
        let indexInParent = this.findStepIndex();
        if (indexInParent <= 0) { // is less then 0 in case of the parent node is a shared node in the tree, first child node is removed for the other branch
            this.stepTextComponent.showPhaseAsAnd = false;
            return;
        }

        let previewsStep: StepCallContainerModel = this.findSiblingStepByIndex(indexInParent-1);
        this.stepTextComponent.showPhaseAsAnd = previewsStep.stepCall.stepDef.phase == this.model.stepCall.stepDef.phase;
    }

    toggleNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren;
        if(this.model.jsonTreeNodeState.showChildren) {
            this.model.children.forEach( (child: JsonTreeContainer) => {
                child.getNodeState().showChildren = true;
            })
        }
    }

    isEditMode(): boolean {
        return this.stepCallTreeComponentService.isEditMode;
    }

    isUndefinedStep(): boolean {
        return this.model.stepCall.stepDef instanceof UndefinedStepDef;
    }

    onStepOrderChangedEvent(): void {
        this.initPropertiesThatDependsOnStepOrder()
    }

    isBasicStep(): boolean {
        return this.model.stepCall.stepDef instanceof BasicStepDef;
    }

    isManualStep(): boolean {
        return this.stepCallTreeComponentService.areManualSteps;
    }

    isManualExecutionMode(): boolean {
        return this.stepCallTreeComponentService.isManualExecutionMode;
    }

    setEditModeToTrue() {
        this.stepCallTreeComponentService.editModeEventEmitter.emit(true);
    }

    editStep() {
        this.setEditModeToTrue();

        let stepToEdit;
        let stepContext: StepContext = new StepContext(this.isManualStep());

        if (this.model.stepCall.stepDef instanceof ComposedStepDef) {
            stepToEdit = this.model.stepCall.stepDef;
        }else
        if (this.isUndefinedStep()) {
            let currentStep: UndefinedStepDef = this.model.stepCall.stepDef;
            stepToEdit = new ComposedStepDef();
            stepToEdit.phase = currentStep.phase;
            stepToEdit.stepPattern = currentStep.stepPattern;
            stepToEdit.path = currentStep.path;
        } else {
            throw new Error("This step is not editable");
        }

        this.stepModalService.showStepModal(
            stepToEdit,
            stepContext
        ).subscribe((newStepDef: ComposedStepDef) => {
            let hasDifferentPath = !newStepDef.path.getParentPath().equals(this.stepCallTreeComponentService.containerPath);
            let hasSubSteps = newStepDef.stepCalls.length > 0;
            let isUndefinedStep = this.isUndefinedStep();
            if (hasDifferentPath || hasSubSteps || !isUndefinedStep) {
                this.model.stepCall.stepDef = newStepDef;
            } else {
                let newUndefinedStep = new UndefinedStepDef();
                newUndefinedStep.phase = newStepDef.phase;
                newUndefinedStep.stepPattern = newStepDef.stepPattern;
                newUndefinedStep.path = PathUtil.generateStepDefPath(this.stepCallTreeComponentService.containerPath, newStepDef.phase, newStepDef.stepPattern.getPatternText());
                this.model.stepCall.stepDef = newUndefinedStep;
            }

            let subStepContainer: SubStepsContainerModel = StepCallTreeUtil.createSubStepsContainerWithChildren(this.model.stepCall.stepDef, new Map());
            if(subStepContainer != null) {
                this.model.removeSubStepsContainerModel();

                subStepContainer.jsonTreeNodeState.showChildren = true;
                subStepContainer.parentContainer = this.model;
                this.model.children.push(
                    subStepContainer
                );
            }

            this.refreshStepCallArgsBasedOnStepDef();
            this.triggerStepOrderChangedEvent();

            this.stepCallTreeComponentService.triggerWarningRecalculationChangesEvent();

        });
    }

    private refreshStepCallArgsBasedOnStepDef() {
        let oldArgs: Arg[] = ArrayUtil.copyArrayOfObjects(this.model.stepCall.args);
        this.model.stepCall.args.length = 0;

        for (let stepParam of this.model.stepCall.stepDef.stepPattern.getParamParts()) {
            let newArg = null;

            let oldArg = this.getArgFromListByParamNameAndType(oldArgs, stepParam.name, stepParam.uiType);
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

        StepCallTreeUtil.createParamContainerWithChildren(this.model.stepCall, this.model);
    }

    private getArgFromListByParamNameAndType(args: Array<Arg>, paramName: string, uiType: string): Arg {
        for (const arg of args) {
            if (arg.paramName == paramName && arg.uiType == uiType) {
                return arg;
            }
        }

        return null;
    }

    isFirstStep(): boolean {
        return this.findStepIndex() == 0;
    }

    isLastStep(): boolean {
        return this.findStepIndex() == this.model.parentContainer.getChildren().length - 1;
    }

    public moveStepUp(): void {
        this.setEditModeToTrue();

        let stepIndex = this.findStepIndex();
        if(stepIndex > 0) {
            let parentContainer = this.model.parentContainer;

            let previewsStep = this.findSiblingStepByIndex(stepIndex-1);
            parentContainer.getChildren()[stepIndex-1] = parentContainer.getChildren()[stepIndex];
            parentContainer.getChildren()[stepIndex] = previewsStep;

            let siblingStepCalls: StepCall[];
            if(parentContainer instanceof JsonTreeModel) {
                siblingStepCalls = this.stepCallTreeComponentService.stepCalls;
            } else if (parentContainer instanceof SubStepsContainerModel) {
                let parentStepCallContainer: StepCallContainerModel = (parentContainer as SubStepsContainerModel).parentContainer as StepCallContainerModel;
                let parentStepDef = parentStepCallContainer.stepCall.stepDef as ComposedStepDef;
                siblingStepCalls = parentStepDef.stepCalls;
            } else {
                let parentStepDef = (parentContainer as StepCallContainerModel).stepCall.stepDef as ComposedStepDef;
                siblingStepCalls = parentStepDef.stepCalls;
            }

            let previewsStepServerModel = siblingStepCalls[stepIndex-1];
            siblingStepCalls[stepIndex-1] = siblingStepCalls[stepIndex];
            siblingStepCalls[stepIndex] = previewsStepServerModel;

            this.triggerStepOrderChangedEvent();
        }
    }

    public moveStepDown(): void {
        this.setEditModeToTrue();

        let stepIndex = this.findStepIndex();
        if(0 <= stepIndex && stepIndex < this.getTotalCountOfSiblings()-1) {
            let parentContainer = this.model.parentContainer;

            let nextStep = this.findSiblingStepByIndex(stepIndex+1);
            parentContainer.getChildren()[stepIndex+1] = parentContainer.getChildren()[stepIndex];
            parentContainer.getChildren()[stepIndex] = nextStep;


            let siblingStepCalls: StepCall[];
            if(parentContainer instanceof JsonTreeModel) {
                siblingStepCalls = this.stepCallTreeComponentService.stepCalls;
            } else if (parentContainer instanceof SubStepsContainerModel) {
                let parentStepCallContainer: StepCallContainerModel = (parentContainer as SubStepsContainerModel).parentContainer as StepCallContainerModel;
                let parentStepDef = parentStepCallContainer.stepCall.stepDef as ComposedStepDef;
                siblingStepCalls = parentStepDef.stepCalls;
            } else {
                let parentStepDef = (parentContainer as StepCallContainerModel).stepCall.stepDef as ComposedStepDef;
                siblingStepCalls = parentStepDef.stepCalls;
            }

            let nextStepServerModel = siblingStepCalls[stepIndex+1];
            siblingStepCalls[stepIndex+1] = siblingStepCalls[stepIndex];
            siblingStepCalls[stepIndex] = nextStepServerModel;

            this.triggerStepOrderChangedEvent();
        }
    }

    public removeStep(): void {
        this.setEditModeToTrue();

        if (this.stepCallOrderChangeSubscription) {
            this.stepCallOrderChangeSubscription.unsubscribe();
        }
        this.stepCallTreeComponentService.removeStepCall(this.model);
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
        this.stepCallTreeComponentService.triggerStepCallOrderChangeEvent();
    }

    hasWarnings(): boolean {
        return this.model.stepCall.getAnyDescendantsHaveWarnings() || this.model.stepCall.getAllWarnings().length > 0;
    }

    hasOwnWarnings(): boolean {
        return this.model.stepCall.getAllWarnings().length > 0;
    }

    onCutStep() {
        this.setEditModeToTrue();

        this.setSelected();
        this.contextService.setPathToCut(this);
    }

    onCopyStep() {
        this.setEditModeToTrue();

        this.setSelected();
        this.contextService.setPathToCopy(this);
    }

    private setSelected() {
        this.stepCallTreeComponentService.setSelectedNode(this);
    }

    isSelectedForCopyOrCut(): boolean {
        return this.stepCallTreeComponentService.getSelectedNode() == this;
    }

    isDisabled(): boolean {
        return !this.model.stepCall.enabled;
    }

    disableOrEnableStep() {
        this.setEditModeToTrue();

        this.model.stepCall.enabled = !this.model.stepCall.enabled;
    }
}
