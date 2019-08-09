import {EventEmitter, Injectable} from "@angular/core";
import {TestModel} from "../../../../model/test/test.model";
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {Scenario} from "../../../../model/test/scenario/scenario.model";
import {ScenarioTreeUtil} from "./util/scenario-tree.util";
import {ScenarioContainerModel} from "./model/scenario-container.model";
import {ArrayUtil} from "../../../../utils/array.util";
import {ScenarioContainerComponent} from "./nodes/scenario-container/scenario-container.component";
import {NameUtil} from "../../../../utils/name.util";
import {ScenarioParamsContainerModel} from "./model/scenario-params-container.model";

@Injectable()
export class ScenarioTreeComponentService {
    jsonTreeModel: JsonTreeModel;
    testModel: TestModel;

    selectedScenario: ScenarioContainerComponent;

    scenarioToCopy: ScenarioContainerComponent;

    isEditMode: boolean;
    editModeEventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    setEditMode(editMode: boolean) {
        this.editModeEventEmitter.emit(editMode);
        this.isEditMode = editMode
    }

    addNewScenario() {
        let scenario = new Scenario();
        this.testModel.scenarios.push(scenario);
        let scenarioContainer = ScenarioTreeUtil.getScenarioContainer(scenario, this.testModel.scenarios.length, this.jsonTreeModel);
        scenarioContainer.jsonTreeNodeState.showChildren = true;
        scenarioContainer.showAsEditScenarioNameMode = true;
        this.jsonTreeModel.getChildren().push(scenarioContainer);
    }

    removeScenario(scenarioContainer: ScenarioContainerModel) {
        ArrayUtil.removeElementFromArray(scenarioContainer.parentContainer.getChildren(), scenarioContainer);
        ArrayUtil.removeElementFromArray(this.testModel.scenarios, scenarioContainer.scenario);
    }

    setSelectedNode(selectedScenario: ScenarioContainerComponent) {
        this.selectedScenario = selectedScenario;
    }

    getSelectedNode(): ScenarioContainerComponent {
        return this.selectedScenario;
    }

    setScenarioToCopy(scenario: ScenarioContainerComponent) {
        this.scenarioToCopy = scenario;
    }

    canPaste(): boolean {
        return this.scenarioToCopy != null;
    }

    onPasteScenario() {
        let treeModel = this.jsonTreeModel;

        if (this.scenarioToCopy) {
            let scenarioToCopyModel = this.scenarioToCopy.model.scenario;

            let newScenario = scenarioToCopyModel.clone();
            if (newScenario.name) {
                let allScenariosName = ScenarioTreeUtil.getAllScenariosName(this.testModel.scenarios);
                newScenario.name = NameUtil.getUniqueNameWithCopyAndIndexSuffix(allScenariosName, newScenario.name);
            }
            this.testModel.scenarios.push(newScenario);

            let scenarioContainer = ScenarioTreeUtil.getScenarioContainer(newScenario, this.testModel.scenarios.length, treeModel);
            scenarioContainer.jsonTreeNodeState.showChildren = true;
            (scenarioContainer.children[0] as ScenarioParamsContainerModel).jsonTreeNodeState.showChildren = true;
            scenarioContainer.showAsEditScenarioNameMode = true;

            treeModel.children.push(scenarioContainer);

            this.afterPasteOperation();
        }
    }

    private afterPasteOperation() {
        this.scenarioToCopy = null;
        this.setSelectedNode(null);
    }
}
