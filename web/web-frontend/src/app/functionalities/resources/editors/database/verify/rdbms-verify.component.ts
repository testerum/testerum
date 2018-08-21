import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {ResourceService} from "../../../../../service/resources/resource.service";
import {ResourcesTreeService} from "../../../tree/resources-tree.service";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {RdbmsConnectionResourceType} from "../../../tree/model/type/rdbms-connection.resource-type.model";
import {RdbmsService} from "../../../../../service/resources/rdbms/rdbms.service";
import {RdbmsVerifyTreeService} from "./rdbms-verify-tree/rdbms-verify-tree.service";
import {SchemaVerify} from "./rdbms-verify-tree/model/schema-verify.model";
import {ResourceComponent} from "../../resource-component.interface";
import {NgForm} from "@angular/forms";
import {ParamStepPatternPart} from "../../../../../model/text/parts/param-step-pattern-part.model";

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
    moduleId: module.id,
    selector: 'rdbms-verify',
    templateUrl: 'rdbms-verify.component.html',
    styleUrls: [
        'rdbms-verify.component.scss',
        '../../resource-editor.scss'
    ]
})
export class RdbmsVerifyComponent extends ResourceComponent<SchemaVerify> implements OnInit {

    @Input() name: string;
    @Input() model: SchemaVerify;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() private _editMode: boolean = false;
    @Input() condensedViewMode: boolean = false;
    @Input() isSharedResource: boolean = false;

    @ViewChild(NgForm) form: NgForm;

    selectedRdbmsConnection: Path;
    availableRdbmsConnections: Array<Path> = [];

    constructor(private cd: ChangeDetectorRef,
                private route: ActivatedRoute,
                private resourceService: ResourceService,
                private rdbmsService: RdbmsService,
                private resourcesTreeService: ResourcesTreeService,
                private rdbmsVerifyTreeService: RdbmsVerifyTreeService) {
        super()
    }

    ngOnInit() {
        if (this.model == null) {
            this.model = new SchemaVerify();
        }
        this.rdbmsVerifyTreeService.editMode = this._editMode;
        this.rdbmsVerifyTreeService.setSchemaVerifyResource(this.model);
        this.initAvailableRdbmsConnections();
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
        if(this.rdbmsVerifyTreeService) this.rdbmsVerifyTreeService.editMode = this._editMode;
    }

    isFormValid(): boolean {
        return this.form.valid;
    }

    getForm(): NgForm {
        return this.form;
    }

    private initAvailableRdbmsConnections() {
        this.selectedRdbmsConnection = null;
        this.resourceService.getResourcePaths(RdbmsConnectionResourceType.getInstanceForRoot().fileExtension).subscribe(
            connectionsPath => {
                this.availableRdbmsConnections.length = 0;
                connectionsPath.forEach(connectionPath => {
                    this.availableRdbmsConnections.push(connectionPath)
                });
            }
        );
    }

    onSelectedRdbmsConnectionChanged(value: string) {
        this.selectedRdbmsConnection = value ? Path.createInstance(value): null;
        this.initialiseVerifyTreeFromSelectedRdbmsConnection();
    }

    private initialiseVerifyTreeFromSelectedRdbmsConnection() {
        this.rdbmsVerifyTreeService.empty();
        if (this.selectedRdbmsConnection) {
            this.rdbmsService.getSchema(this.selectedRdbmsConnection).subscribe(schema => {
                this.rdbmsVerifyTreeService.setRdbmsSchema(schema)
            })
        }
    }
}
