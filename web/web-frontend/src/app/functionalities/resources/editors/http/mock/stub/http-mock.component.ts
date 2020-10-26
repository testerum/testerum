import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {ResourceService} from "../../../../../../service/resources/resource.service";
import {ResourcesTreeService} from "../../../../tree/resources-tree.service";
import {HttpMockService} from "./http-mock.service";
import {NgForm} from "@angular/forms";
import {HttpMock} from "./model/http-mock.model";
import {ResourceComponent} from "../../../resource-component.interface";
import {HttpMockResponseType} from "./model/enums/http-mock-response-type.enum";
import {ParamStepPatternPart} from "../../../../../../model/text/parts/param-step-pattern-part.model";
import {ResourceContextActions} from "../../../infrastructure/model/resource-context-actions.model";

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
    moduleId: module.id,
    selector: 'http-mock',
    templateUrl: 'http-mock.component.html',
    styleUrls: [
        'http-mock.component.scss',
        '../../../resource-editor.scss'
    ]
})
export class HttpMockComponent extends ResourceComponent<HttpMock> implements OnInit {

    @Input() name: string;
    @Input() model:HttpMock;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() private _editMode: boolean = false;
    @Input() condensedViewMode: boolean = false;
    @Input() isSharedResource: boolean = false;
    @Input() contextActions: ResourceContextActions = new class implements ResourceContextActions {
        cancel() {}
        save() {}
    };

    @ViewChild(NgForm) form: NgForm;

    HttpMockResponseType = HttpMockResponseType;

    constructor(private cd: ChangeDetectorRef,
                private route: ActivatedRoute,
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
        if(this.httpMockService) this.httpMockService.setEditMode(value)
    }

    isFormValid(): boolean {
        return this.form.valid && !this.model.isEmpty();
    }

    getForm(): NgForm {
        return this.form;
    }
}
