import {JsonUtil} from "../../../utils/json.util";
import {ManualTestModel} from "../manual-test.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {Serializable} from "../../../model/infrastructure/serializable.model";

export class UpdateManualTestModel implements Serializable<UpdateManualTestModel> {

    oldPath:Path;
    manualTestModel:ManualTestModel;

    constructor(oldPath: Path, manualTestModel: ManualTestModel) {
        this.oldPath = oldPath;
        this.manualTestModel = manualTestModel;
    }

    deserialize(input: Object): UpdateManualTestModel {
        this.oldPath = Path.deserialize(input["oldPath"]);
        this.manualTestModel = input['manualTest'];

        return this;
    }

    serialize() {
        return "" +
            '{' +
            '"oldPath":' + JsonUtil.serializeSerializable(this.oldPath) + ',' +
            '"manualTest":' + JsonUtil.serializeSerializable(this.manualTestModel) +
            '}'
    }
}
