import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {StepChooserService} from "./step-chooser.service";
import {StepDef} from "../../../model/step-def.model";
import {ModelComponentMapping} from "../../../model/infrastructure/model-component-mapping.model";
import {StepChooserContainerComponent} from "./step-chooser-container/step-chooser-container.component";
import {StepTreeContainerModel} from "../../../functionalities/steps/steps-tree/model/step-tree-container.model";
import {StepTreeNodeModel} from "../../../functionalities/steps/steps-tree/model/step-tree-node.model";
import {StepChooserNodeComponent} from "./step-chooser-container/step-chooser-node/step-chooser-node.component";

@Component({
    moduleId: module.id,
    selector: 'step-chooser',
    templateUrl: 'step-chooser.component.html',
    styleUrls: ['step-chooser.component.scss']
})
export class StepChooserComponent implements AfterViewInit {

    @ViewChild("modal") modal:ModalDirective;

    jsonModelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(StepTreeContainerModel, StepChooserContainerComponent)
        .addPair(StepTreeNodeModel, StepChooserNodeComponent);

    stepChooserService: StepChooserService;
    constructor() {
    }

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.stepChooserService.clearModal()
        })
    }

    onCancelAction() {
        this.modal.hide()
    }

    isStepSelected(): boolean {
        return this.stepChooserService.treeModel != null &&
            this.stepChooserService.treeModel.selectedNode != null &&
            (this.stepChooserService.treeModel.selectedNode as StepTreeNodeModel).stepDef != null;
    }

    onStepChooseAction() {
        if (this.isStepSelected()) {
            this.stepChooserService.onStepChooseAction();
            this.modal.hide()
        }
    }
}
