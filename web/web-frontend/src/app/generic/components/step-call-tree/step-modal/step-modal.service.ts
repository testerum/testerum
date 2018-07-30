import {ComponentFactoryResolver, Injectable, ViewContainerRef} from "@angular/core";
import {StepModalComponent} from "./step-modal.component";
import {ComposedStepDef} from "../../../../model/composed-step-def.model";
import {Observable} from "rxjs/Observable";
import {Subject} from "rxjs/Subject";

@Injectable()
export class StepModalService {

    stepModalComponent: StepModalComponent;
    stepModalSubject: Subject<ComposedStepDef>;
    modalViewContainerRef: ViewContainerRef;

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    showStepModal(model: ComposedStepDef,
                  modalViewContainerRef: ViewContainerRef): Observable<ComposedStepDef> {

        let modelClone: ComposedStepDef = model.clone();

        const factory = this.componentFactoryResolver.resolveComponentFactory(StepModalComponent);
        let stepModalComponentRef = modalViewContainerRef.createComponent(factory);
        stepModalComponentRef.instance.model = modelClone;
        stepModalComponentRef.instance.isCreateAction = !modelClone.path;
        stepModalComponentRef.instance.stepModalService = this;

        this.stepModalComponent = stepModalComponentRef.instance;
        this.modalViewContainerRef = modalViewContainerRef;
        this.stepModalSubject = new Subject<ComposedStepDef>();

        return this.stepModalSubject.asObservable();
    }

    onOkAction() {
        this.stepModalSubject.next(this.stepModalComponent.model);
        this.clearStepModal();
    }

    onCancelAction() {
        this.clearStepModal();
    }

    private clearStepModal() {
        this.stepModalSubject.complete();
        this.stepModalComponent.modal.hide();

        this.modalViewContainerRef.clear();

        this.stepModalComponent = null;
        this.modalViewContainerRef = null;
        this.stepModalSubject = null;
    }
}
