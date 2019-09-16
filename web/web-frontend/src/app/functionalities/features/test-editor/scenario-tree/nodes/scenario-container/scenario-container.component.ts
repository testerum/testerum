import {AfterViewInit, Component, Input, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";
import {ScenarioTreeComponentService} from "../../scenario-tree.component-service";
import {ScenarioContainerModel} from "../../model/scenario-container.model";
import {JsonTreeContainer} from "../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {TreeTextEditComponent} from "./tree-text-edit/tree-text-edit.component";
import {TestModel} from "../../../../../../model/test/test.model";
import {TestsRunnerService} from "../../../../tests-runner/tests-runner.service";
import {StringUtils} from "../../../../../../utils/string-utils.util";
import {ScenarioTreeUtil} from "../../util/scenario-tree.util";
import {ScenarioNameUtil} from "../../util/scenario-name.util";

@Component({
    selector: 'scenario-container',
    templateUrl: 'scenario-container.component.html',
    styleUrls: [
        'scenario-container.component.scss',
        '../../../../../../generic/css/tree.scss',
    ],
    encapsulation: ViewEncapsulation.None
})
export class ScenarioContainerComponent implements OnInit, AfterViewInit {

    @Input() model: ScenarioContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    @ViewChild("treeTextEdit", { static: true }) treeTextEditComponent: TreeTextEditComponent;

    hasMouseOver: boolean = false;
    isScenarioNameEditMode: any = false;

    constructor(private scenarioTreeComponentService: ScenarioTreeComponentService,
                private testsRunnerService: TestsRunnerService) {
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
        let allScenariosName = ScenarioTreeUtil.getAllScenariosName(this.scenarioTreeComponentService.testModel.scenarios);
        this.model.scenario.name = ScenarioNameUtil.getNextDefaultName(allScenariosName);
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

    isDisabled(): boolean {
        return !this.model.scenario.enabled;
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

    isFirstScenario(): boolean {
        return this.findScenarioIndex() == 0;
    }

    isLastScenario(): boolean {
        return this.findScenarioIndex() == this.model.parentContainer.getChildren().length - 1;
    }

    private findScenarioIndex(): number {
        return this.model.parentContainer.getChildren().indexOf(this.model);
    }

    public removeScenario(): void {
        this.scenarioTreeComponentService.removeScenario(this.model);
    }

    onCopyScenario() {
        this.setSelected();
        this.scenarioTreeComponentService.setScenarioToCopy(this);
    }

    private setSelected() {
        this.scenarioTreeComponentService.setSelectedNode(this);
    }

    isSelectedForCopyOrCut(): boolean {
        return this.scenarioTreeComponentService.getSelectedNode() == this;
    }

    onScenarioNameChange(newName: string) {
        this.model.scenario.name = newName;
    }

    getTestModel(): TestModel {
        return this.scenarioTreeComponentService.testModel;
    }

    runScenario() {
        this.testsRunnerService.runTestScenario(this.getTestModel().path, this.findScenarioIndex());
    }

    disableOrEnableScenario() {
        this.model.scenario.enabled = !this.model.scenario.enabled;
    }

    onTextEditModeChanged(textEditMode: boolean) {
        if (!textEditMode) {
            if (StringUtils.isEmpty(this.model.scenario.name)) {
                this.setDefaultName();
            }
        }
    }
}
