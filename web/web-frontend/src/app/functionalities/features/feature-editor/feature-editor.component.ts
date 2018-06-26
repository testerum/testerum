import {
    AfterViewInit,
    Component,
    OnDestroy,
    OnInit, ViewChild,
} from '@angular/core';
import {Router, ActivatedRoute} from "@angular/router";
import 'rxjs/add/operator/switchMap';
import {FeaturesTreeService} from "../features-tree/features-tree.service";
import {Subscription} from "rxjs/Subscription";
import {Feature} from "../../../model/feature/feature.model";
import {FeatureService} from "../../../service/feature.service";
import {Path} from "../../../model/infrastructure/path/path.model";
import {MarkdownEditorComponent} from "../../../generic/components/markdown-editor/markdown-editor.component";
import {Message} from "primeng/api";
import {Attachment} from "../../../model/file/attachment.model";

@Component({
    moduleId: module.id,
    selector: 'feature-editor',
    templateUrl: 'feature-editor.component.html',
    styleUrls: ['feature-editor.component.css', '../../../generic/css/generic.css', '../../../generic/css/forms.css']
})
export class FeatureEditorComponent implements OnInit, OnDestroy {
    markdownEditorOptions = {
        status: false,
        spellChecker: false
    };

    model: Feature = new Feature;
    isEditMode: boolean = false;
    isCreateAction: boolean = false;

    routeSubscription: Subscription;
    pathSubscription: Subscription;
    fileUploadSubscription: Subscription;

    @ViewChild(MarkdownEditorComponent) markdownEditor: MarkdownEditorComponent;

    pathForTitle: string = "";

    uploadedFiles: any[] = [];
    msgs: Message[];

    constructor(private router: Router,
                private route: ActivatedRoute,
                private featureService: FeatureService,
                private featuresTreeService: FeaturesTreeService) {
    }

    ngOnInit(): void {
        this.routeSubscription = this.route.data.subscribe(data => {
            this.model = data['featureModel'];
            if (this.model) {
                this.setEditMode(false);
                this.isCreateAction = false;
                this.initPathForTitle();

            } else {
                this.setEditMode(true);
                this.isCreateAction = true;


                this.pathSubscription = this.route.params.subscribe(
                    params => {
                        let pathAsString = params['path'];
                        let path = pathAsString ? Path.createInstance(pathAsString) : new Path([], null, null);

                        this.model = new Feature();
                        this.model.path = path;

                        this.initPathForTitle();
                    }
                )
            }
        });
    }

    ngOnDestroy(): void {
        if(this.routeSubscription) this.routeSubscription.unsubscribe();
        if(this.pathSubscription) this.pathSubscription.unsubscribe();
        if(this.fileUploadSubscription) this.fileUploadSubscription.unsubscribe();
    }

    setEditMode(value: boolean) {
        this.isEditMode = value;
        this.markdownEditor.setEditMode(value);
    }

    enableEditTestMode(): void {
        this.setEditMode(true);
    }

    getFeatureUploadUrl(): string {
        return encodeURI("/rest/features/fileUpload?path="+this.model.path.toString());
    }

    initPathForTitle() {
        if (!this.model.path) {
            this.pathForTitle = ""
        }
        this.pathForTitle = new Path(this.model.path.directories, null, null).toString();
    }

    cancelAction(): void {

        if (this.isCreateAction) {
            this.router.navigate(["features"]);
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
            this.featuresTreeService.initializeTestsTreeFromServer();
            this.router.navigate(["automated/tests"]);
        });
    }

    saveAction(): void {
        this.setDescription();

        if(this.isCreateAction) {
            this.featureService
                .create(this.model)
                .subscribe(savedModel => this.afterSaveHandler(savedModel));
        } else {
            this.featureService
                .update(this.model)
                .subscribe(savedModel => this.afterSaveHandler(savedModel));
        }
    }

    private setDescription() {
        this.model.description = this.markdownEditor.value;
    }

    private afterSaveHandler(savedModel: Feature) {
        this.model = savedModel;
        this.setEditMode(false);
        this.featuresTreeService.initializeTestsTreeFromServer();
    }
}
