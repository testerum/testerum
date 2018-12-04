import {Component, Input, OnDestroy, OnInit, ViewChild, ViewContainerRef} from '@angular/core';
import {StepCallContainerModel} from "../../model/step-call-container.model";
import {StepTextComponent} from "../../../step-text/step-text.component";
import {UndefinedStepDef} from "../../../../../model/undefined-step-def.model";
import {StepModalService} from "../../step-modal/step-modal.service";
import {ComposedStepDef} from "../../../../../model/composed-step-def.model";
import {ArrayUtil} from "../../../../../utils/array.util";
import {Arg} from "../../../../../model/arg/arg.model";
import {ResourceMapEnum} from "../../../../../functionalities/resources/editors/resource-map.enum";
import {StepCallTreeComponentService} from "../../step-call-tree.component-service";
import {StepCall} from "../../../../../model/step-call.model";
import {JsonTreeModel} from "../../../json-tree/model/json-tree.model";
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";
import {BasicStepDef} from "../../../../../model/basic-step-def.model";
import {SubStepsContainerModel} from "../../model/sub-steps-container.model";
import {StepCallTreeUtil} from "../../util/step-call-tree.util";
import {JsonTreeContainer} from "../../../json-tree/model/json-tree-container.model";
import {JsonTreeContainerEditorEvent} from "../../../json-tree/container-editor/model/json-tree-container-editor.event";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {ContextService} from "../../../../../service/context.service";

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

    @ViewChild(StepTextComponent) stepTextComponent: StepTextComponent<any>;

    hasMouseOver: boolean = false;
    showParameters: boolean = true;

    constructor(private stepCallTreeComponentService: StepCallTreeComponentService,
                private stepModalService: StepModalService,
                private contextService: ContextService) {
    }

    private editModeSubscription: any;
    private stepCallOrderChangeSubscription: any;
    ngOnInit() {
        if(!this.isEditMode()) this.showParameters = false;
        this.editModeSubscription = this.stepCallTreeComponentService.editModeEventEmitter.subscribe(
            (editMode: boolean) => {
                this.showParameters = editMode;
            }
        );

        this.stepCallOrderChangeSubscription = this.stepCallTreeComponentService.stepCallOrderChangeEventEmitter.subscribe(
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
            if (newStepDef.path) {
                this.model.stepCall.stepDef = newStepDef;
            } else {
                let newUndefinedStep = new UndefinedStepDef();
                newUndefinedStep.phase = newStepDef.phase;
                newUndefinedStep.stepPattern = newStepDef.stepPattern;
                this.model.stepCall.stepDef = newUndefinedStep;
            }

            let subStepContainer = StepCallTreeUtil.createSubStepsContainerWithChildren(newStepDef);
            if(subStepContainer != null) {
                subStepContainer.parentContainer = this.model;
                this.model.children.push(
                    subStepContainer
                );
            }

            this.refreshStepCallArgsBasedOnStepDef();

            this.stepCallTreeComponentService.triggerWarningRecalculationChangesEvent();

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

    isFirstStep(): boolean {
        return this.findStepIndex() == 0;
    }

    isLastStep(): boolean {
        return this.findStepIndex() == this.model.parentContainer.getChildren().length - 1;
    }

    public moveStepUp(): void {
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
        let stepIndex = this.findStepIndex();
        if(0 <= stepIndex && stepIndex < this.getTotalCountOfSiblings()-1) {
            let parentContainer = this.model.parentContainer;

            let nextStep = this.findSiblingStepByIndex(stepIndex+1);
            parentContainer.getChildren()[stepIndex+1] = parentContainer.getChildren()[stepIndex];
            parentContainer.getChildren()[stepIndex] = nextStep;


            let siblingStepCalls: StepCall[];
            if(parentContainer instanceof JsonTreeModel) {
                siblingStepCalls = this.stepCallTreeComponentService.stepCalls;
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
        this.setSelected();
        this.contextService.setPathToCut(this.model.path);
    }

    onCopyStep() {
        this.setSelected();
        this.contextService.setPathToCopy(this.model.path);
    }

    onPasteStep() {
        let destinationPath = this.model.path;
        if (this.contextService.stepPathToCopy) {
            let sourcePath = this.contextService.stepPathToCopy;
            this.jsonTreeService.triggerCopyAction(sourcePath, destinationPath).subscribe((copyEvent: JsonTreeContainerEditorEvent) => {
                    this.featureService.copy(sourcePath, destinationPath).subscribe( (resultPath: Path) => {
                        this.afterPasteOperation(resultPath);
                    });
                }
            )
        }
        if (this.contextService.stepPathToCut) {
            let sourcePath = this.contextService.stepPathToCut;
            this.jsonTreeService.triggerMoveAction(sourcePath, destinationPath).subscribe((copyEvent: JsonTreeContainerEditorEvent) => {
                    this.featureService.move(sourcePath, destinationPath).subscribe( (resultPath: Path) => {
                        this.afterPasteOperation(resultPath);
                    });
                }
            )
        }
    }

    private afterPasteOperation(resultPath: Path) {
        this.contextService.stepPathToCut = null;
        this.contextService.stepPathToCopy = null;

        this.contextService.initializeTestsTreeFromServer(resultPath);
        if (resultPath.isFile()) {
            this.urlService.navigateToTest(resultPath);
        } else {
            this.urlService.navigateToFeature(resultPath);
        }
    }

    canPaste(): boolean {
        return this.contextService.canPaste(this.model.path);
    }

    private setSelected() {
        this.model.selected = true;
    }
}
