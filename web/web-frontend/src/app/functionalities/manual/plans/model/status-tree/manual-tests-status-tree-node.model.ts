import {Path} from "../../../../../model/infrastructure/path/path.model";
import {ManualTestStatus} from "../enums/manual-test-status.enum";
import {Serializable} from "../../../../../model/infrastructure/serializable.model";
import {ManualExecPlanStatus} from "../enums/manual-exec-plan-status.enum";
import {ManualTreeTest} from "../manual-tree-test.model";
import {JsonUtil} from "../../../../../utils/json.util";
import {ManualTestsStatusTreeBase} from "./manual-tests-status-tree-base.model";

export class ManualTestsStatusTreeNode extends ManualTestsStatusTreeBase implements Serializable<ManualTestsStatusTreeNode>{

    path: Path;
    name: string;
    status: ManualTestStatus;

    deserialize(input: Object): ManualTestsStatusTreeNode {
        this.path = Path.deserialize(input["path"]);
        this.name = input['name'];
        this.status = ManualExecPlanStatus.fromString(input['status']);

        return this;
    }

    serialize(): string {
        throw new Error("Not implemented");
    }
}
