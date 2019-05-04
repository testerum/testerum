import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {SettingsService} from "../../../service/settings.service";
import {Setting} from "./model/setting.model";
import {SettingsModalComponent} from "./settings-modal.component";
import {AppComponent} from "../../../app.component";

@Injectable()
export class SettingsModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private settingsService: SettingsService) {
    }

    showSettingsModal() {
        this.settingsService.getSettings().subscribe(
            (settings: Array<Setting>) => {
                const factory = this.componentFactoryResolver.resolveComponentFactory(SettingsModalComponent);
                let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
                let modalInstance: SettingsModalComponent = modalComponentRef.instance;

                modalInstance.init(settings);
                modalInstance.modalComponentRef = modalComponentRef;
            }
        );
    }
}
