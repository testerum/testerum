import {Path} from "../../../../../model/infrastructure/path/path.model";
import {ManualTestStatus} from "../enums/manual-test-status.enum";
import {Serializable} from "../../../../../model/infrastructure/serializable.model";
import {ManualExecPlanStatus} from "../enums/manual-exec-plan-status.enum";
import {ManualTestsStatusTreeBase} from "./manual-tests-status-tree-base.model";
import {ManualTestsStatusTreeNode} from "./manual-tests-status-tree-node.model";
import {ManualTestsStatusTreeContainer} from "./manual-tests-status-tree-container.model";

export class ManualTestsStatusTreeRoot extends ManualTestsStatusTreeBase implements Serializable<ManualTestsStatusTreeRoot> {

    path: Path;
    name: string;
    status: ManualTestStatus;

    children: ManualTestsStatusTreeBase[] = [];

    deserialize(input: Object): ManualTestsStatusTreeRoot {
        this.path = Path.deserialize(input["path"]);
        this.name = input['name'];
        this.status = ManualTestStatus.fromString(input['status']);

        for (const child of input['children']) {
            if (child["@type"] == "CONTAINER") {
                this.children.push(
                    new ManualTestsStatusTreeContainer().deserialize(child)
                );
            }
            if (child["@type"] == "NODE") {
                this.children.push(
                    new ManualTestsStatusTreeNode().deserialize(child)
                );
            }
        }

        return this;
    }

    serialize(): string {
        throw new Error("Not implemented");
    }
}
