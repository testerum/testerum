import {AfterContentChecked, Component, Input, OnInit, ViewChild} from '@angular/core';
import {ComposedStepDef} from "../../../../model/composed-step-def.model";
import {UpdateIncompatibilityDialogComponent} from "../update-incompatilibity-dialog/update-incompatibility-dialog.component";
import {StepChooserComponent} from "../../../../generic/components/step-chooser/step-chooser.component";
import {NgForm} from "@angular/forms";
import {StepPhaseEnum} from "../../../../model/enums/step-phase.enum";
import {AutoComplete} from "primeng/primeng";
import {Arg} from "../../../../model/arg/arg.model";
import {ArrayUtil} from "../../../../utils/array.util";
import {StepDef} from "../../../../model/step-def.model";
import {StepCall} from "../../../../model/step-call.model";
import {ResourceMapEnum} from "../../../resources/editors/resource-map.enum";
import {StepTreeNodeModel} from "../../steps-tree/model/step-tree-node.model";
import {StepTreeContainerModel} from "../../steps-tree/model/step-tree-container.model";
import {StepCallTreeService} from "../../../../generic/components/step-call-tree/step-call-tree.service";
import {TagsService} from "../../../../service/tags.service";

@Component({
    selector: 'composed-step-view',
    templateUrl: 'composed-step-view.component.html',
    styleUrls: ['composed-step-view.component.scss', '../../../../generic/css/generic.scss', '../../../../generic/css/forms.scss']
})

export class ComposedStepViewComponent implements AfterContentChecked {

    @Input() model: ComposedStepDef;
    @Input() isEditMode: boolean;
    @Input() isCreateAction: boolean = false;

    @ViewChild(StepChooserComponent) stepChooserComponent: StepChooserComponent;
    @ViewChild(UpdateIncompatibilityDialogComponent) updateIncompatibilityDialogComponent: UpdateIncompatibilityDialogComponent;
    @ViewChild(NgForm) form: NgForm;
    StepPhaseEnum = StepPhaseEnum;

    isCreateAction: boolean = false;
    pattern: string;

    areChildComponentsValid: boolean = true;

    @ViewChild("tagsElement") tagsAutoComplete: AutoComplete;
    allKnownTags: Array<string> = [];
    tagsToShow:string[] = [];
    currentTagSearch:string;

    constructor(private stepCallTreeService: StepCallTreeService,
                private tagsService: TagsService,) {
    }

    ngAfterContentChecked(): void {
        this.pattern = this.model.stepPattern.getPatternText();
    }

    onPatternChanged() {
        this.model.stepPattern.setPatternText(this.pattern)
    }

    openStepChooser() {
        this.stepChooserComponent.showStepChooserModelWithoutStepReference(this, this.model.path);
    }

    onStepChose(choseStep: StepDef): void {
        let stepCall = new StepCall();
        stepCall.stepDef = choseStep;
        for (let stepParam of choseStep.stepPattern.getParamParts()) {
            let valueArg = new Arg();
            valueArg.serverType = stepParam.serverType;
            valueArg.uiType = stepParam.uiType;
            valueArg.content = ResourceMapEnum.getResourceMapEnumByUiType(stepParam.uiType).getNewInstance();

            stepCall.args.push(valueArg);
        }

        this.stepCallTreeService.addStepCall(stepCall);
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
