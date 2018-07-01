import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {ResourceService} from "../../../../../../service/resources/resource.service";
import {ResourcesTreeService} from "../../../../tree/resources-tree.service";
import {HttpMockService} from "./http-mock.service";
import {NgForm} from "@angular/forms";
import {HttpMock} from "./model/http-mock.model";
import {ResourceComponent} from "../../../resource-component.interface";
import {HttpMockResponseType} from "./model/enums/http-mock-response-type.enum";
import {ParamStepPatternPart} from "../../../../../../model/text/parts/param-step-pattern-part.model";

@Component({
    moduleId: module.id,
    selector: 'http-mock',
    templateUrl: 'http-mock.component.html',
    styleUrls: [
        'http-mock.component.css',
        '../../../resource-editor.css',
        '../../../../../../generic/css/generic.css',
        '../../../../../../generic/css/forms.css'
    ]
})
export class HttpMockComponent extends ResourceComponent<HttpMock> implements OnInit {

    @Input() name: string;
    @Input() model:HttpMock;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() private _editMode: boolean = false;
    @Input() condensedViewMode: boolean = false;

    @ViewChild(NgForm) form: NgForm;

    HttpMockResponseType = HttpMockResponseType;

    constructor(private route: ActivatedRoute,
                private resourceService: ResourceService,
                private resourcesTreeService: ResourcesTreeService,
                private httpMockService: HttpMockService) {
        super()
    }

    ngOnInit() {
        if (this.model == null) {
            this.model = new HttpMock();
        }
        this.httpMockService.setModel(this.model);
    }

    get editMode(): boolean {
        return this._editMode;
    }

    set editMode(value: boolean) {
        this._editMode = value;
        if(this.httpMockService) this.httpMockService.setEditMode(value)
    }

    isFormValid(): boolean {
        return this.form.valid;
    }

    getForm(): NgForm {
        return this.form;
    }
}
