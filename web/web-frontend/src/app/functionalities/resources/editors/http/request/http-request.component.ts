import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
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

@Component({
    moduleId: module.id,
    selector: 'http-request',
    templateUrl: 'http-request.component.html',
    styleUrls: [
        'http-request.component.css',
        '../../resource-editor.css',
        '../../../../../generic/css/generic.css',
        '../../../../../generic/css/forms.css'
    ]
})
export class HttpRequestComponent extends ResourceComponent<HttpRequest> implements OnInit {


    @Input() name: string;
    @Input() model:HttpRequest;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() private _editMode: boolean = false;
    @Input() condensedViewMode: boolean = false;

    @ViewChild(NgForm) form: NgForm;

    partToDisplay: HttpPart = HttpPart.HEADERS;
    HttpPart = HttpPart;
    HttpMethod = HttpMethod;

    @ViewChild("httpParams") httpParams: HttpParamsComponent;

    constructor(private router: Router,
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
        this.initPartToDisplay(this.model);
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

    hasResponseToDisplay(): boolean {
        return this.httpRequestService.httpResponse == null;
    }

    closeResponseTab(): void {
        if (this.partToDisplay == this.HttpPart.RESPONSE) {
            this.partToDisplay = HttpPart.HEADERS;
        }
        this.httpRequestService.httpResponse = null;
    }

    isFormValid(): boolean {
        return this.form.valid;
    }

    getForm(): NgForm {
        return this.form;
    }
}
