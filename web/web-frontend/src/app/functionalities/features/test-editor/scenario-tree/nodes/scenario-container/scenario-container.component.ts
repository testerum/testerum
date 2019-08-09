import {AfterViewInit, Component, Input, OnInit, ViewChild} from '@angular/core';
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";
import {ScenarioTreeComponentService} from "../../scenario-tree.component-service";
import {ScenarioContainerModel} from "../../model/scenario-container.model";
import {JsonTreeContainer} from "../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {TreeTextEditComponent} from "./tree-text-edit/tree-text-edit.component";

@Component({
    selector: 'scenario-container',
    templateUrl: 'scenario-container.component.html',
    styleUrls: [
        'scenario-container.component.scss',
        '../../../../../../generic/css/tree.scss',
    ]
})
export class ScenarioContainerComponent implements OnInit, AfterViewInit {

    readonly EMPTY_SCENARIO_NAME_PREFIX = "Scenario ";

    @Input() model: ScenarioContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    @ViewChild("treeTextEdit") treeTextEditComponent: TreeTextEditComponent;

    hasMouseOver: boolean = false;
    isScenarioNameEditMode: any = false;

    constructor(private scenarioTreeComponentService: ScenarioTreeComponentService) {
    }

    ngOnInit() {
        if (!this.model.scenario.name) {
            this.setDefaultName();
        }
        this.isScenarioNameEditMode = this.model.showAsEditScenarioNameMode;
    }

    ngAfterViewInit(): void {
        if (this.model.showAsEditScenarioNameMode) {
            this.treeTextEditComponent.focusInputElement();
            this.treeTextEditComponent.selectAllTextInInputElement();
        }
    }

    private setDefaultName() {
        this.model.scenario.name = this.EMPTY_SCENARIO_NAME_PREFIX + this.model.indexInParent;
    }

    toggleNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren;
        if(this.model.jsonTreeNodeState.showChildren) {
            this.model.children.forEach( (child: JsonTreeContainer) => {
                child.getNodeState().showChildren = true;
            })
        }
    }

    isEditMode(): boolean {
        return this.scenarioTreeComponentService.isEditMode;
    }

    editScenarioName() {
        this.treeTextEditComponent.setTextEditMode(true)
    }

    public moveScenarioUp(): void {
        let parentContainer = this.model.parentContainer;
        let currentNodeIndexInParent = parentContainer.getChildren().indexOf(this.model);

        if(1 <= currentNodeIndexInParent && currentNodeIndexInParent < parentContainer.getChildren().length) {
            let previewsNodeModel = parentContainer.getChildren()[currentNodeIndexInParent - 1];
            parentContainer.getChildren()[currentNodeIndexInParent - 1] = this.model;
            parentContainer.getChildren()[currentNodeIndexInParent] = previewsNodeModel;

            let scenarios = this.scenarioTreeComponentService.testModel.scenarios;
            let previewScenarios = scenarios[currentNodeIndexInParent - 1];
            scenarios[currentNodeIndexInParent - 1] = this.model.scenario;
            scenarios[currentNodeIndexInParent] = previewScenarios;
        }
    }

    public moveScenarioDown(): void {
        let parentContainer = this.model.parentContainer;
        let currentNodeIndexInParent = parentContainer.getChildren().indexOf(this.model);

        if(0 <= currentNodeIndexInParent && currentNodeIndexInParent < parentContainer.getChildren().length - 1) {
            let nextNodeModel = parentContainer.getChildren()[currentNodeIndexInParent + 1];
            parentContainer.getChildren()[currentNodeIndexInParent] = nextNodeModel;
            parentContainer.getChildren()[currentNodeIndexInParent + 1] = this.model;

            let scenarios = this.scenarioTreeComponentService.testModel.scenarios;
            let nextScenarios = scenarios[currentNodeIndexInParent + 1];
            scenarios[currentNodeIndexInParent] = nextScenarios;
            scenarios[currentNodeIndexInParent + 1] = this.model.scenario;
        }
    }

    isFirstStep(): boolean {
        return this.findStepIndex() == 0;
    }

    isLastStep(): boolean {
        return this.findStepIndex() == this.model.parentContainer.getChildren().length - 1;
    }

    private findStepIndex(): number {
        return this.model.parentContainer.getChildren().indexOf(this.model);
    }

    public removeStep(): void {
        this.scenarioTreeComponentService.removeScenario(this.model);
    }

    onCopyStep() {
        this.setSelected();
        this.scenarioTreeComponentService.setScenarioToCopy(this);
    }

    private setSelected() {
        this.scenarioTreeComponentService.setSelectedNode(this);
    }

    isSelectedForCopyOrCut(): boolean {
        return this.scenarioTreeComponentService.getSelectedNode() == this;
    }
}
