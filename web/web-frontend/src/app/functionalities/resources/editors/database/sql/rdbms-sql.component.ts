import {Component, OnInit, ViewChild, Input} from '@angular/core';
import {NgForm} from "@angular/forms";
import {ResourceComponent} from "../../resource-component.interface";
import {BasicResource} from "../../../../../model/resource/basic/basic-resource.model";
import {ParamStepPatternPart} from "../../../../../model/text/parts/param-step-pattern-part.model";

@Component({
    moduleId: module.id,
    selector: 'rdbms-sql',
    templateUrl: 'rdbms-sql.component.html',
    styleUrls: [
        'rdbms-sql.component.css',
        '../../resource-editor.css',
        '../../../../../generic/css/generic.css',
        '../../../../../generic/css/forms.css'
    ]
})

export class RdbmsSqlComponent extends ResourceComponent<BasicResource> implements OnInit {

    @Input() name: string;
    @Input() model: BasicResource;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() editMode: boolean = false;
    @Input() condensedViewMode: boolean = false;

    @ViewChild(NgForm) form: NgForm;

    ngOnInit() {
        if (this.model == null) {
            this.model = new BasicResource();
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
