import {Component, DoCheck, EventEmitter, OnDestroy, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {TestsService} from "../../../service/tests.service";
import {TestModel} from "../../../model/test/test.model";
import {StepPhaseEnum} from "../../../model/enums/step-phase.enum";
import {TestsRunnerService} from "../tests-runner/tests-runner.service";
import {FeaturesTreeService} from "../features-tree/features-tree.service";
import {IdUtils} from "../../../utils/id.util";
import {Subscription} from "rxjs";
import {Path} from "../../../model/infrastructure/path/path.model";
import {UrlService} from "../../../service/url.service";
import {TagsService} from "../../../service/tags.service";
import {ArrayUtil} from "../../../utils/array.util";
import {StepCallTreeComponent} from "../../../generic/components/step-call-tree/step-call-tree.component";
import {MarkdownEditorComponent} from "../../../generic/components/markdown-editor/markdown-editor.component";
import {AreYouSureModalEnum} from "../../../generic/components/are_you_sure_modal/are-you-sure-modal.enum";
import {AreYouSureModalService} from "../../../generic/components/are_you_sure_modal/are-you-sure-modal.service";
import {AbstractComponentCanDeactivate} from "../../../generic/interfaces/can-deactivate/AbstractComponentCanDeactivate";
import {StepCallWarningUtil} from "../../../generic/components/step-call-tree/util/step-call-warning.util";
import {ContextService} from "../../../service/context.service";
import {ScenarioTreeComponent} from "./scenario-tree/scenario-tree.component";
import {AutoComplete} from "primeng/autocomplete";
import {Message} from "primeng/api";

@Component({
    moduleId: module.id,
    selector: 'test-editor',
    templateUrl: 'test-editor.component.html',
    styleUrls: ['test-editor.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class TestEditorComponent extends AbstractComponentCanDeactivate implements OnInit, OnDestroy, DoCheck{

    StepPhaseEnum = StepPhaseEnum;
    testModel: TestModel = new TestModel;
    oldTestModel: TestModel;
    pathForTitle: string = "";
    isEditExistingTest: boolean; //TODO: is this used?
    isCreateAction: boolean = false;
    isEditMode: boolean = false;
    readonly editModeEventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    @ViewChild("tagsElement") tagsAutoComplete: AutoComplete;
    allKnownTags: Array<string> = [];
    tagsToShow:string[] = [];
    currentTagSearch:string;

    @ViewChild(ScenarioTreeComponent, { static: true }) scenarioTreeComponent: ScenarioTreeComponent;

    @ViewChild('stepsCallTree', { static: true }) stepsCallTreeComponent: StepCallTreeComponent;
    @ViewChild('hooksCallTree', { static: true }) hooksCallTreeComponent: StepCallTreeComponent;
    showHooks: boolean = false;

    descriptionMarkdownEditor: MarkdownEditorComponent;
    @ViewChild("descriptionMarkdownEditor", { static: true }) set setDescriptionMarkdownEditor(descriptionMarkdownEditor: MarkdownEditorComponent) {
        if (descriptionMarkdownEditor != null) {
            descriptionMarkdownEditor.setEditMode(this.isEditMode);
            descriptionMarkdownEditor.setValue(this.testModel.description);
        }
        this.descriptionMarkdownEditor = descriptionMarkdownEditor;
    }

    warnings: Message[] = [];

    private routeSubscription: Subscription;
    private warningRecalculationChangesSubscription: Subscription;
    private addNewScenarioSubscription: Subscription;
    private editModeEventEmitterSubscription: Subscription;

    constructor(private route: ActivatedRoute,
                private urlService: UrlService,
                private testsTreeService: FeaturesTreeService,
                private testsService: TestsService,
                private testsRunnerService: TestsRunnerService,
                private tagsService: TagsService,
                private contextService: ContextService,
                private areYouSureModalService: AreYouSureModalService) {
        super();

        //should be initialized before we use "setEditMode"
        this.editModeEventEmitterSubscription = this.editModeEventEmitter.subscribe( (isEditMode: boolean) => {

            if(this.isEditMode == isEditMode) return;

            if (isEditMode) {
                this.tagsService.getTags().subscribe(tags => {
                    ArrayUtil.replaceElementsInArray(this.allKnownTags, tags);
                });
            }

            this.isEditMode = isEditMode;

            this.stepsCallTreeComponent.stepCallTreeComponentService.setEditMode(isEditMode);
            this.hooksCallTreeComponent.stepCallTreeComponentService.setEditMode(isEditMode);

            if(this.descriptionMarkdownEditor) {
                this.descriptionMarkdownEditor.setEditMode(isEditMode);
            }
        })
    }

    ngOnInit(): void {
        this.oldTestModel = this.testModel;

        this.routeSubscription = this.route.data.subscribe(data => {
            this.testModel = data['testModel'];

            this.isEditExistingTest =  IdUtils.isTemporaryId(this.testModel.id);
            this.setEditMode(IdUtils.isTemporaryId(this.testModel.id));
            this.isCreateAction = !this.testModel.path.fileName;

            if (this.descriptionMarkdownEditor) {
                this.descriptionMarkdownEditor.setValue(this.testModel.description, this.isCreateAction);
            }

            this.initPathForTitle();
        });

        this.warningRecalculationChangesSubscription = this.stepsCallTreeComponent.stepCallTreeComponentService.warningRecalculationChangesEventEmitter.subscribe(refreshWarningsEvent => {
            let testModel = this.getModelForWarningRecalculation();

            this.testsService.getWarnings(testModel).subscribe((newTestModel:TestModel) => {
                StepCallWarningUtil.copyWarningState(this.testModel.stepCalls, newTestModel.stepCalls);

                ArrayUtil.replaceElementsInArray(this.testModel.warnings, newTestModel.warnings);
                this.refreshWarnings();
            })
        });

    }

    private getModelForWarningRecalculation() {
        if (this.testModel.name) {
            return this.testModel
        }

        let testModel: TestModel = this.testModel.clone();
        testModel.name = IdUtils.getTemporaryId();

        return testModel;
    }

    ngDoCheck(): void {
        if (this.oldTestModel != this.testModel) {
            this.refreshWarnings();
            this.oldTestModel = this.testModel;
        }
    }

    initPathForTitle() {
        this.pathForTitle = "";
        let nodeName = null;
        if (!this.isCreateAction) {
            nodeName = this.testModel.name;
        }
        if (this.testModel.path) {
            this.pathForTitle = new Path(this.testModel.path.directories, nodeName, null).toString();
        }
    }

    private refreshWarnings() {
        this.warnings = [];
        for (const warning of this.testModel.warnings) {
            this.warnings.push(
                {severity: 'error', summary: warning.message}
            )
        }
    }

    ngOnDestroy(): void {
        if(this.routeSubscription) this.routeSubscription.unsubscribe();
        if(this.warningRecalculationChangesSubscription) this.warningRecalculationChangesSubscription.unsubscribe();
        if(this.addNewScenarioSubscription) this.addNewScenarioSubscription.unsubscribe();
        if(this.editModeEventEmitterSubscription) this.editModeEventEmitterSubscription.unsubscribe();
    }

    canDeactivate(): boolean {
        return !this.isEditMode;
    }

    onManualAutomatedTestToggle() {
        this.testModel.properties.isManual = !this.testModel.properties.isManual;
        if(!this.testModel.properties.isManual) {
            this.stepsCallTreeComponent.triggerWarningRecalculation();
        }
    }

    addStep() {
        if (!this.isEditMode) {
            this.setEditMode(true);
        }

        this.stepsCallTreeComponent.stepCallTreeComponentService.addStepCallEditor(this.stepsCallTreeComponent.jsonTreeModel);
    }

    setEditMode(isEditMode: boolean) {
        this.editModeEventEmitter.emit(isEditMode);
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

    onTagsKeyUp(event: KeyboardEvent) {
        event.preventDefault();

        if (event.key == "Enter") {
            this.addCurrentTagToTags();
        }
    }

    addCurrentTagToTags() {
        if (this.currentTagSearch) {
            this.testModel.tags.push(this.currentTagSearch);
            this.currentTagSearch = null;
            this.tagsAutoComplete.multiInputEL.nativeElement.value = null;
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
        this.areYouSureModalService.showAreYouSureModal(
            "Cancel",
            "Are you sure you want to cancel all your changes?"
        ).subscribe((action: AreYouSureModalEnum) => {
            if (action == AreYouSureModalEnum.OK) {
                this.cancelActionAfterConfirmation();
            }
        });
    }

    private cancelActionAfterConfirmation(): void {
        if (this.isCreateAction) {
            this.setEditMode(false); //this is required for CanDeactivate
            this.urlService.navigateToProject()
        } else {
            this.testsService.getTest(this.testModel.path.toString()).subscribe(
                result => {
                    Object.assign(this.testModel, result);
                    this.descriptionMarkdownEditor.setValue(this.testModel.description);

                    this.setEditMode(false);
                    this.refreshWarnings();
                }
            )
        }
    }

    deleteAction(): void {
        this.areYouSureModalService.showAreYouSureModal(
            "Delete",
            "Are you sure you want to delete this test?"
        ).subscribe((action: AreYouSureModalEnum) => {
            if (action == AreYouSureModalEnum.OK) {
                this.deleteActionAfterConfirmation();
            }
        });
    }

    private deleteActionAfterConfirmation(): void {
        this.testsService.delete(this.testModel).subscribe(restul => {
            this.isEditMode = false; // to not show UnsavedChangesGuard
            this.testsTreeService.initializeTestsTreeFromServer(this.testModel.path.getParentPath());
            this.urlService.navigateToFeature(this.testModel.path.getParentPath());
        });
    }

    saveAction(): void {
        this.setDescription();

        this.testsService
            .saveTest(this.testModel)
            .subscribe(savedModel => this.afterSaveHandler(savedModel));
    }
    private setDescription() {
        this.testModel.description = this.descriptionMarkdownEditor.getValue();
    }

    private afterSaveHandler(savedModel: TestModel) {
        this.testModel = savedModel;
        this.setEditMode(false);
        this.testsTreeService.initializeTestsTreeFromServer(savedModel.path);
        this.urlService.navigateToTest(savedModel.path);
    }

    runTest(): void {
        this.testsRunnerService.runTests([this.testModel.path]);
    }

    canPasteStep(): boolean {
        return this.contextService.stepToCut != null || this.contextService.stepToCopy != null;
    }

    onPaste(stepsOrHooksTree: StepCallTreeComponent) {
        let stepCallTreeComponentService = stepsOrHooksTree.stepCallTreeComponentService;
        let treeModel = stepsOrHooksTree.jsonTreeModel;

        let stepToPaste = this.contextService.stepToCopy || this.contextService.stepToCut
        if (stepToPaste) {
            stepCallTreeComponentService.addStepCallToParentContainer(stepToPaste, treeModel);
            this.contextService.onPaste();
        }
    }

    addScenario() {
        if (!this.isEditMode) {
            this.setEditMode(true);
        }

        this.scenarioTreeComponent.scenarioTreeComponentService.addNewScenario();
    }

    getScenarioDescription(): string {
        return "A Scenario is a set of parameters.\n" +
            "This test will be executed multiple times, once for each scenario.\n" +
            "If there is no Scenario defined the test will be executed only once.";
    }

    getStepsDescription(): string {
        return "A test is a list of actions, called steps.\n" +
            "The steps can use the parameters defined in the Scenarios.";
    }

    canPasteScenario(): boolean {
        return this.scenarioTreeComponent.scenarioTreeComponentService.canPaste();
    }

    onPasteScenario() {
        this.scenarioTreeComponent.scenarioTreeComponentService.onPasteScenario();
    }

    hasScenarios(): boolean {
        return this.testModel.scenarios.length != 0;
    }

    runTestForAllScenarios() {
        this.testsRunnerService.runTests([this.testModel.path]);
    }

//-- Hooks Method ------------------------------------------------------------------------------------------------------

    shouldShowHooks(): boolean {
        if(this.showHooks) return true;

        return this.testModel.afterHooks.length != 0 && !this.testModel.properties.isManual
    }

    addAfterTestHook() {
        this.showHooks = true;

        if (!this.isEditMode) {
            this.setEditMode(true);
        }

        this.hooksCallTreeComponent.stepCallTreeComponentService.addStepCallEditor(this.hooksCallTreeComponent.jsonTreeModel);
    }

    getAfterTestHooksDescription(): string {
        return "AFTER TEST HOOKS. \n" +
            "The hooks are steps that are executed after the test steps.\n" +
            "The hooks are executed no matter of the test outcome.\n" +
            "All the root after test hooks are executed, no matter if the previews hook has failed.\n"
    }
}
