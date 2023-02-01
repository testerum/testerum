import {ComponentFactoryResolver, EventEmitter, Injectable} from "@angular/core";
import {AppComponent} from "../../../app.component";
import {RunConfigModalComponent} from "./run-config-modal.component";
import {RunConfig} from "./model/runner-config.model";
import {RunConfigComponentService} from "./run-config-component.service";
import {Setting} from "../settings/model/setting.model";
import {SettingsService} from "../../../service/settings.service";
import {Observable} from "rxjs";

@Injectable()
export class RunConfigModalService {

    instance: RunConfigModalComponent;

    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private runConfigComponentService: RunConfigComponentService,
                private settingsService: SettingsService) {
    }

    showRunnersModal(runners: Array<RunConfig>): Observable<RunConfig[]> {
        if (this.instance && this.instance.modal.isShown) {
            return null;
        }
        this.settingsService.getSettings().subscribe(
            (settings: Array<Setting>) => {
                this.runConfigComponentService.setRunners(runners);
                this.runConfigComponentService.settings = settings;

                const factory = this.componentFactoryResolver.resolveComponentFactory(RunConfigModalComponent);
                let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
                this.instance = modalComponentRef.instance;

                this.instance.modalComponentRef = modalComponentRef;
            }
        );

        this.runConfigComponentService.savedRunConfigsEventEmitter = new EventEmitter<Array<RunConfig>>();
        return this.runConfigComponentService.savedRunConfigsEventEmitter;
    }
}
