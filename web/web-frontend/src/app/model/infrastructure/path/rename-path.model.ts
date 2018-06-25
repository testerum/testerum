
import {Path} from "./path.model";
import {JsonUtil} from "../../../utils/json.util";

export class RenamePath implements Serializable<RenamePath> {
    path: Path;
    newName: string;

    constructor(path: Path, newName: string) {
        this.path = path;
        this.newName = newName;
    }

    deserialize(input: Object): RenamePath {
        return null;
    }

    serialize(): string {
        return "" +
            '{' +
            '"path":' + this.path.serialize() + ',' +
            '"newName":' + JsonUtil.stringify(this.newName) +
            '}'
    }
}
