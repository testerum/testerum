import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit, ViewChild} from '@angular/core';
import {ResourceComponent} from "../../resource-component.interface";
import {ParamStepPatternPart} from "../../../../../model/text/parts/param-step-pattern-part.model";
import {NgForm} from "@angular/forms";
import {BasicResource} from "../../../../../model/resource/basic/basic-resource.model";
import * as Prism from 'prismjs';
import 'prismjs/components/prism-json';
import {ResourceContextActions} from "../../infrastructure/model/resource-context-actions.model";

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
    moduleId: module.id,
    selector: 'json-resource',
    templateUrl: 'json-resources.component.html',
    styleUrls: [
        'json-resources.component.scss'
    ]
})
export class JsonResourceComponent extends ResourceComponent<BasicResource> implements OnInit {

    @Input() name: string;
    @Input() model: BasicResource;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() editMode: boolean = false;
    @Input() condensedViewMode: boolean = false;
    @Input() isSharedResource: boolean = false;
    @Input() contextActions: ResourceContextActions = new class implements ResourceContextActions {
        cancel() {}
        save() {}
    };

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

    onChange(code: string) {
        this.model.content = code;
        this.refresh();
    }

    isFormValid(): boolean {
        return this.form ? this.form.valid : true;
    }

    getForm(): NgForm {
        return this.form;
    }

    getHighlightedJson(): string {
        return Prism.highlight(this.model.content, Prism.languages.json, 'json');
    }
}
