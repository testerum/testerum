import {Component, OnDestroy, OnInit, ViewChild,} from '@angular/core';
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
import {AutoComplete} from "primeng/primeng";
import {ArrayUtil} from "../../../utils/array.util";
import {TagsService} from "../../../service/tags.service";
import {AreYouSureModalService} from "../../../generic/components/are_you_sure_modal/are-you-sure-modal.service";
import {AreYouSureModalEnum} from "../../../generic/components/are_you_sure_modal/are-you-sure-modal.enum";

@Component({
    moduleId: module.id,
    selector: 'feature-editor',
    templateUrl: 'feature-editor.component.html',
    styleUrls: ['feature-editor.component.scss']
})
export class FeatureEditorComponent implements OnInit, OnDestroy {
    markdownEditorOptions = {
        status: false,
        spellChecker: false
    };

    model: Feature = new Feature();
    fileAttachmentsAdded: File[] = [];
    attachmentsPathsToDelete: Path[] = [];

    isEditMode: boolean = false;
    isCreateAction: boolean = false;

    @ViewChild("tagsElement") tagsAutoComplete: AutoComplete;
    allKnownTags: Array<string> = [];
    tagsToShow:string[] = [];
    currentTagSearch:string;

    routeSubscription: Subscription;
    pathSubscription: Subscription;
    fileUploadSubscription: Subscription;

    @ViewChild(NgForm) form: NgForm;
    @ViewChild(MarkdownEditorComponent) descriptionMarkdownEditor: MarkdownEditorComponent;

    pathForTitle: string = "";

    constructor(private route: ActivatedRoute,
                private urlService: UrlService,
                private featureService: FeatureService,
                private featuresTreeService: FeaturesTreeService,
                private tagsService: TagsService,
                private areYouSureModalService: AreYouSureModalService,
                ) {
    }

    ngOnInit(): void {
        this.routeSubscription = this.route.data.subscribe(data => {
            this.model = data['featureModel'];
            if (this.descriptionMarkdownEditor) {
                this.descriptionMarkdownEditor.setValue(this.model.description);
            }

            let actionParam = this.route.snapshot.params["action"];

            if (actionParam == "create") {
                this.setEditMode(true);
                this.isCreateAction = true;
            } else {
                this.setEditMode(false);
                this.isCreateAction = false;
            }
            this.initPathForTitle();
        });
    }

    ngOnDestroy(): void {
        if(this.routeSubscription) this.routeSubscription.unsubscribe();
        if(this.pathSubscription) this.pathSubscription.unsubscribe();
        if(this.fileUploadSubscription) this.fileUploadSubscription.unsubscribe();
    }

    setEditMode(value: boolean) {
        if (value == true) {
            this.tagsService.getTags().subscribe(tags => {
                ArrayUtil.replaceElementsInArray(this.allKnownTags, tags);
            });
        }

        this.isEditMode = value;
        if (this.descriptionMarkdownEditor) {
            this.descriptionMarkdownEditor.setEditMode(value);
            this.descriptionMarkdownEditor.setValue(this.model.description);
        }
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
            this.urlService.navigateToFeatures()
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
            this.featuresTreeService.initializeTestsTreeFromServer(null);
            this.urlService.navigateToFeatures();
        });
    }

    saveAction(): void {
        this.setDescription();

        if (this.isCreateAction) {
            this.model.path.directories.push(this.model.name);
        }

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
        this.setEditMode(false);
        this.featuresTreeService.initializeTestsTreeFromServer(this.model.path);
        this.urlService.navigateToFeature(this.model.path);
    }
}
