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

    model: Feature = new Feature;
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
    @ViewChild(MarkdownEditorComponent) markdownEditor: MarkdownEditorComponent;

    pathForTitle: string = "";

    constructor(private route: ActivatedRoute,
                private urlService: UrlService,
                private featureService: FeatureService,
                private featuresTreeService: FeaturesTreeService,
                private tagsService: TagsService,
                ) {
    }

    ngOnInit(): void {
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
        this.markdownEditor.setEditMode(value);
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

    getFeatureUploadUrl(): string {
        return "/rest/features/fileUpload?path="+encodeURIComponent(this.model.path.toString());
    }

    initPathForTitle() {
        this.pathForTitle = "";
        if (this.model.path) {
            this.pathForTitle = new Path(this.model.path.directories, null, null).toString();
        }
    }

    cancelAction(): void {
        if (this.isCreateAction) {
            this.urlService.navigateToFeatures()
        } else {
            this.featureService.getFeature(this.model.path).subscribe(
                result => {
                    Object.assign(this.model, result);
                    this.setEditMode(false);
                }
            )
        }
    }

    deleteAction(): void {
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
            .save(this.model)
            .subscribe(
                savedModel => this.afterSaveHandler(savedModel),
                error => FormUtil.setErrorsToForm(this.form, error)
            );
    }

    private setDescription() {
        this.model.description = this.markdownEditor.value;
    }

    private afterSaveHandler(savedModel: Feature) {
        this.model = savedModel;
        this.setEditMode(false);
        this.featuresTreeService.initializeTestsTreeFromServer(this.model.path);
        this.urlService.navigateToFeature(this.model.path);
    }
}
