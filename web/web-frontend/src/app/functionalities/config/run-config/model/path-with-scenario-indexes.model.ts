import {Serializable} from "../../../../model/infrastructure/serializable.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonUtil} from "../../../../utils/json.util";

export class PathWithScenarioIndexes implements Serializable<PathWithScenarioIndexes> {

    path: Path;
    scenarioIndexes: Array<number> = [];

    deserialize(input: Object): PathWithScenarioIndexes {
        this.path = Path.deserialize(input['path']);
        this.scenarioIndexes = input["scenarioIndexes"];

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"path":' + this.path.serialize() +
            ',"scenarioIndexes":' + JsonUtil.stringify(this.scenarioIndexes) +
            '}'
    }

}
