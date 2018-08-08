import {ChangeDetectorRef, Component, Input, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ResourceService} from "../../../../service/resources/resource.service";
import {ResourcesTreeService} from "../../tree/resources-tree.service";
import {JsonVerifyTreeService} from "./json-verify-tree/json-verify-tree.service";
import {ArrayJsonVerify} from "./json-verify-tree/model/array-json-verify.model";
import {UrlService} from "../../../../service/url.service";
import {ResourceComponent} from "../resource-component.interface";
import {ParamStepPatternPart} from "../../../../model/text/parts/param-step-pattern-part.model";
import {NgForm} from "@angular/forms";

@Component({
    moduleId: module.id,
    selector: 'json-verify',
    templateUrl: 'json-verify.component.html',
    styleUrls: [
        'json-verify.component.scss'
    ]
})
export class JsonVerifyComponent extends ResourceComponent<ArrayJsonVerify> implements OnInit {

    @Input() name: string;
    @Input() model:ArrayJsonVerify;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() editMode: boolean = true;
    @Input() condensedViewMode: boolean = false;
    @Input() isSharedResource: boolean = false;

    @ViewChild(NgForm) form: NgForm;

    sampleJsonText: string;

    constructor(private cd: ChangeDetectorRef,
                private router: Router,
                private route: ActivatedRoute,
                private urlService: UrlService,
                private resourceService: ResourceService,
                private resourcesTreeService: ResourcesTreeService,
                private jsonVerifyTreeService: JsonVerifyTreeService) {
        super()
    }

    ngOnInit() {
        if (this.model == null) {
            this.model = new ArrayJsonVerify(null);
        }

        this.jsonVerifyTreeService.setJsonVerifyRootResource(this.model);
        this.jsonVerifyTreeService.editMode = this.editMode;
    }

    refresh() {
        this.cd.detectChanges();
    }

    isEmptyModel(): boolean {
        return this.jsonVerifyTreeService.isEmptyModel();
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

    setEditMode(isEditMode: boolean): void {
        this.editMode = isEditMode;
        this.jsonVerifyTreeService.editMode = isEditMode;
    }
}
