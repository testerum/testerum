import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {StepModalComponent} from "./step-modal.component";
import {ComposedStepDef} from "../../../../model/step/composed-step-def.model";
import {Observable, Subject} from "rxjs";
import {AppComponent} from "../../../../app.component";
import {StepContext} from "../../../../model/step/context/step-context.model";

@Injectable()
export class StepModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    showStepModal(model: ComposedStepDef, stepContext: StepContext = new StepContext()): Observable<ComposedStepDef> {
        let modalSubject = new Subject<ComposedStepDef>();

        let modelClone: ComposedStepDef = model.clone();

        const factory = this.componentFactoryResolver.resolveComponentFactory(StepModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: StepModalComponent = modalComponentRef.instance;

        modalInstance.model = modelClone;
        modalInstance.stepContext = stepContext;

        modalInstance.modalComponentRef = modalComponentRef;
        modalInstance.modalSubject = modalSubject;

        return modalSubject.asObservable();
    }

}
