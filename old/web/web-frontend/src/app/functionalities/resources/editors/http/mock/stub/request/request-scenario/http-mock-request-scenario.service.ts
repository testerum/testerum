

import {Injectable} from "@angular/core";
import {ArrayUtil} from "../../../../../../../../utils/array.util";

@Injectable()
export class HttpMockRequestScenarioService {

    private allKnownScenarioNames: Array<string> = [];
    private allKnownScenarioStates: Array<string> = ["Started"];

    addScenarioName(scenarioName: string) {
        if(!ArrayUtil.containsElement(this.allKnownScenarioNames, scenarioName)) {
            this.allKnownScenarioNames.push(scenarioName);
            ArrayUtil.sort(this.allKnownScenarioNames)
        }
    }

    addScenarioState(scenarioState: string) {
        if(!ArrayUtil.containsElement(this.allKnownScenarioStates, scenarioState)) {
            this.allKnownScenarioStates.push(scenarioState);
            ArrayUtil.sort(this.allKnownScenarioStates)
        }
    }

    getAllKnownScenarioNames(): Array<string> {
        return this.allKnownScenarioNames;
    }

    getAllKnownScenarioStates(): Array<string> {
        return this.allKnownScenarioStates;
    }
}
