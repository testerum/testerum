import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {AppComponent} from "../../../app.component";
import {AboutComponent} from "./about.component";

@Injectable()
export class AboutModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    showAboutModal() {
        const factory = this.componentFactoryResolver.resolveComponentFactory(AboutComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: AboutComponent = modalComponentRef.instance;

        modalInstance.modalComponentRef = modalComponentRef;
    }
}
