import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {ResourceService} from "../../../../../service/resources/resource.service";
import {ResourcesTreeService} from "../../../tree/resources-tree.service";
import {HttpResponseVerifyService} from "./http-response-verify.service";
import {HttpResponseVerify} from "./model/http-response-verify.model";
import {HttpResponseStatusCode} from "../../../../../model/resource/http/enum/http-response-status-code.enum";
import {ResourceComponent} from "../../resource-component.interface";
import {NgForm} from "@angular/forms";
import {ParamStepPatternPart} from "../../../../../model/text/parts/param-step-pattern-part.model";
import {HttpResponseVerifyBodyComponent} from "./body/http-response-verify-body.component";

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
    moduleId: module.id,
    selector: 'http-response-verify',
    templateUrl: 'http-response-verify.component.html',
    styleUrls: [
        'http-response-verify.component.scss',
        '../../resource-editor.scss'
    ]
})
export class HttpResponseVerifyComponent extends ResourceComponent<HttpResponseVerify> implements OnInit {

    @Input() name: string;
    @Input() model: HttpResponseVerify;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() private _editMode: boolean = false;
    @Input() condensedViewMode: boolean = false;
    @Input() isSharedResource: boolean = false;
    @Input() contextActions: ResourceContextActions = new class implements ResourceContextActions {
        cancel() {}
        save() {}
    };

    @ViewChild(NgForm) form: NgForm;
    @ViewChild(HttpResponseVerifyBodyComponent) httpResponseVerifyBodyComponent: HttpResponseVerifyBodyComponent;

    constructor(private cd: ChangeDetectorRef,
                private route: ActivatedRoute,
                private resourceService: ResourceService,
                private resourcesTreeService: ResourcesTreeService,
                private httpResponseVerifyService: HttpResponseVerifyService) {
        super()
    }

    ngOnInit() {
        if (this.model == null) {
            this.model = new HttpResponseVerify();
        }
        this.httpResponseVerifyService.setHttpResponseVerify(this.model);
    }

    refresh() {
        if (!this.cd['destroyed']) { //without this the folowing error will appear: "ERROR Error: ViewDestroyedError: Attempt to use a destroyed view: detectChanges"
            this.cd.detectChanges();
        }
    }

    get editMode(): boolean {
        return this._editMode;
    }

    set editMode(value: boolean) {
        this._editMode = value;
        if(this.httpResponseVerifyService) this.httpResponseVerifyService.setEditMode(value)
    }

    isFormValid(): boolean {
        return this.form.valid;
    }

    getForm(): NgForm {
        return this.form;
    }

    getStatusCodeText(): string {
        let httpResponseStatusCode = HttpResponseStatusCode.fromStatusCodeNumber(
            this.model.expectedStatusCode
        );

        if (httpResponseStatusCode != null) {
            return httpResponseStatusCode.toString()
        }
        return "";
    }

    onBeforeSave(): void {
        this.httpResponseVerifyBodyComponent.onBeforeSave()
    }
}
