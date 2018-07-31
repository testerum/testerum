import {
    AfterViewInit,
    Component,
    ComponentFactoryResolver, ComponentRef,
    OnDestroy,
    OnInit,
    ViewChild,
    ViewContainerRef
} from '@angular/core';
import {ComposedStepDef} from "../../../../model/composed-step-def.model";
import {ModalDirective} from "ngx-bootstrap";
import {ComposedStepViewComponent} from "../../step/composed-step-view/coposed-step-view.component";
import {StepModalService} from "./step-modal.service";
import {Subject} from "rxjs/Subject";
import {AppComponent} from "../../../../app.component";

@Component({
    selector: 'step-modal',
    templateUrl: 'step-modal.component.html',
    styleUrls: ['step-modal.component.scss']
})

export class StepModalComponent implements AfterViewInit, OnDestroy {

    model: ComposedStepDef;
    isCreateAction: boolean = false;

    @ViewChild("modal") modal: ModalDirective;
    @ViewChild(ComposedStepViewComponent) composedStepViewComponent: ComposedStepViewComponent;

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
        this.modalSubject.next(this.model);
        this.clearStepModal();
    }

    onCancelAction() {
        this.clearStepModal();
    }

    private clearStepModal() {
        this.modalSubject.complete();
        this.modal.hide();

        let modalIndex = AppComponent.rootViewContainerRef.indexOf(this.modalComponentRef);
        AppComponent.rootViewContainerRef.remove(modalIndex);

        this.modalComponentRef = null;
        this.modalSubject = null;
    }
}
