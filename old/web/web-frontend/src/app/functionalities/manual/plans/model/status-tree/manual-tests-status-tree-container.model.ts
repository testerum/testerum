import {Path} from "../../../../../model/infrastructure/path/path.model";
import {ManualTestStatus} from "../enums/manual-test-status.enum";
import {Serializable} from "../../../../../model/infrastructure/serializable.model";
import {ManualTestsStatusTreeBase} from "./manual-tests-status-tree-base.model";
import {ManualTestsStatusTreeNode} from "./manual-tests-status-tree-node.model";

export class ManualTestsStatusTreeContainer extends ManualTestsStatusTreeBase implements Serializable<ManualTestsStatusTreeContainer>{

    path: Path;
    name: string;
    status: ManualTestStatus;

    children: ManualTestsStatusTreeBase[] = [];

    deserialize(input: Object): ManualTestsStatusTreeContainer {
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
