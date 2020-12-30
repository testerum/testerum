import {Component, EventEmitter, OnDestroy, OnInit, ViewChild,} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {FeaturesTreeService} from "../features-tree/features-tree.service";
import {Subscription} from "rxjs";
import {Feature} from "../../../model/feature/feature.model";
import {FeatureService} from "../../../service/feature.service";
import {Path} from "../../../model/infrastructure/path/path.model";
import {MarkdownEditorComponent} from "../../../generic/components/markdown-editor/markdown-editor.component";
import {FormUtil} from "../../../utils/form.util";
import {NgForm} from "@angular/forms";
import {UrlService} from "../../../service/url.service";
import {ArrayUtil} from "../../../utils/array.util";
import {TagsService} from "../../../service/tags.service";
import {AreYouSureModalService} from "../../../generic/components/are_you_sure_modal/are-you-sure-modal.service";
import {AreYouSureModalEnum} from "../../../generic/components/are_you_sure_modal/are-you-sure-modal.enum";
import {AbstractComponentCanDeactivate} from "../../../generic/interfaces/can-deactivate/AbstractComponentCanDeactivate";
import {ContextService} from "../../../service/context.service";
import {ProjectService} from "../../../service/project.service";
import {Project} from "../../../model/home/project.model";
import {AutoComplete} from "primeng/autocomplete";
import {StepCallTreeComponent} from "../../../generic/components/step-call-tree/step-call-tree.component";

@Component({
    moduleId: module.id,
    selector: 'feature-editor',
    templateUrl: 'feature-editor.component.html',
    styleUrls: ['feature-editor.component.scss']
})
export class FeatureEditorComponent extends AbstractComponentCanDeactivate implements OnInit, OnDestroy {
    markdownEditorOptions = {
        status: false,
        spellChecker: false
    };

    model: Feature;
    fileAttachmentsAdded: File[] = [];
    attachmentsPathsToDelete: Path[] = [];

    projectName: string;

    isEditMode: boolean = false;
    readonly editModeEventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    isCreateAction: boolean = false;

    @ViewChild("tagsElement") tagsAutoComplete: AutoComplete;
    allKnownTags: Array<string> = [];
    tagsToShow:string[] = [];
    currentTagSearch:string;

    routeSubscription: Subscription;
    pathSubscription: Subscription;
    fileUploadSubscription: Subscription;

    @ViewChild(NgForm, { static: true }) form: NgForm;
    @ViewChild(MarkdownEditorComponent, { static: true }) descriptionMarkdownEditor: MarkdownEditorComponent;

    @ViewChild('beforeAllHooksCallTree', { static: true }) beforeAllHooksCallTree: StepCallTreeComponent;
    @ViewChild('beforeEachHooksCallTree', { static: true }) beforeEachHooksCallTree: StepCallTreeComponent;
    @ViewChild('afterEachHooksCallTree', { static: true }) afterEachHooksCallTree: StepCallTreeComponent;
    @ViewChild('afterAllHooksCallTree', { static: true }) afterAllHooksCallTree: StepCallTreeComponent;
    showHooksCategories: boolean = false;

    pathForTitle: string = "";

    private editModeEventEmitterSubscription: Subscription;

