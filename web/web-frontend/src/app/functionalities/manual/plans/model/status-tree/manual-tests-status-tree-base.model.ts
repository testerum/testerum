import {Path} from "../../../../../model/infrastructure/path/path.model";
import {ManualTestStatus} from "../enums/manual-test-status.enum";
import {Serializable} from "../../../../../model/infrastructure/serializable.model";
import {ManualExecPlanStatus} from "../enums/manual-exec-plan-status.enum";
import {ManualTreeTest} from "../manual-tree-test.model";
import {JsonUtil} from "../../../../../utils/json.util";

export abstract class ManualTestsStatusTreeBase {

    path: Path;
    name: string;
    status: ManualTestStatus;
}
