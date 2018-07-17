import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {TestsService} from "../../../service/tests.service";
import {TestModel} from "../../../model/test/test.model";
import 'rxjs/add/operator/switchMap';
import {StepPhaseEnum} from "../../../model/enums/step-phase.enum";
import {TestsRunnerService} from "../tests-runner/tests-runner.service";
import {StepCall} from "../../../model/step-call.model";
import {Arg} from "../../../model/arg/arg.model";
import {StepChoseHandler} from "../../../generic/components/step-chooser/step-choosed-handler.interface";
import {StepChooserComponent} from "../../../generic/components/step-chooser/step-chooser.component";
import {StepDef} from "../../../model/step-def.model";
import {FeaturesTreeService} from "../features-tree/features-tree.service";
import {IdUtils} from "../../../utils/id.util";
import {UpdateTestModel} from "../../../model/test/operation/update-test.model";
import {StepCallTreeService} from "../../../generic/components/step-call-tree/step-call-tree.service";
import {ResourceMapEnum} from "../../resources/editors/resource-map.enum";
import {Subscription} from "rxjs/Subscription";
import {Path} from "../../../model/infrastructure/path/path.model";
import {UrlService} from "../../../service/url.service";
import {StepCallEditorContainerModel} from "../../../generic/components/step-call-tree/model/step-call-editor-container.model";

@Component({
    moduleId: module.id,
    selector: 'test-editor',
    templateUrl: 'test-editor.component.html',
    styleUrls: ['test-editor.component.css', '../../../generic/css/generic.css', '../../../generic/css/forms.css']
})
export class TestEditorComponent implements OnInit, OnDestroy {

    StepPhaseEnum = StepPhaseEnum;
    testModel: TestModel = new TestModel;
    isEditExistingTest: boolean; //TODO: is this used?
    isEditMode: boolean = false;
    isCreateAction: boolean = false;

    routeSubscription: Subscription;
    editModeStepCallTreeSubscription: Subscription;

    constructor(private route: ActivatedRoute,
                private urlService: UrlService,
                private testsTreeService: FeaturesTreeService,
                private testsService: TestsService,
                private testsRunnerService: TestsRunnerService,
                private stepCallTreeService: StepCallTreeService) {
    }

    ngOnInit(): void {
        this.routeSubscription = this.route.data.subscribe(data => {
            this.testModel = data['testModel'];
            this.isEditExistingTest =  IdUtils.isTemporaryId(this.testModel.id);
            this.setEditMode(IdUtils.isTemporaryId(this.testModel.id));
            this.isCreateAction = !this.testModel.path.fileName
        });
        this.editModeStepCallTreeSubscription = this.stepCallTreeService.editModeEventEmitter.subscribe( (editMode: boolean) => {
                this.isEditMode = editMode;
            }
        )
    }

    ngOnDestroy(): void {
        if(this.routeSubscription) this.routeSubscription.unsubscribe();
        if(this.editModeStepCallTreeSubscription) this.editModeStepCallTreeSubscription.unsubscribe();
    }

    addStep() {

        this.stepCallTreeService.addStepCallEditor(this.stepCallTreeService.jsonTreeModel);

    }

    setEditMode(isEditMode: boolean) {
        this.isEditMode = isEditMode;
        this.stepCallTreeService.setEditMode(isEditMode);
    }

    enableEditTestMode(): void {
        this.setEditMode(true);
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
            this.testsTreeService.initializeTestsTreeFromServer();
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
        this.testsTreeService.initializeTestsTreeFromServer();
        this.urlService.navigateToTest(savedModel.path);
    }

    runTest(): void {
        this.testsRunnerService.runTests([this.testModel]);
    }
}