    constructor(private route: ActivatedRoute,
                private urlService: UrlService,
                private featureService: FeatureService,
                private featuresTreeService: FeaturesTreeService,
                private tagsService: TagsService,
                private contextService: ContextService,
                private projectService: ProjectService,
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

            this.beforeAllHooksCallTree.stepCallTreeComponentService.setEditMode(isEditMode);
            this.beforeEachHooksCallTree.stepCallTreeComponentService.setEditMode(isEditMode);
            this.afterEachHooksCallTree.stepCallTreeComponentService.setEditMode(isEditMode);
            this.afterAllHooksCallTree.stepCallTreeComponentService.setEditMode(isEditMode);

            if (this.descriptionMarkdownEditor) {
                this.descriptionMarkdownEditor.setEditMode(isEditMode);
                this.descriptionMarkdownEditor.setValue(this.model.description);
            }
        })
    }

    ngOnInit(): void {
        this.projectName = this.contextService.getProjectName();

        this.routeSubscription = this.route.data.subscribe(data => {
            this.model = data['featureModel'];

            let actionParam = this.route.snapshot.params["action"];

            if (actionParam == "create") {
                this.setEditMode(true);
                this.isCreateAction = true;
            } else {
                this.setEditMode(false);
                this.isCreateAction = false;
            }
            this.initPathForTitle();

            if (this.descriptionMarkdownEditor) {
                this.descriptionMarkdownEditor.setValue(this.model.description);
            }

            this.showHooksCategories = this.hasHooks();
        });
    }

    ngOnDestroy(): void {
        if(this.routeSubscription) this.routeSubscription.unsubscribe();
        if(this.pathSubscription) this.pathSubscription.unsubscribe();
        if(this.fileUploadSubscription) this.fileUploadSubscription.unsubscribe();
        if(this.editModeEventEmitterSubscription) this.editModeEventEmitterSubscription.unsubscribe();
    }

    canDeactivate(): boolean {
        return !this.isEditMode;
    }

    setEditMode(value: boolean) {
        this.editModeEventEmitter.emit(value);
    }

    enableEditMode(): void {
        this.setEditMode(true);
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
    }

    initPathForTitle() {
        this.pathForTitle = "";
        if (this.model.path) {
            this.pathForTitle = new Path(this.model.path.directories, null, null).toString();
        }
    }

    isRootFeature(): boolean {
        return this.model.path.isEmpty() && !this.isCreateAction;
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
            this.featureService.getFeature(this.model.path).subscribe(
                result => {
                    Object.assign(this.model, result);
                    if (this.descriptionMarkdownEditor) {
                        this.descriptionMarkdownEditor.setValue(this.model.description);
                    }

                    this.setEditMode(false);
                }
            )
        }
    }

    deleteAction(): void {
        this.areYouSureModalService.showAreYouSureModal(
            "Delete",
            "Are you sure you want to delete this feature?"
        ).subscribe((action: AreYouSureModalEnum) => {
            if (action == AreYouSureModalEnum.OK) {
                this.deleteActionAfterConfirmation();
            }
        });
    }

    private deleteActionAfterConfirmation(): void {
        this.featureService.delete(this.model.path).subscribe(result => {
            this.isEditMode = false; // to not show UnsavedChangesGuard
            this.featuresTreeService.initializeTestsTreeFromServer(this.model.path.getParentPath());
            this.urlService.navigateToFeature(this.model.path.getParentPath());
        });
    }

    saveAction(): void {
        this.setDescription();

        if (this.isCreateAction) {
            this.model.path.directories.push(this.model.name);
        }

        if (this.isRootFeature() && this.projectName != this.contextService.getProjectName()) {
            let projectPath = this.contextService.getProjectPath();
            let renameProject = new Project(this.projectName, projectPath);

            this.projectService.renameProject(renameProject).subscribe(
                (renamedProject: Project) => {
                    this.contextService.setCurrentProject(renamedProject);
                    this.saveFeature()
                },
                error => FormUtil.setErrorsToForm(this.form, error)
            );
        } else {
            this.saveFeature()
        }
    }

    private saveFeature() {
            this.featureService
                .save(this.model, this.fileAttachmentsAdded, this.attachmentsPathsToDelete)
                .subscribe(
                    savedModel => this.afterSaveHandler(savedModel),
                    error => FormUtil.setErrorsToForm(this.form, error)
                );
        }

    private setDescription() {
        this.model.description = this.descriptionMarkdownEditor.getValue();
    }

    private afterSaveHandler(savedModel: Feature) {
        this.model = savedModel;
        this.fileAttachmentsAdded.length = 0;
        this.attachmentsPathsToDelete.length = 0;

        this.setEditMode(false);
        this.featuresTreeService.initializeTestsTreeFromServer(this.model.path);
        this.urlService.navigateToFeature(this.model.path);
    }

// -- Hooks ------------------------------------------------------------------------------------------------------------
    addHooks() {
        this.showHooksCategories = true;
    }

    hasHooks(): boolean {
        let hooks = this.model.hooks;
        return hooks.beforeAll.length != 0
            || hooks.beforeEach.length != 0
            || hooks.afterEach.length != 0
            || hooks.afterAll.length != 0
    }

    addHook(hooksStepCallTree: StepCallTreeComponent) {
        hooksStepCallTree.stepCallTreeComponentService.addStepCallEditor(hooksStepCallTree.jsonTreeModel);
    }

    getHooksDescription(): string {
        return "**Hooks** allows you to define behavior that can be executed before or after the tests.\n" +
               "The hooks defined on a Feature will be executed on all the tests that are children of this feature.\n"
    }

    getBeforeAllHooksDescription(): string {
        return "**Before All Tests Hooks** are executed only once before all the tests defined in this Functionality.\n" +
            "If one of this Hooks throws an exception all the tests in this Functionality will be marked as failed.";
    }

    getBeforeEachHooksDescription(): string {
        return "**Before Each Test Hooks** are executed before each test defined in this Functionality.\n" +
            "If one of this Hooks throws an exception the test will be marked as failed.";
    }

    getAfterEachHooksDescription(): string {
        return "**After Each Test Hooks** are executed after each test no mather what the test outcome is.\n" +
            "If one of this Hooks throws an exception the test is not going to be marked as failed.";
    }

    getAfterAllHooksDescription(): string {
        return "**After All Tests Hooks** are executed only once after all the tests in this Functionality has been executed.\n" +
            "If one of this Hooks throws an exception the tests are not going to be marked as failed.\n" +
            "All the steps defined at the root level will be executed, regardless if a previews step thrown an exception.";
    }
}
