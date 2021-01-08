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
import {ScenarioParam} from "../../../../model/test/scenario/param/scenario-param.model";
import {
    ScenarioParamChangeModel,
    ScenarioParamModalResultModelAction
} from "./nodes/scenario-param-node/modal/model/scenario-param-change.model";

@Injectable()
export class ScenarioTreeComponentService {
    jsonTreeModel: JsonTreeModel;
    testModel: TestModel;

    selectedScenario: ScenarioContainerComponent;

    scenarioToCopy: ScenarioContainerComponent;

    isEditMode: boolean;
    editModeEventEmitter: EventEmitter<boolean>;

    initComponentTree() {
        ScenarioTreeUtil.mapScenarioToTreeModel(this.testModel.scenarios, this.jsonTreeModel);
    }

    setEditMode(editMode: boolean) {
        if (this.isEditMode != editMode) {
            this.editModeEventEmitter.emit(editMode);
            this.isEditMode = editMode
        }
    }

    addNewScenario() {
        let scenario = new Scenario();
        scenario.params = this.getEmptyParamsThatExistsInOtherScenarios();
        this.testModel.scenarios.push(scenario);

        let scenarioContainer = ScenarioTreeUtil.getScenarioContainer(scenario, this.testModel.scenarios.length, this.jsonTreeModel);
        scenarioContainer.jsonTreeNodeState.showChildren = true;
        scenarioContainer.showAsEditScenarioNameMode = true;
        this.jsonTreeModel.getChildren().push(scenarioContainer);
    }

    private getEmptyParamsThatExistsInOtherScenarios(): ScenarioParam[] {
        let resultParamsMap: Map<string, ScenarioParam> = new Map<string, ScenarioParam>();

        for (const scenario of this.testModel.scenarios) {
            for (const param of scenario.params) {
                let key = param.name.toLowerCase();
                let existingScenarioParam = resultParamsMap.get(key);
                if (!existingScenarioParam) {
                    let scenarioParamToAdd = param.clone();
                    scenarioParamToAdd.value = null;
                    resultParamsMap.set(key, scenarioParamToAdd);
                }
            }
        }

        let result: ScenarioParam[] = [];
        resultParamsMap.forEach((value, key) => {
            result.push(value);
        });

        return result;
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

    updateScenariosParams(paramChangeResult: ScenarioParamChangeModel, scenarioOfChangedParam: Scenario) {

        let oldParam = paramChangeResult.oldParam;
        let newParam = paramChangeResult.newParam;

        if (paramChangeResult.action == ScenarioParamModalResultModelAction.CANCEL) { return; }

        if (paramChangeResult.action == ScenarioParamModalResultModelAction.DELETE) {
            this.deleteParamFromAllScenarios(oldParam);
        }

        if (paramChangeResult.action == ScenarioParamModalResultModelAction.ADD) {
            this.addParamToAllScenarios(newParam, scenarioOfChangedParam);
        }

        if (paramChangeResult.action == ScenarioParamModalResultModelAction.UPDATE) {
            this.updateParamInAllScenarios(oldParam, newParam, scenarioOfChangedParam);
        }
        this.initComponentTree();
    }

    private getParamWithName(scenario: Scenario, name: string): ScenarioParam|null {
        for (const param of scenario.params) {
            if (param.name == name) {
                return param;
            }
        }
        return null;
    }

    private deleteParamFromAllScenarios(paramToDelete: ScenarioParam) {

        for (const scenario of this.testModel.scenarios) {
            let scenarioParamToDelete = this.getParamByNameFromScenario(paramToDelete.name, scenario);
            if (scenarioParamToDelete) {
                ArrayUtil.removeElementFromArray(scenario.params, scenarioParamToDelete);
            }
        }
    }

    private getParamByNameFromScenario(paramName: string, scenario: Scenario): ScenarioParam|null {
        for (const param of scenario.params) {
            if (param.name == paramName) {
                return param;
            }
        }
        return null;
    }

    private addParamToAllScenarios(newParam: ScenarioParam, scenarioOfChangedParam: Scenario) {
        for (const scenario of this.testModel.scenarios) {
            let alreadyExistingParamByName = this.getParamByNameFromScenario(newParam.name, scenario);

            let alreadyExistingValue: string;
            if (alreadyExistingParamByName) {
                alreadyExistingValue = alreadyExistingParamByName.value;
                ArrayUtil.removeElementFromArray(scenario.params, alreadyExistingParamByName);
            }

            let scenarioParam = new ScenarioParam();
            scenarioParam.name = newParam.name;
            scenarioParam.type = newParam.type;
            scenarioParam.value = scenarioOfChangedParam == scenario ? newParam.value : alreadyExistingValue;

            scenario.params.push(scenarioParam)
        }
    }

    private updateParamInAllScenarios(oldParam: ScenarioParam, newParam: ScenarioParam, scenarioOfChangedParam: Scenario) {
        if (oldParam.name != newParam.name ||
            oldParam.type != newParam.type) {
            this.updateParmNameAndTypeAllScenarios(oldParam, newParam, scenarioOfChangedParam);
        }

        oldParam.name = newParam.name;
        oldParam.type = newParam.type;
        oldParam.value = newParam.value;
    }

    private updateParmNameAndTypeAllScenarios(oldParam: ScenarioParam, newParam: ScenarioParam, scenarioOfChangedParam: Scenario) {
        let oldParamName = oldParam.name;

        for (const scenario of this.testModel.scenarios) {

            let paramWithOldName = this.getParamWithName(scenario, oldParamName);
            if (paramWithOldName == null) {
                let indexOfOldParam = scenarioOfChangedParam.params.indexOf(oldParam);

                let scenarioParam = new ScenarioParam();
                scenarioParam.name = newParam.name;
                scenarioParam.type = newParam.type;
                scenarioParam.value = newParam.value;

                scenario.params.splice(indexOfOldParam, 0, scenarioParam);
                continue;
            }

            paramWithOldName.name = newParam.name;
            paramWithOldName.type = newParam.type;
        }
    }

    reorderParamsLikeTheOrderInScenario(mainScenario: Scenario) {
        for (const scenario of this.testModel.scenarios) {
            if (scenario == mainScenario) { continue; }

            for (let i = 0; i < mainScenario.params.length; i++) {
                const mainParam = mainScenario.params[i];

                this.moveParamInTargetScenarioAtIndex(scenario, mainParam.name, i);

            }

        }
        this.initComponentTree();
    }

    private moveParamInTargetScenarioAtIndex(targetScenario: Scenario, paramNameToMove: string, indexWhereItShouldBeMoved: number) {
        let targetParamIndex: number = this.getParamIndexByName(targetScenario, paramNameToMove);
        if (0 <= targetParamIndex && targetParamIndex != indexWhereItShouldBeMoved) {
            this.swapParameters(targetScenario, targetParamIndex, indexWhereItShouldBeMoved);
        }
    }

    private getParamIndexByName(targetScenario: Scenario, paramNameToMove: string): number {
        for (let i = 0; i < targetScenario.params.length; i++) {
            if (targetScenario.params[i].name == paramNameToMove) {
                return i;
            }
        }
        return -1;
    }

    private swapParameters(scenario: Scenario, paramIndexA: number, paramIndexB: number) {
        let params = scenario.params;

        if(paramIndexA == paramIndexB) { return; }
        if (!(paramIndexA < params.length || paramIndexB < params.length)) { return; }

        let tempParam = params[paramIndexA];
        params[paramIndexA] = params[paramIndexB];
        params[paramIndexB] = tempParam;
    }
}
