import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {SelectProjectModalComponent} from "./select-project-modal.component";
import {AppComponent} from "../../../../app.component";

@Injectable()
export class SelectProjectModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    show() {
        const factory = this.componentFactoryResolver.resolveComponentFactory(SelectProjectModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: SelectProjectModalComponent = modalComponentRef.instance;

        modalInstance.modalComponentRef = modalComponentRef;
    }
}
