import {
    AfterViewInit,
    Component,
    ComponentFactoryResolver,
    OnDestroy,
    OnInit,
    ViewChild,
    ViewContainerRef
} from '@angular/core';
import {ComposedStepDef} from "../../../../model/composed-step-def.model";
import {ModalDirective} from "ngx-bootstrap";
import {ComposedStepViewComponent} from "../../step/composed-step-view/coposed-step-view.component";
import {StepModalService} from "./step-modal.service";

@Component({
    selector: 'step-modal',
    templateUrl: 'step-modal.component.html',
    styleUrls: ['step-modal.component.scss']
})

export class StepModalComponent implements AfterViewInit {

    model: ComposedStepDef;
    isCreateAction: boolean = false;

    stepModalService: StepModalService;

    @ViewChild("modal") modal: ModalDirective;
    @ViewChild(ComposedStepViewComponent) composedStepViewComponent: ComposedStepViewComponent;

    constructor() {
    }

    ngAfterViewInit(): void {
        this.modal.show();
    }

    onCancelAction() {
        this.stepModalService.onCancelAction();
    }

    onOkAction() {
        this.stepModalService.onOkAction()
    }
}
