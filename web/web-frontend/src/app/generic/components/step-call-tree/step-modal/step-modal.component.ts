import {AfterViewInit, Component, ComponentRef, OnDestroy, ViewChild} from '@angular/core';
import {ComposedStepDef} from "../../../../model/composed-step-def.model";
import {ModalDirective} from "ngx-bootstrap";
import {ComposedStepViewComponent} from "../../step/composed-step-view/composed-step-view.component";
import {Subject} from "rxjs";
import {StepContext} from "../../../../model/step/context/step-context.model";

@Component({
    selector: 'step-modal',
    templateUrl: 'step-modal.component.html',
    styleUrls: ['step-modal.component.scss']
})
export class StepModalComponent implements AfterViewInit {

    model: ComposedStepDef;
    isCreateAction: boolean = false;
    stepContext: StepContext = new StepContext();

    @ViewChild("modal") modal: ModalDirective;
    @ViewChild("composedStepView") composedStepViewComponent: ComposedStepViewComponent;

    modalComponentRef: ComponentRef<StepModalComponent>;
    modalSubject: Subject<ComposedStepDef>;

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalSubject.complete();

            this.modalComponentRef.destroy();

            this.modalComponentRef = null;
            this.modalSubject = null;
        })
    }

    onOkAction() {
        if (this.composedStepViewComponent.isValid()) {
            this.modalSubject.next(this.model);
            this.modal.hide();
        }
    }

    onCancelAction() {
        this.modal.hide();
    }
}
