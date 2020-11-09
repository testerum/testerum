import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit, ViewChild} from '@angular/core';
import {RdbmsService} from "../../../../../service/resources/rdbms/rdbms.service";
import {RdbmsConnectionConfig} from "../../../../../model/resource/rdbms/rdbms-connection-config.model";
import {RdbmsDriver} from "./model/rdbms-driver.model";
import {ActivatedRoute} from "@angular/router";
import {SchemaChooserModalListener} from "./schema_chooser_modal/schema-chooser-modal.listener";
import {ResourceComponent} from "../../resource-component.interface";
import {NgForm} from "@angular/forms";
import {ParamStepPatternPart} from "../../../../../model/text/parts/param-step-pattern-part.model";
import {InfoModalService} from "../../../../../generic/components/info_modal/info-modal.service";
import {SchemaChooserModalService} from "./schema_chooser_modal/schema-chooser-modal.service";
import {ResourceContextActions} from "../../infrastructure/model/resource-context-actions.model";

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
    moduleId: module.id,
    selector: 'db-connection',
    templateUrl: 'rdbms-connection-config.component.html',
    styleUrls: [
        'rdbms-connection-config.component.scss',
        '../../resource-editor.scss'
    ]
})
export class RdbmsConnectionConfigComponent extends ResourceComponent<RdbmsConnectionConfig> implements OnInit, SchemaChooserModalListener {

    @Input() name: string;
    @Input() model:RdbmsConnectionConfig;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() editMode: boolean = false;
    @Input() condensedViewMode: boolean = false;
    @Input() isSharedResource: boolean = false;
    @Input() contextActions: ResourceContextActions = new class implements ResourceContextActions {
        cancel() {}
        save() {}
    };

    @ViewChild(NgForm) form: NgForm;

    drivers: Array<RdbmsDriver> = [];
    selectedDriver: RdbmsDriver;

    constructor(private cd: ChangeDetectorRef,
                private route: ActivatedRoute,
                private dbConnectionService: RdbmsService,
                private infoModalService: InfoModalService,
                private schemaChooserModalService: SchemaChooserModalService) { super()
    }

    ngOnInit() {
        if (this.model == null) {
            this.model = new RdbmsConnectionConfig();
        }

        this.loadDrivers();

        if (this.model && this.drivers) {
            this.setSelectedDriver(this.model.driverName);
        }
    }

    refresh() {
        if (!this.cd['destroyed']) { //without this the folowing error will appear: "ERROR Error: ViewDestroyedError: Attempt to use a destroyed view: detectChanges"
            this.loadDrivers();
            this.cd.detectChanges();
        }
    }

    loadDrivers() {
        if (this.drivers.length == 0) {
            this.dbConnectionService.getDrivers().subscribe(
                items => {
                    this.drivers = items;
                    if (this.model) {
                        this.setSelectedDriver(this.model.driverName)
                    }
                    this.refresh();
                }
            );
        }
    }

    private maskedPassword(): string {
        let result = "";

        if (!this.model.password) {
            return result;
        }

        for (let i = 0; i < this.model.password.length; i++) {
            result += "*";
        }
        return result;
    }

    private setSelectedDriver(driverName: string): void {
        if (!driverName || !this.drivers) {
            return;
        }

        this.drivers.forEach(
            item => {
                if (item.name == driverName) {
                    this.selectedDriver = item;
                    return;
                }
            }
        )
    }

    canCallPing(): boolean {
        if (this.model.host && this.model.port) {
            return true
        }
        return false;
    }

    ping(): void {
        if (!this.canCallPing()) {
            return;
        }

        this.dbConnectionService
            .ping(this.model.host, this.model.port)
            .subscribe(
                pingSuccessful => {
                    this.infoModalService.showInfoModal (
                        "Ping Results",
                        pingSuccessful ? "Ping Successful" : "No Ping Response"
                    )
                }
            )
    }

    showSchemasChooser(): void {
        this.setSelectedDriverInModel();

        this.dbConnectionService
            .showSchemasChooser(this.model)
            .subscribe(
                dbSchema => {
                    this.schemaChooserModalService.showSchemaChooserModal(
                        dbSchema.schemas,
                        this
                    )
                }
            )
    }

    schemaChooserEventListener(chosenSchema: string): void {
        this.model.database = chosenSchema;
        this.refresh();
    }

    useCustomUrl(value: boolean): void {
        this.model.useCustomUrl = value;
    }

    private setSelectedDriverInModel() {
        if(this.selectedDriver) {
            this.model.driverName = this.selectedDriver.name;
            this.model.driverJar = this.selectedDriver.driverJar;
            this.model.driverClass = this.selectedDriver.driverClass;
            this.model.driverUrlPattern = this.selectedDriver.urlPattern;
        } else {
            this.model.driverName = null;
            this.model.driverJar = null;
            this.model.driverClass = null;
            this.model.driverUrlPattern = null;
        }
    }

    isFormValid(): boolean {
        return this.form.valid;
    }

    getForm(): NgForm {
        return this.form;
    }
}
