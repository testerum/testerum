import {ChangeDetectorRef, Component, Input, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {ResourceComponent} from "../../resource-component.interface";
import {ParamStepPatternPart} from "../../../../../model/text/parts/param-step-pattern-part.model";
import {NgForm} from "@angular/forms";
import {JsonUtil} from '../../../../../utils/json.util';
import {JsonVerify} from "../../../../../generic/components/json-verify/model/json-verify.model";

@Component({
    moduleId: module.id,
    selector: 'json-verify-resource',
    templateUrl: 'json-verify-resources.component.html',
    styleUrls: [
        'json-verify-resources.component.scss'
    ],
    encapsulation: ViewEncapsulation.None
})
export class JsonVerifyResourceComponent extends ResourceComponent<JsonVerify> implements OnInit {

    @Input() name: string;
    @Input() model: JsonVerify;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() editMode: boolean = true;
    @Input() condensedViewMode: boolean = false;
    @Input() isSharedResource: boolean = false;
    @Input() contextActions: ResourceContextActions = new class implements ResourceContextActions {
        cancel() {}
        save() {}
    };

    @ViewChild(NgForm) form: NgForm;

    sampleJsonText: string;

    isTreeVisible: boolean = false;
    isValidJson: boolean = true;

    constructor(private cd: ChangeDetectorRef) {
        super()
    }

    ngOnInit() {
        if (this.model == null) {
            this.model = new JsonVerify();
        }
    }

    onToggleEditorMode() {
        this.isTreeVisible = !this.isTreeVisible;
    }

    refresh() {
        if (!this.cd['destroyed']) { //without this the folowing error will appear: "ERROR Error: ViewDestroyedError: Attempt to use a destroyed view: detectChanges"
            this.cd.detectChanges();
        }
    }

    isEmptyModel(): boolean {
        return this.model.isEmpty();
    }

    shouldDisplayJsonSample(): boolean {
        return this.editMode && (this.isEmptyModel() || this.sampleJsonText != null);
    }

    isFormValid(): boolean {
        return this.form.valid;
    }

    getForm(): NgForm {
        return this.form;
    }

    onTextChange(jsonAsString: string) {
        this.isValidJson = JsonUtil.isJson(jsonAsString);
    }
}
