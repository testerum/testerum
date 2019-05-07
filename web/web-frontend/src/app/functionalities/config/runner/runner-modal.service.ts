import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {AppComponent} from "../../../app.component";
import {RunnerModalComponent} from "./runner-modal.component";
import {RunnerConfig} from "./model/runner-config.model";

@Injectable()
export class RunnerModalService {

    instance: RunnerModalComponent;

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    showRunnersModal(runners: Array<RunnerConfig>) {
        if (this.instance && this.instance.modal.isShown) {
            return;
        }

        const factory = this.componentFactoryResolver.resolveComponentFactory(RunnerModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        this.instance = modalComponentRef.instance;

        this.instance.init(runners);
        this.instance.modalComponentRef = modalComponentRef;
    }
}
