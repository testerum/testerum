import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {SettingsService} from "../../../service/settings.service";
import {Setting} from "./model/setting.model";
import {SettingsModalComponent} from "./settings-modal.component";
import {AppComponent} from "../../../app.component";

@Injectable()
export class SettingsModalService {

    instance: SettingsModalComponent;

    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private settingsService: SettingsService) {
    }

    showSettingsModal() {
        this.settingsService.getSettings().subscribe(
            (settings: Array<Setting>) => {
                if (this.instance && this.instance.modal.isShown) {
                    return;
                }

                const factory = this.componentFactoryResolver.resolveComponentFactory(SettingsModalComponent);
                let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
                this.instance = modalComponentRef.instance;

                this.instance.init(settings);
                this.instance.modalComponentRef = modalComponentRef;
            }
        );
    }
}
