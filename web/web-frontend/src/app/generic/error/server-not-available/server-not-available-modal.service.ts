import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {AppComponent} from "../../../app.component";
import {ServerNotAvailableModalComponent} from "./server-not-available-modal.component";

@Injectable()
export class ServerNotAvailableModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    show(shouldRefreshWhenServerIsBack: boolean = false) {
        const factory = this.componentFactoryResolver.resolveComponentFactory(ServerNotAvailableModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: ServerNotAvailableModalComponent = modalComponentRef.instance;

        modalInstance.modalComponentRef = modalComponentRef;
        modalInstance.shouldRefreshWhenServerIsBack = shouldRefreshWhenServerIsBack;
    }
}
