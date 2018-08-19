import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonUtil} from "../../../../utils/json.util";
import {Serializable} from "../../../../model/infrastructure/serializable.model";

export class Setup implements Serializable<Setup> {
    repositoryPath: Path;

    deserialize(input: Object): Setup {
        this.repositoryPath = Path.deserialize(input["repositoryPath"]);

        return this;
    }

    serialize() {
        return "" +
            '{' +
            '"repositoryPath":' + JsonUtil.serializeSerializable(this.repositoryPath) +
            '}'
    }
}
