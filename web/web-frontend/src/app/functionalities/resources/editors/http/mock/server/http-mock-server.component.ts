import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {HttpMockServer} from "./model/http-mock-server.model";
import {NgForm} from "@angular/forms";
import {ResourceComponent} from "../../../resource-component.interface";
import {ParamStepPatternPart} from "../../../../../../model/text/parts/param-step-pattern-part.model";

@Component({
    moduleId: module.id,
    selector: 'http-mock-server',
    templateUrl: 'http-mock-server.component.html',
    styleUrls: [
        'http-mock-server.component.css',
        '../../../resource-editor.css',
        '../../../../../../generic/css/generic.css',
        '../../../../../../generic/css/forms.css'
    ]
})
export class HttpMockServerComponent extends ResourceComponent<HttpMockServer> implements OnInit {

    @Input() name: string;
    @Input() model:HttpMockServer;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() editMode: boolean = false;
    @Input() condensedViewMode: boolean = false;

    @ViewChild(NgForm) form: NgForm;

    ngOnInit() {
        if (this.model == null) {
            this.model = new HttpMockServer();
        }
    }

    setEditMode(isEditMode: boolean): void {
        this.editMode = isEditMode;
    }

    isFormValid(): boolean {
        return this.form.valid;
    }

    getForm(): NgForm {
        return this.form;
    }
}
