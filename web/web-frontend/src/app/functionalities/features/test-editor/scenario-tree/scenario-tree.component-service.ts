import {EventEmitter, Injectable} from "@angular/core";
import {TestModel} from "../../../../model/test/test.model";
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {Scenario} from "../../../../model/test/scenario/scenario.model";
import {ScenarioTreeUtil} from "./util/scenario-tree.util";
import {ScenarioContainerModel} from "./model/scenario-container.model";
import {ArrayUtil} from "../../../../utils/array.util";
import {ScenarioContainerComponent} from "./nodes/scenario-container/scenario-container.component";

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

    afterPasteOperation() {
        this.scenarioToCopy = null;
        this.setSelectedNode(null);
    }
}
