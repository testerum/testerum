import {ScenarioParam} from "../../../../../../../../model/test/scenario/param/scenario-param.model";


export class ScenarioParamChangeModel {
    oldParam: ScenarioParam;
    newParam: ScenarioParam;
    action: ScenarioParamModalResultModelAction;
}

export enum ScenarioParamModalResultModelAction {
    UPDATE,
    DELETE,
    ADD,
    CANCEL
}
