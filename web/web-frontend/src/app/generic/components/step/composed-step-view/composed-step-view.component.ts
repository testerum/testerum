import {AfterContentChecked, Component, Input, OnInit, ViewChild} from '@angular/core';
import {ComposedStepDef} from "../../../../model/composed-step-def.model";
import {NgForm} from "@angular/forms";
import {StepPhaseEnum} from "../../../../model/enums/step-phase.enum";
import {AutoComplete} from "primeng/primeng";
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

@Component({
    selector: 'composed-step-view',
    templateUrl: 'composed-step-view.component.html',
    styleUrls: ['composed-step-view.component.scss', '../../../../generic/css/generic.scss', '../../../../generic/css/forms.scss']
})

export class ComposedStepViewComponent implements AfterContentChecked {

    @Input() model: ComposedStepDef;
    @Input() isEditMode: boolean;
    @Input() isCreateAction: boolean = false;

    @ViewChild(NgForm) form: NgForm;
    StepPhaseEnum = StepPhaseEnum;

    pattern: string;

    areChildComponentsValid: boolean = true;

    @ViewChild("tagsElement") tagsAutoComplete: AutoComplete;
    allKnownTags: Array<string> = [];
    tagsToShow:string[] = [];
    currentTagSearch:string;

    @ViewChild(StepCallTreeComponent) stepCallTreeComponent: StepCallTreeComponent;

    constructor(private stepChooserService: StepChooserService,
                private tagsService: TagsService,) {
    }

    ngAfterContentChecked(): void {
        this.pattern = this.model.stepPattern.getPatternText();
    }

    onPatternChanged() {
        this.model.stepPattern.setPatternText(this.pattern)
    }

    openStepChooser() {
        this.stepChooserService.showStepChooserModal(this.model.path).subscribe( (stepDef: StepDef) => {
            this.onStepChose(stepDef);
        });
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

        this.stepCallTreeComponent.treeState.addStepCall(stepCall);
    }

    enableEditTestMode(): void {
        this.tagsService.getTags().subscribe(tags => {
            ArrayUtil.replaceElementsInArray(this.allKnownTags, tags);
        });

        this.isEditMode = true;
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
}