import {Serializable} from "../../../../model/infrastructure/serializable.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonUtil} from "../../../../utils/json.util";
import {PathWithScenarioIndexes} from "./path-with-scenario-indexes.model";

export class RunConfig implements Serializable<RunConfig> {
    name: string;
    settings: Map<string, string> = new Map<string, string>();
    tagsToInclude: Array<string> = [];
    tagsToExclude: Array<string> = [];
    pathsToInclude: Array<PathWithScenarioIndexes> = [];

    deserialize(input: Object): RunConfig {

        this.name = input['name'];
        this.settings.clear();
        let settingsJson = input['settings'];
        Object.keys(settingsJson).forEach( key => {
            let value = settingsJson[key];
            this.settings.set(key, value);
        });
        this.tagsToInclude = input['tagsToInclude'] || [];
        this.tagsToExclude = input['tagsToExclude'] || [];
        this.pathsToInclude = [];
        for (let selectedPathJson of (input['pathsToInclude']) || []) {
            this.pathsToInclude.push(new PathWithScenarioIndexes().deserialize(selectedPathJson));
        }

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"name":' + JsonUtil.stringify(this.name) +
            ',"settings":' + JsonUtil.serializeMap(this.settings) +
            ',"tagsToInclude":' + JsonUtil.stringify(this.tagsToInclude) +
            ',"tagsToExclude":' + JsonUtil.stringify(this.tagsToExclude) +
            ',"pathsToInclude":' + JsonUtil.serializeArrayOfSerializable(this.pathsToInclude) +
            '}'
    }

    clone(): RunConfig {
        return new RunConfig().deserialize(JSON.parse(this.serialize()));
    }

}
