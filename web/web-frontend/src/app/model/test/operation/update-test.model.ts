import {Path} from "../../infrastructure/path/path.model";
import {JsonUtil} from "../../../utils/json.util";
import {TestModel} from "../test.model";

export class UpdateTestModel implements Serializable<UpdateTestModel> {

    oldPath:Path;
    testModel:TestModel;

    constructor(oldPath: Path, testModel: TestModel) {
        this.oldPath = oldPath;
        this.testModel = testModel;
    }

    deserialize(input: Object): UpdateTestModel {
        this.oldPath = Path.deserialize(input["oldPath"]);
        this.testModel = input['testModel'];

        return this;
    }

    serialize() {
        return "" +
            '{' +
            '"oldPath":' + JsonUtil.serializeSerializable(this.oldPath) + ',' +
            '"testModel":' + JsonUtil.serializeSerializable(this.testModel) +
            '}'
    }
}
