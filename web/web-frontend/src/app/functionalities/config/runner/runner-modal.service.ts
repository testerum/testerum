import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {AppComponent} from "../../../app.component";
import {RunnerModalComponent} from "./runner-modal.component";
import {RunnerConfig} from "./model/runner-config.model";
import {RunnerConfigService} from "./runner-config.service";
import {Setting} from "../settings/model/setting.model";
import {SettingsService} from "../../../service/settings.service";

@Injectable()
export class RunnerModalService {

    instance: RunnerModalComponent;


    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private runnerConfigService: RunnerConfigService,
                private settingsService: SettingsService) {
    }

    showRunnersModal(runners: Array<RunnerConfig>) {
        if (this.instance && this.instance.modal.isShown) {
            return null;
        }
        this.settingsService.getSettings().subscribe(
            (settings: Array<Setting>) => {
                this.runnerConfigService.setRunners(runners);
                this.runnerConfigService.settings = settings;

                const factory = this.componentFactoryResolver.resolveComponentFactory(RunnerModalComponent);
                let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
                this.instance = modalComponentRef.instance;

                this.instance.modalComponentRef = modalComponentRef;
            }
        );
    }
}
