
import {Path} from "./path.model";
import {JsonUtil} from "../../../utils/json.util";

export class CopyPath implements Serializable<CopyPath> {
    copyPath: Path;
    destinationPath: Path;

    constructor(copyPath: Path, destinationPath: Path) {
        this.copyPath = copyPath;
        this.destinationPath = destinationPath;
    }

    deserialize(input: Object): CopyPath {
        return null;
    }

    serialize(): string {
        return "" +
            '{' +
            '"copyPath":' + this.copyPath.serialize() + ',' +
            '"destinationPath":' + this.destinationPath.serialize() +
            '}'
    }
}
