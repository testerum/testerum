import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit, ViewChild} from '@angular/core';
import {HttpMockServer} from "./model/http-mock-server.model";
import {NgForm} from "@angular/forms";
import {ResourceComponent} from "../../../resource-component.interface";
import {ParamStepPatternPart} from "../../../../../../model/text/parts/param-step-pattern-part.model";

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
    moduleId: module.id,
    selector: 'http-mock-server',
    templateUrl: 'http-mock-server.component.html',
    styleUrls: [
        'http-mock-server.component.scss',
        '../../../resource-editor.scss'
    ]
})
export class HttpMockServerComponent extends ResourceComponent<HttpMockServer> implements OnInit {

    @Input() name: string;
    @Input() model:HttpMockServer;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() editMode: boolean = false;
    @Input() condensedViewMode: boolean = false;
    @Input() isSharedResource: boolean = false;

    @ViewChild(NgForm) form: NgForm;

    constructor(private cd: ChangeDetectorRef) {
        super();
    }

    ngOnInit() {
        if (this.model == null) {
            this.model = new HttpMockServer();
        }
    }

    refresh() {
        this.cd.detectChanges();
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
