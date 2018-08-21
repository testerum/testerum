import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit, ViewChild} from '@angular/core';
import {NgForm} from "@angular/forms";
import {ResourceComponent} from "../../resource-component.interface";
import {BasicResource} from "../../../../../model/resource/basic/basic-resource.model";
import {ParamStepPatternPart} from "../../../../../model/text/parts/param-step-pattern-part.model";

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
    moduleId: module.id,
    selector: 'rdbms-sql',
    templateUrl: 'rdbms-sql.component.html',
    styleUrls: [
        'rdbms-sql.component.scss',
        '../../resource-editor.scss'
    ]
})

export class RdbmsSqlComponent extends ResourceComponent<BasicResource> implements OnInit {

    @Input() name: string;
    @Input() model: BasicResource;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() editMode: boolean = false;
    @Input() condensedViewMode: boolean = false;
    @Input() isSharedResource: boolean = false;

    @ViewChild(NgForm) form: NgForm;

    constructor(private cd: ChangeDetectorRef){
        super();
    }

    ngOnInit() {
        if (this.model == null) {
            this.model = new BasicResource();
        }
    }

    refresh() {
        if (!this.cd['destroyed']) { //without this the folowing error will appear: "ERROR Error: ViewDestroyedError: Attempt to use a destroyed view: detectChanges"
            this.cd.detectChanges();
        }
    }

    isFormValid(): boolean {
        return this.form.valid;
    }

    getForm(): NgForm {
        return this.form;
    }

    sqlChange(sql: string) {
        this.model.content = sql;
    }
}
