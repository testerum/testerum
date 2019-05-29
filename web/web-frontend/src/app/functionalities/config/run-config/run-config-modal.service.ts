import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {AppComponent} from "../../../app.component";
import {RunConfigModalComponent} from "./run-config-modal.component";
import {RunConfig} from "./model/runner-config.model";
import {RunConfigService} from "./run-config.service";
import {Setting} from "../settings/model/setting.model";
import {SettingsService} from "../../../service/settings.service";

@Injectable()
export class RunConfigModalService {

    instance: RunConfigModalComponent;


    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private runnerConfigService: RunConfigService,
                private settingsService: SettingsService) {
    }

    showRunnersModal(runners: Array<RunConfig>) {
        if (this.instance && this.instance.modal.isShown) {
            return null;
        }
        this.settingsService.getSettings().subscribe(
            (settings: Array<Setting>) => {
                this.runnerConfigService.setRunners(runners);
                this.runnerConfigService.settings = settings;

                const factory = this.componentFactoryResolver.resolveComponentFactory(RunConfigModalComponent);
                let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
                this.instance = modalComponentRef.instance;

                this.instance.modalComponentRef = modalComponentRef;
            }
        );
    }
}
