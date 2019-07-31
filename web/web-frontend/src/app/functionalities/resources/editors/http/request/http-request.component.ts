import {ChangeDetectorRef, Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {ResourceService} from "../../../../../service/resources/resource.service";
import {ResourcesTreeService} from "../../../tree/resources-tree.service";
import {HttpRequest} from "../../../../../model/resource/http/http-request.model";
import {HttpRequestService} from "./http-request.service";
import {HttpMethod} from "../../../../../model/enums/http-method-enum";
import {HttpPart} from "../../../../../model/resource/http/enum/http-part.enum";
import {HttpParamsComponent} from "./params/http-params.component";
import {NgForm} from "@angular/forms";
import {ResourceComponent} from "../../resource-component.interface";
import {ParamStepPatternPart} from "../../../../../model/text/parts/param-step-pattern-part.model";
import {Subscription} from "rxjs";
import * as Prism from 'prismjs';
import 'prismjs/components/prism-json';
import 'prismjs/components/prism-javascript';
import 'prismjs/components/prism-http';
import {HttpContentType} from "../../../../../model/resource/http/enum/http-content-type.enum";

@Component({
    moduleId: module.id,
    selector: 'http-request',
    templateUrl: 'http-request.component.html',
    providers: [HttpRequestService],
    styleUrls: [
        'http-request.component.scss',
        '../../resource-editor.scss'
    ]
})
export class HttpRequestComponent extends ResourceComponent<HttpRequest> implements OnInit, OnDestroy {

    @Input() name: string;
    @Input() model:HttpRequest;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() private _editMode: boolean = false;
    @Input() condensedViewMode: boolean = false;
    @Input() isSharedResource: boolean = false;
    @Input() contextActions: ResourceContextActions = new class implements ResourceContextActions {
        cancel() {}
        save() {}
    };

    @ViewChild(NgForm) form: NgForm;

    partToDisplay: HttpPart = HttpPart.HEADERS;
    HttpPart = HttpPart;
    HttpMethod = HttpMethod;

    @ViewChild("httpParams") httpParams: HttpParamsComponent;

    private changesMadeEventSubscription: Subscription;
    constructor(private cd: ChangeDetectorRef,
                private route: ActivatedRoute,
                private resourceService: ResourceService,
                private resourcesTreeService: ResourcesTreeService,
                private httpRequestService: HttpRequestService) {
        super()
    }

    ngOnInit() {
        if (this.model == null) {
            this.model = new HttpRequest();
        }

        this.changesMadeEventSubscription = this.httpRequestService.changesMadeEventEmitter.subscribe(() => {
            this.refresh();
        });

        this.initPartToDisplay(this.model);
    }

    ngOnDestroy(): void {
        if (this.changesMadeEventSubscription) { this.changesMadeEventSubscription.unsubscribe()}
    }

    refresh() {
        if (!this.cd['destroyed']) { //without this the folowing error will appear: "ERROR Error: ViewDestroyedError: Attempt to use a destroyed view: detectChanges"
            this.cd.detectChanges();
        }
    }

    private initPartToDisplay(httpRequest: HttpRequest) {
        this.httpRequestService.setHttpRequestResource(this.model); //TODO Ionut: maybe we don't need this
        this.httpRequestService.setEditMode(this._editMode);

        if (httpRequest.method == this.HttpMethod.GET) {
            this.partToDisplay = HttpPart.HEADERS;
            return;
        }

        if (httpRequest.headers.length > 1 && httpRequest.body.isEmpty()) {
            this.partToDisplay = HttpPart.HEADERS;
            return;
        }

        this.partToDisplay = HttpPart.BODY;
    }

    set editMode(value: boolean) {
        this._editMode = value;
        if (this.httpRequestService) {
            this.httpRequestService.setEditMode(value)
        }
    }

    display(httpPartToDisplay: HttpPart) {

        if (httpPartToDisplay == HttpPart.BODY && this.getModel().method == HttpMethod.GET) {
            return;
        }

        this.partToDisplay = httpPartToDisplay;
    }

    setEditMode(isEditMode: boolean): void {
        this._editMode = isEditMode;
        this.httpRequestService.setEditMode(isEditMode);
    }

    isEditMode(): boolean {
        return this.httpRequestService.editMode;
    }

    getModel(): HttpRequest {
        return this.model;
    }

    getHttpMethods(): Array<HttpMethod> {
        return HttpMethod.enums;
    }

    methodChange(value: HttpMethod) {
        this.getModel().method = value;
        if (value == HttpMethod.GET && this.partToDisplay == HttpPart.BODY) {
            this.partToDisplay = HttpPart.HEADERS;
        }
    }

    urlChanged() {
        this.httpParams.refreshParamsFromUrl()
    }

    showHideParams() {
        if (this.httpParams.shouldShow) {
            this.httpParams.hide();
        } else {
            this.httpParams.show();
        }
    }

    executeRequest(): void {
        this.partToDisplay = HttpPart.RESPONSE;
        this.httpRequestService.executeRequest();
    }

    shouldDisplayResponseTab(): boolean {
        return this.httpRequestService.shouldDisplayHttpResponseTab;
    }

    closeResponseTab(): void {
        if (this.partToDisplay == this.HttpPart.RESPONSE) {
            this.partToDisplay = HttpPart.HEADERS;
        }
        this.httpRequestService.shouldDisplayHttpResponseTab = false;
    }

    isFormValid(): boolean {
        return this.form.valid;
    }

    getForm(): NgForm {
        return this.form;
    }

    getHighlightedBody(): string {
        let contentType = this.getContentType();

        if (this.model.body.content != null) {
            if (contentType == HttpContentType.JSON) {
                return Prism.highlight(this.model.body.content, Prism.languages.json, 'json');
            }
            if (contentType == HttpContentType.XML || contentType == HttpContentType.XML_TEXT) {
                return Prism.highlight(this.model.body.content, Prism.languages.xml, 'xml');
            }
            if (contentType == HttpContentType.JAVASCRIPT) {
                return Prism.highlight(this.model.body.content, Prism.languages.javascript, 'javascript');
            }
            if (contentType == HttpContentType.HTML) {
                return Prism.highlight(this.model.body.content, Prism.languages.html, 'html');
            }
        }

        return this.model.body.content
    }

    isFormattedContent(): boolean {
        let contentType = this.getContentType();
        return contentType == HttpContentType.JSON
            || contentType == HttpContentType.XML || contentType == HttpContentType.XML_TEXT
            || contentType == HttpContentType.JAVASCRIPT
            || contentType == HttpContentType.HTML;
    }

    private getContentType(): HttpContentType {
        let contentType = this.model.getContentType();
        if(!contentType) {
            return HttpContentType.TEXT;
        }

        return contentType;
    }
}
