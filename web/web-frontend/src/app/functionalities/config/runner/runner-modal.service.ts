import {ComponentFactoryResolver, EventEmitter, Injectable} from "@angular/core";
import {AppComponent} from "../../../app.component";
import {RunnerModalComponent} from "./runner-modal.component";
import {RunnerConfig} from "./model/runner-config.model";
import {Project} from "../../../model/home/project.model";
import {Observable} from "rxjs";
import {RunnerConfigService} from "./runner-config.service";
import {ArrayUtil} from "../../../utils/array.util";

@Injectable()
export class RunnerModalService {

    instance: RunnerModalComponent;


    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private runnerConfigService: RunnerConfigService) {
    }

    showRunnersModal(runners: Array<RunnerConfig>): Observable<RunnerConfig> | null {
        if (this.instance && this.instance.modal.isShown) {
            return null;
        }

        this.runnerConfigService.setRunners(runners);

        const factory = this.componentFactoryResolver.resolveComponentFactory(RunnerModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        this.instance = modalComponentRef.instance;

        this.instance.modalComponentRef = modalComponentRef;

        return this.instance.runnerConfigSelectedEventEmitter;
    }
}
