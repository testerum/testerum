import {Serializable} from "../../../../model/infrastructure/serializable.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonUtil} from "../../../../utils/json.util";

export class RunnerConfig implements Serializable<RunnerConfig> {
    name: string;
    path: Path = Path.createInstanceOfEmptyPath();
    oldPath: Path;
    settings: Map<string, string> = new Map<string, string>();
    tagsToInclude: Array<string> = [];
    tagsToExclude: Array<string> = [];
    paths: Array<Path> = [];

    deserialize(input: Object): RunnerConfig {

        this.name = input['name'];
        this.path = Path.deserialize(input["path"]);
        this.oldPath = Path.deserialize(input["oldPath"]);
        this.settings.clear();
        let settingsJson = input['settings'];
        Object.keys(settingsJson).forEach( key => {
            let value = settingsJson[key];
            this.settings.set(key, value);
        });
        this.tagsToInclude = input['tagsToInclude'] || [];
        this.tagsToExclude = input['tagsToExclude'] || [];
        this.paths = [];
        for (let selectedPathJson of (input['paths']) || []) {
            this.paths.push(Path.deserialize(selectedPathJson));
        }

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"name":' + JsonUtil.stringify(this.name) +
            ',"path":' + JsonUtil.serializeSerializable(this.path) +
            ',"oldPath":' + JsonUtil.serializeSerializable(this.oldPath) +
            ',"settings":' + JsonUtil.stringify(this.settings) +
            ',"tagsToInclude":' + JsonUtil.stringify(this.tagsToInclude) +
            ',"tagsToExclude":' + JsonUtil.stringify(this.tagsToExclude) +
            ',"paths":' + JsonUtil.serializeArrayOfSerializable(this.paths) +
            '}'
    }

     clone(): RunnerConfig {
        return new RunnerConfig().deserialize(JSON.parse(this.serialize()));
    }
}
