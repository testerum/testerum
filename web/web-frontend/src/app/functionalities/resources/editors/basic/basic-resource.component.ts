import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit, ViewChild} from '@angular/core';
import {NgForm} from "@angular/forms";
import {BasicResource} from "../../../../model/resource/basic/basic-resource.model";
import {ResourceComponent} from "../resource-component.interface";
import {ParamStepPatternPart} from "../../../../model/text/parts/param-step-pattern-part.model";
import {NumberTypeMeta} from "../../../../model/text/parts/param-meta/number-type.meta";
import {BooleanTypeMeta} from "../../../../model/text/parts/param-meta/boolean-type.meta";
import {EnumTypeMeta} from "../../../../model/text/parts/param-meta/enum-type.meta";
import {DateTypeMeta} from "../../../../model/text/parts/param-meta/date-type-meta.model";
import {InstantTypeMeta} from "../../../../model/text/parts/param-meta/instant-type-meta.model";
import {LocalDateTimeTypeMeta} from "../../../../model/text/parts/param-meta/local-date-time-type-meta.model";
import {LocalDateTypeMeta} from "../../../../model/text/parts/param-meta/local-date-type-meta.model";
import {ZonedDateTimeTypeMeta} from "../../../../model/text/parts/param-meta/zoned-date-time-type-meta.model";

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
    moduleId: module.id,
    selector: 'basic-resource',
    templateUrl: 'basic-resource.component.html',
    styleUrls: [
        'basic-resource.component.scss',
        '../resource-editor.scss'
    ]
})
export class BasicResourceComponent extends ResourceComponent<BasicResource> implements OnInit {

    @Input() name: string;
    @Input() model:BasicResource;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() editMode: boolean = false;
    @Input() condensedViewMode: boolean = false;
    @Input() contextActions: ResourceContextActions = new class implements ResourceContextActions {
        cancel() {}
        save() {
        }
    };

    @ViewChild(NgForm, { static: false }) form: NgForm;

    editNameMode: boolean = false;

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

    setEditMode(isEditMode: boolean): void {
        this.editMode = isEditMode;
    }

    isFormValid(): boolean {
        return this.form.valid;
    }

    getForm(): NgForm {
        return this.form;
    }

    onEditName() {
        this.editNameMode = true;
    }

    onRemoveCustomName() {
        this.name = this.stepParameter.name;
    }

    onBeforeSave(): void {
        if (!this.name || this.name == this.stepParameter.name) {
            this.name = null;
        }
    }

    isBooleanResource(): boolean {
        return this.stepParameter.serverType instanceof BooleanTypeMeta
    }

    isEnumResource():boolean {
        return this.stepParameter.serverType instanceof EnumTypeMeta
    }

    isDateResource():boolean {
        return this.stepParameter.serverType instanceof DateTypeMeta ||
            this.stepParameter.serverType instanceof InstantTypeMeta ||
            this.stepParameter.serverType instanceof LocalDateTimeTypeMeta ||
            this.stepParameter.serverType instanceof LocalDateTypeMeta ||
            this.stepParameter.serverType instanceof ZonedDateTimeTypeMeta
    }

    isStringResource(): boolean {
        return !this.isBooleanResource() &&
            !this.isEnumResource() &&
            !this.isDateResource();
    }
}
