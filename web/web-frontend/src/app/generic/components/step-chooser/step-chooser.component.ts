
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

    basicStepsJsonTreeModel: JsonTreeModel;
    composedStepsJsonTreeModel: JsonTreeModel;

    constructor(private stepChooserService: StepChooserService,
                private stepsService: StepsService) {
    }

    ngOnInit() {
        this.stepsService.getDefaultSteps().subscribe(
            steps => this.basicStepsJsonTreeModel = StepsTreeUtil.mapStepsDefToStepJsonTreeModel(steps, false)
        );
    }

    showStepChooserModelWithoutStepReference(stepChoseHandler:StepChoseHandler, stepIdToRemove:string) {
        this.loadComposedStepsWithoutStep(stepIdToRemove);

        this.stepChoseHandler = stepChoseHandler;

        this.stepChooser.show()
    }

    showStepChooserModal(stepChoseHandler:StepChoseHandler) {
        this.loadComposedStepsWithoutStep(null);

        this.stepChoseHandler = stepChoseHandler;

        this.stepChooser.show()
    }

    private loadComposedStepsWithoutStep(stepIdToRemove:string) {
        this.stepsService.getComposedStepDefs().subscribe(
            composedSteps => {
                let filteredComposedSteps = composedSteps.filter(composedStep => !this.hasOrContainsStepsWithId(composedStep, stepIdToRemove));

                this.composedStepsJsonTreeModel = StepsTreeUtil.mapStepsDefToStepJsonTreeModel(filteredComposedSteps, true)
            }
        );
    }

    private hasOrContainsStepsWithId(composedStep: ComposedStepDef, stepIdToRemove: string) {
        if(composedStep.id == stepIdToRemove) {
            return true
        }
        return this.containsStepsWithId(composedStep, stepIdToRemove);
    }

    private containsStepsWithId(composedStep: ComposedStepDef, stepIdToRemove: string) {
        for (let stepCall of composedStep.stepCalls) {
            let childComposedStepDef = stepCall.stepDef;
            if(childComposedStepDef instanceof ComposedStepDef) {
                if(childComposedStepDef.id == stepIdToRemove) {
                    return true
                }
                let childStepContainsStepToRemove = this.containsStepsWithId(childComposedStepDef, stepIdToRemove);

                if(childStepContainsStepToRemove) {
                    return true;
                }
            }
        }
        return false;
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
