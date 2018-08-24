import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonUtil} from "../../../../utils/json.util";
import {Serializable} from "../../../../model/infrastructure/serializable.model";

export class Setup implements Serializable<Setup> {
    repositoryAbsoluteJavaPath: string;

    deserialize(input: Object): Setup {
        this.repositoryAbsoluteJavaPath = input["repositoryAbsoluteJavaPath"];

        return this;
    }

    serialize() {
        return "" +
            '{' +
            '"repositoryAbsoluteJavaPath":' + JsonUtil.stringify(this.repositoryAbsoluteJavaPath) +
            '}'
    }
}
