import {ComponentFactoryResolver, ComponentRef, Injectable, ViewContainerRef} from "@angular/core";
import {StepModalComponent} from "./step-modal.component";
import {ComposedStepDef} from "../../../../model/composed-step-def.model";
import {Observable} from "rxjs/Observable";
import {Subject} from "rxjs/Subject";
import {AppComponent} from "../../../../app.component";

@Injectable()
export class StepModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    showStepModal(model: ComposedStepDef): Observable<ComposedStepDef> {
        let modalSubject = new Subject<ComposedStepDef>();

        let modelClone: ComposedStepDef = model.clone();

        const factory = this.componentFactoryResolver.resolveComponentFactory(StepModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: StepModalComponent = modalComponentRef.instance;

        modalInstance.model = modelClone;
        modalInstance.modalComponentRef = modalComponentRef;
        modalInstance.modalSubject = modalSubject;

        return modalSubject.asObservable();
    }

}
