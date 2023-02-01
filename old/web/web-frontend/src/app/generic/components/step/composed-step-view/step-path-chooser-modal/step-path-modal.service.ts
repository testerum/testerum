import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {StepPathModalComponent} from "./step-path-modal.component";
import {Observable, Subject} from "rxjs";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {AppComponent} from "../../../../../app.component";

@Injectable()
export class StepPathModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    showModal(): Observable<Path> {
        let modalSubject = new Subject<Path>();

        const factory = this.componentFactoryResolver.resolveComponentFactory(StepPathModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: StepPathModalComponent = modalComponentRef.instance;

        modalInstance.modalComponentRef = modalComponentRef;
        modalInstance.modalSubject = modalSubject;

        return modalSubject.asObservable();
    }
}
