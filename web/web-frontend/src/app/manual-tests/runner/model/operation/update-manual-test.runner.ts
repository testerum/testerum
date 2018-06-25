import {ManualTestsRunner} from "../manual-tests-runner.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonUtil} from "../../../../utils/json.util";

export class UpdateManualTestRunner implements Serializable<UpdateManualTestRunner> {

    oldPath:Path;
    manualTestsRunner:ManualTestsRunner;

    constructor(oldPath: Path, manualTestsRunner: ManualTestsRunner) {
        this.oldPath = oldPath;
        this.manualTestsRunner = manualTestsRunner;
    }

    deserialize(input: Object): UpdateManualTestRunner {
        this.oldPath = Path.deserialize(input["oldPath"]);
        this.manualTestsRunner = input['manualTestsRunner'];

        return this;
    }

    serialize() {
        return "" +
            '{' +
            '"oldPath":' + JsonUtil.serializeSerializable(this.oldPath) + ',' +
            '"manualTestsRunner":' + JsonUtil.serializeSerializable(this.manualTestsRunner) +
            '}'
    }
}
