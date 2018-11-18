import {Path} from "../../../../../model/infrastructure/path/path.model";
import {ManualTestStatus} from "../enums/manual-test-status.enum";

export abstract class ManualTestsStatusTreeBase {

    path: Path;
    name: string;
    status: ManualTestStatus;
}
