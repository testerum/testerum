
import {Component, OnInit, ViewChild, Type} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {StepChooserService} from "./step-chooser.service";
import {StepChoseHandler} from "./step-choosed-handler.interface";
import {StepsService} from "../../../service/steps.service";
import StepsTreeUtil from "../../../functionalities/steps/steps-tree/util/steps-tree.util";
import {ComposedStepDef} from "../../../model/composed-step-def.model";
import {StepDef} from "../../../model/step-def.model";
import {ModelComponentMapping} from "../../../model/infrastructure/model-component-mapping.model";
import {StepChooserContainerComponent} from "./step-chooser-container/step-chooser-container.component";
import {StepTreeContainerModel} from "../../../functionalities/steps/steps-tree/model/step-tree-container.model";
import {StepTreeNodeModel} from "../../../functionalities/steps/steps-tree/model/step-tree-node.model";
import {JsonTreeModel} from "../json-tree/model/json-tree.model";
import {StepChooserNodeComponent} from "./step-chooser-container/step-chooser-node/step-chooser-node.component";
import {Path} from "../../../model/infrastructure/path/path.model";
import {ActivatedRoute} from "@angular/router";

@Component({
    moduleId: module.id,
    selector: 'step-chooser',
    templateUrl: 'step-chooser.component.html',
    styleUrls: ['step-chooser.component.css']
})
export class StepChooserComponent implements OnInit {

    @ViewChild("stepChooser") stepChooser:ModalDirective;

    jsonModelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(StepTreeContainerModel, StepChooserContainerComponent)
        .addPair(StepTreeNodeModel, StepChooserNodeComponent);

    stepChoseHandler:StepChoseHandler;

    stepChooserService: StepChooserService;
    constructor(private activatedRoute: ActivatedRoute,
                stepChooserService: StepChooserService) {
        this.stepChooserService = stepChooserService;
    }

    ngOnInit() {
    }

    showStepChooserModelWithoutStepReference(stepChoseHandler:StepChoseHandler, stepIdToRemove:string) {
        this.stepChooserService.stepIdToRemove = stepIdToRemove;

        let pathAsString = this.activatedRoute.firstChild ? this.activatedRoute.firstChild.snapshot.params['path'] : null;
        let path: Path = pathAsString != null ? Path.createInstance(pathAsString) : null;

        this.stepChooserService.initializeStepsTreeFromServer(path);

        this.stepChoseHandler = stepChoseHandler;

        this.stepChooser.show()
    }

    showStepChooserModal(stepChoseHandler:StepChoseHandler) {
        this.showStepChooserModelWithoutStepReference(stepChoseHandler, null);
    }

    hide() {
        this.stepChooser.hide()
    }

    addStep() {
        let chosenStep:StepDef = this.stepChooserService.selectedStep.stepDef;
        let clonedChosenStep:StepDef = chosenStep.clone();
        this.stepChoseHandler.onStepChose(clonedChosenStep);
        this.hide();
    }
}
