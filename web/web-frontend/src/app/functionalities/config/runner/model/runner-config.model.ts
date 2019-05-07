import {Serializable} from "../../../../model/infrastructure/serializable.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonUtil} from "../../../../utils/json.util";

export class RunnerConfig implements Serializable<RunnerConfig> {
    name: string;
    settings: Map<string, string> = new Map<string, string>();
    tagsToExecute: Array<string> = [];
    tagsToExclude: Array<string> = [];
    selectedPaths: Array<Path> = [];

    deserialize(input: Object): RunnerConfig {

        this.name = input['name'];
        this.settings.clear();
        let settingsJson = input['settings'];
        Object.keys(settingsJson).forEach( key => {
            let value = settingsJson[key];
            this.settings.set(key, value);
        });
        this.tagsToExecute = input['tagsToExecute'] || [];
        this.tagsToExclude = input['tagsToExclude'] || [];
        this.selectedPaths = [];
        for (let selectedPathJson of (input['selectedPaths']) || []) {
            this.selectedPaths.push(Path.deserialize(selectedPathJson));
        }

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"name":' + JsonUtil.stringify(this.name) +
            ',"settings":' + JsonUtil.stringify(this.settings) +
            ',"tagsToExecute":' + JsonUtil.stringify(this.tagsToExecute) +
            ',"tagsToExclude":' + JsonUtil.stringify(this.tagsToExclude) +
            ',"selectedPaths":' + JsonUtil.serializeArrayOfSerializable(this.selectedPaths) +
            '}'
    }
}
