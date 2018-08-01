import {AfterViewInit, Component, ComponentRef, OnDestroy, ViewChild} from '@angular/core';
import {ComposedStepDef} from "../../../../model/composed-step-def.model";
import {ModalDirective} from "ngx-bootstrap";
import {ComposedStepViewComponent} from "../../step/composed-step-view/composed-step-view.component";
import {Subject} from "rxjs";

@Component({
    selector: 'step-modal',
    templateUrl: 'step-modal.component.html',
    styleUrls: ['step-modal.component.scss']
})

export class StepModalComponent implements AfterViewInit, OnDestroy {

    model: ComposedStepDef;
    isCreateAction: boolean = false;

    @ViewChild("modal") modal: ModalDirective;
    @ViewChild("composedStepView") composedStepViewComponent: ComposedStepViewComponent;

    modalComponentRef: ComponentRef<StepModalComponent>;
    modalSubject: Subject<ComposedStepDef>;

    constructor() {
    }

    ngAfterViewInit(): void {
        this.modal.show();
    }

    ngOnDestroy(): void {
        this.clearStepModal();
    }

    onOkAction() {
        this.composedStepViewComponent.onBeforeSave();
        this.modalSubject.next(this.model);
        this.clearStepModal();
    }

    onCancelAction() {
        this.clearStepModal();
    }

    private clearStepModal() {
        this.modalSubject.complete();
        this.modal.hide();

        this.modalComponentRef.destroy();

        this.modalComponentRef = null;
        this.modalSubject = null;
    }
}
