import {Serializable} from "../../../../model/infrastructure/serializable.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonUtil} from "../../../../utils/json.util";

export class ManualTreeTest implements Serializable<ManualTreeTest>{

    path: Path;
    testName: string;

    deserialize(input: Object): ManualTreeTest {
        this.path = Path.deserialize(input["path"]);
        this.testName = input['testName'];
        return this;
    }

    serialize(): string {
        let response = "" +
            '{' +
            '"path":' + JsonUtil.serializeSerializable(this.path) +
            ',"testName":' + JsonUtil.stringify(this.testName) +
            '}';

        return response;
    }
}
