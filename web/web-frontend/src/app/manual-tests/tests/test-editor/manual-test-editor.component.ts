import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import 'rxjs/add/operator/switchMap';
import {ManualTestsTreeService} from "../tests-tree/manual-tests-tree.service";
import {IdUtils} from "../../../utils/id.util";
import {ManualTestModel} from "../../model/manual-test.model";
import {ManualTestStatus} from "../../model/enums/manual-test-status.enum";
import {ManualTestsService} from "../service/manual-tests.service";
import {UpdateManualTestModel} from "../../model/operation/update-manual-test.model";
import {AutoComplete} from "primeng/primeng";
import {ArrayUtil} from "../../../utils/array.util";
import {ManualTestStepModel} from "../../model/manual-step.model";
import {StringUtils} from "../../../utils/string-utils.util";
import {StepPhaseEnum} from "../../../model/enums/step-phase.enum";
import {AreYouSureModalComponent} from "../../../generic/components/are_you_sure_modal/are-you-sure-modal.component";
import {AreYouSureModalEnum} from "../../../generic/components/are_you_sure_modal/are-you-sure-modal.enum";

@Component({
    moduleId: module.id,
    selector: 'test-editor',
    templateUrl: 'manual-test-editor.component.html',
    styleUrls: ['manual-test-editor.component.scss', '../../../generic/css/generic.scss', '../../../generic/css/forms.scss']
})
export class ManualTestEditorComponent implements OnInit {

    @ViewChild(AreYouSureModalComponent) areYouSureModalComponent:AreYouSureModalComponent;

    ManualTestStatus = ManualTestStatus;
    StepPhaseEnum = StepPhaseEnum;

    manualTestModel: ManualTestModel = new ManualTestModel;
    isEditExistingTest: boolean; //TODO: is this used?
    isEditMode: boolean = false;
    isCreateAction: boolean = false;

    @ViewChild("tagsElement") tagsAutoComplete: AutoComplete;
    tagsToShow:string[] = [];
    currentTagSearch:string;

    constructor(private router: Router,
                private route: ActivatedRoute,
                private testsTreeService: ManualTestsTreeService,
                private manualTestsService: ManualTestsService) {
    }

    ngOnInit(): void {
        this.route.data.subscribe(data => {
            this.manualTestModel = data['manualTestModel'];
            this.isEditExistingTest =  IdUtils.isTemporaryId(this.manualTestModel.id);
            this.isEditMode = IdUtils.isTemporaryId(this.manualTestModel.id);
            if (this.isEditMode) {
                this.checkAndAddIfNewEmptyStepIsRequired();
            }
            this.isCreateAction = !this.manualTestModel.path.fileName;
        });
    }

    search(event) {
        this.currentTagSearch = event.query;

        let newTagsToShow = ArrayUtil.filterArray(
            ManualTestsService.manualTestsTags,
            event.query
        );
        for (let currentTag of this.manualTestModel.tags) {
            ArrayUtil.removeElementFromArray(newTagsToShow, currentTag)
        }
        this.tagsToShow = newTagsToShow
    }

    onTagsKeyUp(event) {
        if(event.key =="Enter") {
            if (this.currentTagSearch) {
                this.manualTestModel.tags.push(this.currentTagSearch);
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

    enableEditTestMode(): void {
        this.isEditMode = true;
        this.checkAndAddIfNewEmptyStepIsRequired();
    }

    getTestPathDirectoryAsString(): string {
        if(this.manualTestModel.path == null || this.manualTestModel.path.directories.length == 0) return "/";
        return this.manualTestModel.path.toDirectoryString()
    }

    getPhaseText(phase: StepPhaseEnum, stepIndex: number) {
        if(stepIndex == 0) {
            return StepPhaseEnum[phase];
        }

        if (this.manualTestModel.steps[stepIndex-1].phase == phase) {
            return StepPhaseEnum[StepPhaseEnum.AND];
        }

        return StepPhaseEnum[phase];
    }

    public moveStepUp(stepIndex: number):void {
        if(stepIndex > 0) {
            let previewsStep = this.manualTestModel.steps[stepIndex-1];
            this.manualTestModel.steps[stepIndex-1] = this.manualTestModel.steps[stepIndex];
            this.manualTestModel.steps[stepIndex] = previewsStep;
        }
    }

    public moveStepDown(stepIndex: number):void {
        if(0 <= stepIndex && stepIndex < this.manualTestModel.steps.length-1) {
            let nextStep = this.manualTestModel.steps[stepIndex+1];
            this.manualTestModel.steps[stepIndex+1] = this.manualTestModel.steps[stepIndex];
            this.manualTestModel.steps[stepIndex] = nextStep;
        }
    }

    public removeStep(stepIndex: number):void {
        this.manualTestModel.steps.splice(stepIndex, 1);
    }

    cancelAction(): void {
        if (this.isCreateAction) {
            this.router.navigate(["manual/tests"]);
        } else {
            this.manualTestsService.getTest(this.manualTestModel.path.toString()).subscribe(
                result => {
                    Object.assign(this.manualTestModel, result);
                    this.removeLastStepIfIsEmpty();
                    this.isEditMode = false;
                }
            )
        }
    }

    deleteAction(): void {
        this.areYouSureModalComponent.show(
            "Delete Test",
            "Are you shore you want to delete this Manual Test?",
            (action: AreYouSureModalEnum): void => {
                if (action == AreYouSureModalEnum.OK) {
                    this.manualTestsService.delete(this.manualTestModel).subscribe(restul => {
                        this.testsTreeService.initializeTestsTreeFromServer();
                        this.router.navigate(["manual/tests"]);
                    });
                }
            }
        );
    }

    saveAction(): void {
        this.removeLastStepIfIsEmpty();
        if(this.isCreateAction) {
            this.manualTestsService
                .createTest(this.manualTestModel)
                .subscribe(savedModel => this.afterSaveHandler(savedModel));
        } else {
            let updateManualTestModel = new UpdateManualTestModel(
                this.manualTestModel.path,
                this.manualTestModel
            );

            this.manualTestsService
                .updateTest(updateManualTestModel)
                .subscribe(savedModel => this.afterSaveHandler(savedModel));
        }
    }

    private afterSaveHandler(savedModel: ManualTestModel) {
        this.isEditMode = false;
        this.testsTreeService.initializeTestsTreeFromServer();
        this.router.navigate(["/manual/tests/show", {path : savedModel.path.toString()} ]);
    }

    private removeLastStepIfIsEmpty() {
        let lastStep = this.manualTestModel.steps[this.manualTestModel.steps.length - 1];
        if (StringUtils.isEmpty(lastStep.description)) {
            ArrayUtil.removeElementFromArray(this.manualTestModel.steps, lastStep);
        }
    }

    private checkAndAddIfNewEmptyStepIsRequired() {
        let lastStep = this.manualTestModel.steps[this.manualTestModel.steps.length - 1];
        if (!lastStep || !StringUtils.isEmpty(lastStep.description)) {
            this.manualTestModel.steps.push(
                new ManualTestStepModel()
            )
        }
    }
}
