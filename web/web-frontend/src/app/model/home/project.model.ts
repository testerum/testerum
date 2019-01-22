import {Path} from "../infrastructure/path/path.model";
import {Serializable} from "../infrastructure/serializable.model";
import {JsonUtil} from "../../utils/json.util";

export class Project implements Serializable<Project> {

    private _name: string;
    private _path: Path;

    constructor(name: string, path: Path) {
        this._name = name;
        this._path = path;
    }

    static deserialize(input: Object): Project {
        return new Project(null, null).deserialize(input);
    }

    get name(): string {
        return this._name;
    }

    get path(): Path {
        return this._path;
    }

    deserialize(input: Object): Project {
        this._name = input["name"];
        this._path = Path.createInstance(input["path"]);

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"name":' + JsonUtil.stringify(this._name) + ',' +
            '"path":' + JsonUtil.stringify(this._path.toString()) +
            '}'
    }
}
