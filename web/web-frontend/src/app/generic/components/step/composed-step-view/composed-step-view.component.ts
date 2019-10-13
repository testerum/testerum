import {
    AfterContentChecked,
    Component,
    DoCheck,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    ViewChild,
    ViewEncapsulation
} from '@angular/core';
import {ComposedStepDef} from "../../../../model/composed-step-def.model";
import {NgForm} from "@angular/forms";
import {StepPhaseEnum} from "../../../../model/enums/step-phase.enum";
import {AutoComplete, Message} from "primeng/primeng";
import {Arg} from "../../../../model/arg/arg.model";
import {ArrayUtil} from "../../../../utils/array.util";
import {StepDef} from "../../../../model/step-def.model";
import {StepCall} from "../../../../model/step-call.model";
import {StepTreeNodeModel} from "../../../../functionalities/steps/steps-tree/model/step-tree-node.model";
import {StepTreeContainerModel} from "../../../../functionalities/steps/steps-tree/model/step-tree-container.model";
import {TagsService} from "../../../../service/tags.service";
import {ResourceMapEnum} from "../../../../functionalities/resources/editors/resource-map.enum";
import {StepCallTreeComponent} from "../../step-call-tree/step-call-tree.component";
import {StepPathModalService} from "./step-path-chooser-modal/step-path-modal.service";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {Subscription} from "rxjs";
import {StepsService} from "../../../../service/steps.service";
import {MarkdownEditorComponent} from "../../markdown-editor/markdown-editor.component";
import {StepCallWarningUtil} from "../../step-call-tree/util/step-call-warning.util";
import {ContextService} from "../../../../service/context.service";
import {StepContext} from "../../../../model/step/context/step-context.model";
import {ParamNameValidatorDirective} from "../../../validators/param-name-validator.directive";

