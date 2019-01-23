import {Path} from "../infrastructure/path/path.model";
import {Serializable} from "../infrastructure/serializable.model";
import {JsonUtil} from "../../utils/json.util";

export class Project implements Serializable<Project> {

    private _name: string;
    private _path: string;
    private _lastOpened: Date;

    constructor(name: string, path: string, lastOpened: Date = null) {
        this._name = name;
        this._path = path;
        this._lastOpened = lastOpened;
    }

    static deserialize(input: Object): Project {
        return new Project(null, null, null).deserialize(input);
    }

    get name(): string {
        return this._name;
    }

    get path(): string {
        return this._path;
    }

    get lastOpened(): Date {
        return this._lastOpened;
    }

    deserialize(input: Object): Project {
        this._name = input["name"];
        this._path = input["path"];
        this._lastOpened = new Date(input["lastOpened"]);

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
