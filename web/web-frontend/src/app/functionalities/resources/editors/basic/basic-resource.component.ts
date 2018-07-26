import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit, ViewChild} from '@angular/core';
import {NgForm} from "@angular/forms";
import {BasicResource} from "../../../../model/resource/basic/basic-resource.model";
import {ResourceComponent} from "../resource-component.interface";
import {ParamStepPatternPart} from "../../../../model/text/parts/param-step-pattern-part.model";

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
    moduleId: module.id,
    selector: 'basic-resource',
    templateUrl: 'basic-resource.component.html',
    styleUrls: [
        'basic-resource.component.scss',
        '../resource-editor.scss',
        '../../../../generic/css/generic.scss',
        '../../../../generic/css/forms.scss'
    ]
})
export class BasicResourceComponent extends ResourceComponent<BasicResource> implements OnInit {

    @Input() name: string;
    @Input() model:BasicResource;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() editMode: boolean = false;
    @Input() condensedViewMode: boolean = false;

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