@Component({
    selector: 'composed-step-view',
    templateUrl: 'composed-step-view.component.html',
    styleUrls: ['composed-step-view.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class ComposedStepViewComponent implements OnInit, OnDestroy, AfterContentChecked, DoCheck {

    @Input() model: ComposedStepDef;
    @Input() isCreateAction: boolean = false;
    @Input() stepContext: StepContext = new StepContext();
    @Input() isEditMode: boolean;


    @ViewChild(NgForm, { static: true }) form: NgForm;
    StepPhaseEnum = StepPhaseEnum;

    oldModel: ComposedStepDef;
    pattern: string;
    allowPathEdit = false;

    errorsKey: string[] = [];
    invalidParameterName: string;

    warnings: Message[] = [];
    areChildComponentsValid: boolean = true;

    @ViewChild("tagsElement", { static: false }) tagsAutoComplete: AutoComplete;
    allKnownTags: Array<string> = [];
    tagsToShow:string[] = [];
    currentTagSearch:string;

    @ViewChild(StepCallTreeComponent, { static: true }) stepCallTreeComponent: StepCallTreeComponent;
    readonly editModeEventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    @ViewChild("descriptionMarkdownEditor", { static: true }) descriptionMarkdownEditor: MarkdownEditorComponent;

    private editModeEventEmitterSubscription: Subscription;
    private warningRecalculationChangesSubscription: Subscription;

    constructor(private stepPathModalService: StepPathModalService,
                private tagsService: TagsService,
                private stepsService: StepsService,
                private contextService: ContextService) {
    }

    ngOnInit(): void {
        if (this.stepContext.isPartOfManualTest) {
            if (this.model.path) {
                this.allowPathEdit = true;
            }
        } else {
            this.allowPathEdit = true;
        }

        if (!this.isCreateAction) {
            this.recalculateWarnings();
        }

        if (this.isEditMode) {
            this.loadAllTags();
        }
        this.descriptionMarkdownEditor.changeEventEmitter.subscribe((description: string) => {
            this.model.description = description;
            this.validate();
        });
        this.refreshDescription();

        this.editModeEventEmitterSubscription = this.editModeEventEmitter.subscribe( (editMode: boolean) => {
            this.isEditMode = editMode;
            this.stepCallTreeComponent.stepCallTreeComponentService.setEditMode(this.isEditMode);

            this.refreshDescription();
        });

        this.warningRecalculationChangesSubscription = this.stepCallTreeComponent.stepCallTreeComponentService.warningRecalculationChangesEventEmitter.subscribe(refreshWarningsEvent => {
            this.recalculateWarnings();
        })
    }

    private recalculateWarnings() {
        if (this.stepContext.isPartOfManualTest) {
            return;
        }

        let model = this.getModelForWarningRecalculation();

        this.stepsService.getWarnings(model).subscribe((newModel: ComposedStepDef) => {
            StepCallWarningUtil.copyWarningState(this.model.stepCalls, newModel.stepCalls);

            ArrayUtil.replaceElementsInArray(this.model.warnings, newModel.warnings);
            this.refreshWarnings();
        });
    }

    private getModelForWarningRecalculation() {
        if (this.model.path && this.model.phase) {
            return this.model;
        }

        let model: ComposedStepDef = this.model.clone();
        if (!this.model.path) {
            model.path = Path.createInstance("/");
        }

        if (!this.model.phase) {
            model.phase = StepPhaseEnum.GIVEN;
        }

        return model;
    }

    setEditMode(editMode: boolean) {
        this.editModeEventEmitter.emit(editMode);
    }

    ngAfterContentChecked(): void {
        this.pattern = this.model.stepPattern.getPatternText();
    }

    ngOnDestroy(): void {
        if(this.editModeEventEmitterSubscription) this.editModeEventEmitterSubscription.unsubscribe();
        if(this.warningRecalculationChangesSubscription) this.warningRecalculationChangesSubscription.unsubscribe();
    }

    ngDoCheck(): void {
        if (this.oldModel != this.model) {
            this.refreshWarnings();
            this.oldModel = this.model;
            this.validate();

            this.refreshDescription();
        }
    }

    refreshDescription() {
        if (this.descriptionMarkdownEditor) {
            this.descriptionMarkdownEditor.setEditMode(this.isEditMode);
            this.descriptionMarkdownEditor.setValue(this.model.description);
            this.validate();
        }
    }

    private refreshWarnings() {
        this.warnings = [];
        if (this.stepContext.isPartOfManualTest) {
            return
        }
        for (const warning of this.model.warnings) {
            this.warnings.push(
                {severity: 'error', summary: warning.message}
            );
        }
    }

    onPatternChanged() {
        this.model.stepPattern.setPatternText(this.pattern);

        this.errorsKey = this.getStepCallErrors(this.model);
        if (this.errorsKey.length > 0) {
            return;
        }
    }

    private getStepCallErrors(stepDef: StepDef): string[] {
        this.invalidParameterName = null;

        let paramParts = stepDef.stepPattern.getParamParts();
        for (const paramPart of paramParts) {
            if (!ParamNameValidatorDirective.isValidParamName(paramPart.name)) {
                this.invalidParameterName = paramPart.name;
                return ["invalidParamName"]
            }
        }
        return [];
    }

    addStep() {
        this.stepCallTreeComponent.stepCallTreeComponentService.addStepCallEditor(this.stepCallTreeComponent.jsonTreeModel);
    }

    onStepChose(choseStep: StepDef): void {
        let stepCall = new StepCall();
        stepCall.stepDef = choseStep;
        for (let stepParam of choseStep.stepPattern.getParamParts()) {
            let valueArg = new Arg();
            valueArg.name = stepParam.name;
            valueArg.serverType = stepParam.serverType;
            valueArg.uiType = stepParam.uiType;
            valueArg.content = ResourceMapEnum.getResourceMapEnumByUiType(stepParam.uiType).getNewInstance();

            stepCall.args.push(valueArg);
        }

        this.stepCallTreeComponent.stepCallTreeComponentService.addStepCall(stepCall);
    }

    enableEditTestMode(): void {
        this.loadAllTags();
        this.setEditMode(true);
    }

    private loadAllTags() {
        this.tagsService.getTags().subscribe(tags => {
            ArrayUtil.replaceElementsInArray(this.allKnownTags, tags);
        });
    }

    onSearchTag(event) {
        this.currentTagSearch = event.query;

        let newTagsToShow = ArrayUtil.filterArray(
            this.allKnownTags,
            event.query
        );
        for (let currentTag of this.model.tags) {
            ArrayUtil.removeElementFromArray(newTagsToShow, currentTag)
        }
        this.tagsToShow = newTagsToShow
    }

    onTagsKeyUp(event: KeyboardEvent) {
        event.preventDefault();

        if (event.key == "Enter") {
            this.addCurrentTagToTags();
        }
    }

    addCurrentTagToTags() {
        if (this.currentTagSearch) {
            this.model.tags.push(this.currentTagSearch);
            this.currentTagSearch = null;
            this.tagsAutoComplete.multiInputEL.nativeElement.value = null;
        }
    }

    onTagSelect(event) {
        this.currentTagSearch = null;

        this.validate();
    }

    getPhaseEnumValues(): Array<StepPhaseEnum> {
        return [StepPhaseEnum.GIVEN, StepPhaseEnum.WHEN, StepPhaseEnum.THEN]
    }

    allowDrop(): any {
        return (dragData: StepTreeNodeModel) => {
            let isTheSameStep = dragData.path.equals(this.model.path);
            let isStepContainer = dragData instanceof StepTreeContainerModel;
            return this.isEditMode && !isStepContainer && !isTheSameStep;
        }
    }

    onTreeNodeDrop($event: any) {
        let stepToCopyTreeNode: StepTreeNodeModel = $event.dragData;
        this.onStepChose(stepToCopyTreeNode.stepDef)
    }

    onSelectStepPath() {
        this.stepPathModalService.showModal().subscribe((selectedPath: Path)=> {
            this.model.path = selectedPath;
        })
    }

    onStepCallTreeChange() {
        this.validate();
    }

    validate() {
        this.isValid();
    }

    isValid(): boolean {
        let control = this.form.control;

        //reset state
        if (control.get("pathInput")) {
            control.get("pathInput").setErrors(null);
        }
        if (this.stepContext.isPartOfManualTest && this.model.path == null) {
            this.allowPathEdit = false;
        }

        //validate
        if (this.model.stepCalls.length > 0) {
            if(!this.model.path) {
                this.addPathRequiredValidationError(control);
                return false;
            }
        }

        if (this.model.description && !this.model.path) {
            this.addPathRequiredValidationError(control);
            return false;
        }

        if (this.model.tags.length > 0 && !this.model.path) {
            this.addPathRequiredValidationError(control);
            return false;
        }

        return true;
    }

    private addPathRequiredValidationError(control) {
        let validationError = {};
        validationError["required"] = true;
        control.markAsTouched();
        control.markAsDirty();

        control.get("pathInput").setErrors(validationError);

        this.allowPathEdit = true;
    }

    onBeforeSave() {
        this.setDescription();
    }

    private setDescription() {
        this.model.description = this.descriptionMarkdownEditor.getValue()
    }

    canPasteStep(): boolean {
        return this.contextService.stepToCut != null || this.contextService.stepToCopy != null;
    }

    onPasteStep() {
        let stepCallTreeComponentService = this.stepCallTreeComponent.stepCallTreeComponentService;

        let treeModel = this.stepCallTreeComponent.jsonTreeModel;
        if (this.contextService.stepToCopy) {
            let stepToCopyModel = this.contextService.stepToCopy.model;

            let newStepCall = stepToCopyModel.stepCall.clone();
            newStepCall.stepDef = stepToCopyModel.stepCall.stepDef; //do not clone the StepDef, we still want to point to the same def

            stepCallTreeComponentService.addStepCallToParentContainer(newStepCall, treeModel);
            this.afterPasteOperation();
        }
        if (this.contextService.stepToCut) {
            let stepToCutModel = this.contextService.stepToCut.model;

            this.contextService.stepToCut.moveStep(treeModel);
            this.afterPasteOperation();
        }
    }

    private afterPasteOperation() {
        let stepForOperation = this.contextService.stepToCopy? this.contextService.stepToCopy: this.contextService.stepToCut;

        if (stepForOperation) {
            stepForOperation.stepCallTreeComponentService.setSelectedNode(null);
        }

        this.contextService.stepToCut = null;
        this.contextService.stepToCopy = null;
    }
}
