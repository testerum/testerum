import {AfterContentInit, Component, DoCheck, OnDestroy, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {TestsService} from "../../../service/tests.service";
import {TestModel} from "../../../model/test/test.model";
import 'rxjs/add/operator/switchMap';
import {StepPhaseEnum} from "../../../model/enums/step-phase.enum";
import {TestsRunnerService} from "../tests-runner/tests-runner.service";
import {FeaturesTreeService} from "../features-tree/features-tree.service";
import {IdUtils} from "../../../utils/id.util";
import {UpdateTestModel} from "../../../model/test/operation/update-test.model";
import {StepCallTreeService} from "../../../generic/components/step-call-tree/step-call-tree.service";
import {Subscription} from "rxjs/Subscription";
import {Path} from "../../../model/infrastructure/path/path.model";
import {UrlService} from "../../../service/url.service";
import {AutoComplete, Message} from "primeng/primeng";
import {TagsService} from "../../../service/tags.service";
import {ArrayUtil} from "../../../utils/array.util";

@Component({
    moduleId: module.id,
    selector: 'test-editor',
    templateUrl: 'test-editor.component.html',
    styleUrls: ['test-editor.component.scss', '../../../generic/css/generic.scss', '../../../generic/css/forms.scss'],
    encapsulation: ViewEncapsulation.None
})
export class TestEditorComponent implements OnInit, OnDestroy, DoCheck{

    StepPhaseEnum = StepPhaseEnum;
    testModel: TestModel = new TestModel;
    oldTestModel: TestModel;
    isEditExistingTest: boolean; //TODO: is this used?
    isEditMode: boolean = false;
    isCreateAction: boolean = false;

    @ViewChild("tagsElement") tagsAutoComplete: AutoComplete;
    allKnownTags: Array<string> = [];
    tagsToShow:string[] = [];
    currentTagSearch:string;

    warnings: Message[] = [];
    oldWarnings: Message[] = [];

    routeSubscription: Subscription;
    editModeStepCallTreeSubscription: Subscription;

    constructor(private route: ActivatedRoute,
                private urlService: UrlService,
                private testsTreeService: FeaturesTreeService,
                private testsService: TestsService,
                private testsRunnerService: TestsRunnerService,
                private stepCallTreeService: StepCallTreeService,
                private tagsService: TagsService) {
    }

    ngOnInit(): void {
        this.oldTestModel = this.testModel;

        this.routeSubscription = this.route.data.subscribe(data => {
            this.testModel = data['testModel'];
            this.isEditExistingTest =  IdUtils.isTemporaryId(this.testModel.id);
            this.setEditMode(IdUtils.isTemporaryId(this.testModel.id));
            this.isCreateAction = !this.testModel.path.fileName
        });
        this.editModeStepCallTreeSubscription = this.stepCallTreeService.editModeEventEmitter.subscribe( (editMode: boolean) => {
                this.isEditMode = editMode;
            }
        );
    }

    ngDoCheck(): void {
        if (this.oldTestModel != this.testModel) {
            this.refreshWarnings();
            this.oldTestModel = this.testModel;
        }
    }

    private refreshWarnings() {
        this.warnings = [];
        for (const warning of this.testModel.warnings) {
            this.warnings.push(
                {severity: 'warn', summary: warning.message}
            )
        }
    }

    ngOnDestroy(): void {
        if(this.routeSubscription) this.routeSubscription.unsubscribe();
        if(this.editModeStepCallTreeSubscription) this.editModeStepCallTreeSubscription.unsubscribe();
    }

    addStep() {
        this.stepCallTreeService.addStepCallEditor(this.stepCallTreeService.jsonTreeModel);
    }

    setEditMode(isEditMode: boolean) {
        if (isEditMode) {
            this.tagsService.getTags().subscribe(tags => {
                ArrayUtil.replaceElementsInArray(this.allKnownTags, tags);
            });
        }

        this.isEditMode = isEditMode;
        this.stepCallTreeService.setEditMode(isEditMode);
    }

    enableEditTestMode(): void {
        this.setEditMode(true);
    }

    onSearchTag(event) {
        this.currentTagSearch = event.query;

        let newTagsToShow = ArrayUtil.filterArray(
            this.allKnownTags,
            event.query
        );
        for (let currentTag of this.testModel.tags) {
            ArrayUtil.removeElementFromArray(newTagsToShow, currentTag)
        }
        this.tagsToShow = newTagsToShow
    }

    onTagsKeyUp(event) {
        if(event.key =="Enter") {
            if (this.currentTagSearch) {
                this.testModel.tags.push(this.currentTagSearch);
                this.currentTagSearch = null;
                this.tagsAutoComplete.multiInputEL.nativeElement.value = null;
                event.preventDefault();
            }
        }
    }

    onTagSelect(event) {
        this.currentTagSearch = null;
    }

    getPathWithoutTestName(): string {
        if (!this.testModel.path) {
            return ""
        }
        return new Path(this.testModel.path.directories, null, null).toString();
    }

    cancelAction(): void {
        if (this.isCreateAction) {
            this.urlService.navigateToFeatures()
        } else {
            this.testsService.getTest(this.testModel.path.toString()).subscribe(
                result => {
                    Object.assign(this.testModel, result);
                    this.setEditMode(false)
                }
            )
        }
    }

    deleteAction(): void {
        this.testsService.delete(this.testModel).subscribe(restul => {
            this.testsTreeService.initializeTestsTreeFromServer(null);
            this.urlService.navigateToFeatures();
        });
    }

    saveAction(): void {
        if(this.isCreateAction) {
            this.testsService
                .createTest(this.testModel)
                .subscribe(savedModel => this.afterSaveHandler(savedModel));
        } else {
            let updateTestModel = new UpdateTestModel(
                this.testModel.path,
                this.testModel
            );

            this.testsService
                .updateTest(updateTestModel)
                .subscribe(savedModel => this.afterSaveHandler(savedModel));
        }
    }

    private afterSaveHandler(savedModel: TestModel) {
        this.testModel = savedModel;
        this.setEditMode(false);
        this.testsTreeService.initializeTestsTreeFromServer(savedModel.path);
        this.urlService.navigateToTest(savedModel.path);
    }

    runTest(): void {
        this.testsRunnerService.runTests([this.testModel]);
    }
}
