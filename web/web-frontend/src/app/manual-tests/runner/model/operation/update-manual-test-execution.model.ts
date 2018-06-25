import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonUtil} from "../../../../utils/json.util";
import {ManualTestModel} from "../../../model/manual-test.model";
import {ManualTestExeModel} from "../manual-test-exe.model";

export class UpdateManualTestExecutionModel implements Serializable<UpdateManualTestExecutionModel> {

    manualTestRunnerPath: Path;
    manualTestExe: ManualTestModel;

    constructor(manualTestRunnerPath: Path, manualTestExeModel: ManualTestExeModel) {
        this.manualTestRunnerPath = manualTestRunnerPath;
        this.manualTestExe = manualTestExeModel;
    }

    deserialize(input: Object): UpdateManualTestExecutionModel {
        this.manualTestRunnerPath = Path.deserialize(input["manualTestRunnerPath"]);
        this.manualTestExe = input['manualTestExe'];

        return this;
    }

    serialize() {
        return "" +
            '{' +
            '"manualTestRunnerPath":' + JsonUtil.serializeSerializable(this.manualTestRunnerPath) + ',' +
            '"manualTestExe":' + JsonUtil.serializeSerializable(this.manualTestExe) +
            '}'
    }
}
