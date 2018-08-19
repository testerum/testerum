
import {JsonUtil} from "../../../../../../../../utils/json.util";
import {Serializable} from "../../../../../../../../model/infrastructure/serializable.model";

export class HttpMockRequestScenario implements Serializable<HttpMockRequestScenario> {
    scenarioName: string;
    currentState: string;
    newState: string;

    deserialize(input: Object): HttpMockRequestScenario {

        if (input['scenarioName']) {
            this.scenarioName = input['scenarioName'];
        }
        if (input['currentState']) {
            this.currentState = input['currentState'];
        }
        if (input['newState']) {
            this.newState = input['newState'];
        }

        return this;
    }

    serialize(): string {
        let result = '{';
        result += '"scenarioName":' + JsonUtil.stringify(this.scenarioName) + ',';
        result += '"currentState":' + JsonUtil.stringify(this.currentState) + ',';
        result += '"newState":' + JsonUtil.stringify(this.newState);
        result += '}';

        return result;
    }

    isEmpty(): boolean {
        return this.scenarioName == null && this.currentState == null && this.newState == null;
    }
}
