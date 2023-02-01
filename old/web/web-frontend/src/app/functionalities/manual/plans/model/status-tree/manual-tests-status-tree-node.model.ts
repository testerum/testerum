import {Path} from "../../../../../model/infrastructure/path/path.model";
import {ManualTestStatus} from "../enums/manual-test-status.enum";
import {Serializable} from "../../../../../model/infrastructure/serializable.model";
import {ManualTestsStatusTreeBase} from "./manual-tests-status-tree-base.model";

export class ManualTestsStatusTreeNode extends ManualTestsStatusTreeBase implements Serializable<ManualTestsStatusTreeNode>{

    path: Path;
    name: string;
    status: ManualTestStatus;

    deserialize(input: Object): ManualTestsStatusTreeNode {
        this.path = Path.deserialize(input["path"]);
        this.name = input['name'];
        this.status = ManualTestStatus.fromString(input['status']);

        return this;
    }

    serialize(): string {
        throw new Error("Not implemented");
    }
}
