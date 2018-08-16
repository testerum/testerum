import {
    AfterContentChecked,
    Component, DoCheck,
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
import {StepChooserService} from "../../step-chooser/step-chooser.service";
import {StepCallTreeComponent} from "../../step-call-tree/step-call-tree.component";
import {StepPathModalService} from "./step-path-chooser-modal/step-path-modal.service";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {Subscription} from "rxjs";
import {TestModel} from "../../../../model/test/test.model";
import {StepsService} from "../../../../service/steps.service";

@Component({
    selector: 'composed-step-view',
    templateUrl: 'composed-step-view.component.html',
    styleUrls: ['composed-step-view.component.scss'],
    encapsulation: ViewEncapsulation.None
})

export class ComposedStepViewComponent implements OnInit, OnDestroy, AfterContentChecked, DoCheck {

    @Input() model: ComposedStepDef;
    @Input() isEditMode: boolean;
    @Input() isCreateAction: boolean = false;

    @ViewChild(NgForm) form: NgForm;
    StepPhaseEnum = StepPhaseEnum;

    oldModel: ComposedStepDef;
    pattern: string;
    hasPathDefined = true;

    warnings: Message[] = [];
    areChildComponentsValid: boolean = true;

    @ViewChild("tagsElement") tagsAutoComplete: AutoComplete;
    allKnownTags: Array<string> = [];
    tagsToShow:string[] = [];
    currentTagSearch:string;

    @ViewChild(StepCallTreeComponent) stepCallTreeComponent: StepCallTreeComponent;
    editModeEventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    private editModeStepCallTreeSubscription: Subscription;
    private warningRecalculationChangesSubscription: Subscription;

    constructor(private stepPathModalService: StepPathModalService,
                private tagsService: TagsService,
                private stepsService: StepsService) {
    }

    ngOnInit(): void {
        // this.oldModel = this.model;

        if (this.model.path == null) {
            this.hasPathDefined = false
        }

        if (this.isEditMode) {
            this.loadAllTags();
        }

        this.editModeStepCallTreeSubscription = this.stepCallTreeComponent.stepCallTreeComponentService.editModeEventEmitter.subscribe( (editMode: boolean) => {
                this.isEditMode = editMode;
                this.editModeEventEmitter.emit(this.isEditMode);
            }
        );

        this.warningRecalculationChangesSubscription = this.stepCallTreeComponent.stepCallTreeComponentService.warningRecalculationChangesEventEmitter.subscribe(refreshWarningsEvent => {
            this.stepsService.getWarnings(this.model).subscribe((newModel: ComposedStepDef) => {
                ArrayUtil.replaceElementsInArray(this.model.stepCalls, newModel.stepCalls);
                this.stepCallTreeComponent.initTree();
            })
        })
    }

    setEditMode(editMode: boolean) {
        this.isEditMode = editMode;
        this.editModeEventEmitter.emit(this.isEditMode);
        this.stepCallTreeComponent.stepCallTreeComponentService.setEditMode(this.isEditMode);
    }

    ngAfterContentChecked(): void {
        this.pattern = this.model.stepPattern.getPatternText();
    }


    ngOnDestroy(): void {
        if(this.editModeStepCallTreeSubscription) this.editModeStepCallTreeSubscription.unsubscribe();
        if(this.warningRecalculationChangesSubscription) this.warningRecalculationChangesSubscription.unsubscribe();
    }

    ngDoCheck(): void {
        if (this.oldModel != this.model) {
            this.refreshWarnings();
            this.oldModel = this.model;
        }
    }
    private refreshWarnings() {
        this.warnings = [];
        for (const warning of this.model.warnings) {
            this.warnings.push(
                {severity: 'error', summary: warning.message}
            )
        }
    }
    onBeforeSave() {
    }

    onPatternChanged() {
        this.model.stepPattern.setPatternText(this.pattern)
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

    onTagsKeyUp(event) {
        if(event.key =="Enter") {
            if (this.currentTagSearch) {
                this.model.tags.push(this.currentTagSearch);
                this.currentTagSearch = null;
                this.tagsAutoComplete.multiInputEL.nativeElement.value = null;
                event.preventDefault();
            }
        }
    }

    onTagSelect(event) {
        this.currentTagSearch = null;
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
}
