import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Input,
    OnDestroy,
    OnInit,
    ViewChild
} from '@angular/core';
import {RdbmsService} from "../../../../../service/resources/rdbms/rdbms.service";
import {RdbmsConnectionConfig} from "../../../../../model/resource/rdbms/rdbms-connection-config.model";
import {RdbmsDriver} from "./model/rdbms-driver.model";
import {ActivatedRoute} from "@angular/router";
import {InfoModalComponent} from "../../../../../generic/components/info_modal/info-modal.component";
import {SchemaChooserModalComponent} from "./schema_chooser_modal/schema-chooser-modal.component";
import {SchemaChooserModalListener} from "./schema_chooser_modal/schema-chooser-modal.listener";
import {ResourceComponent} from "../../resource-component.interface";
import {NgForm} from "@angular/forms";
import {ParamStepPatternPart} from "../../../../../model/text/parts/param-step-pattern-part.model";

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

    @ViewChild(NgForm) form: NgForm;

    @ViewChild(InfoModalComponent) infoModalComponent: InfoModalComponent;
    @ViewChild(SchemaChooserModalComponent) schemaChooserModalComponent: SchemaChooserModalComponent;

    drivers: Array<RdbmsDriver> = [];
    selectedDriver: RdbmsDriver;

    constructor(private cd: ChangeDetectorRef,
                private route: ActivatedRoute,
                private dbConnectionService: RdbmsService) { super()
    }

    ngOnInit() {
        if (this.model == null) {
            this.model = new RdbmsConnectionConfig();
        }

        this.dbConnectionService.getDrivers().subscribe(
            items => {
                this.drivers = items;
                if (this.model) {
                    this.setSelectedDriver(this.model.driverName)
                }
                this.refresh();
            }
        );

        if (this.model && this.drivers) {
            this.setSelectedDriver(this.model.driverName);
        }
    }

    refresh() {
        if (!this.cd['destroyed']) { //without this the folowing error will appear: "ERROR Error: ViewDestroyedError: Attempt to use a destroyed view: detectChanges"
            this.cd.detectChanges();
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
        this.dbConnectionService
            .ping(this.model.host, this.model.port)
            .subscribe(
                pingSuccessful => {
                    this.infoModalComponent.show(
                        "Ping Results",
                        pingSuccessful ? "Ping Successful" : "No Ping Response",
                        null,
                        null
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
                    if (dbSchema.errorMessage) {
                        this.infoModalComponent.show(
                            "Database Results",
                            dbSchema.errorMessage,
                            null,
                            null
                        )
                    } else {
                        this.schemaChooserModalComponent.show(
                            dbSchema.schemas,
                            this
                        )
                    }

                }
            )
    }

    schemaChooserEventListener(chosenSchema: string): void {
        this.model.database = chosenSchema;
    }

    useCustomUrl(value: boolean): void {
        this.model.useCustomUrl = value;
    }

    private setSelectedDriverInModel() {
        this.model.driverName = this.selectedDriver.name;
        this.model.driverJar = this.selectedDriver.driverJar;
        this.model.driverClass = this.selectedDriver.driverClass;
        this.model.driverUrlPattern = this.selectedDriver.urlPattern;
    }

    isFormValid(): boolean {
        return this.form.valid;
    }

    getForm(): NgForm {
        return this.form;
    }
}
