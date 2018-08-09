import { RunnerNode } from "./runner-node.model";
import { Path } from "../../infrastructure/path/path.model";
import { RunnerContainerNode } from "./runner-container-node.model";
import { RunnerTreeDeserializationUtil } from "./util/runner-tree-deserialization.util";

export class RunnerComposedStepNode implements Serializable<RunnerComposedStepNode>, RunnerContainerNode {
    id: string;
    name: string;
    path: Path;
    children: Array<RunnerNode> = [];

    deserialize(input: Object): RunnerComposedStepNode {
        this.id = input["id"];
        this.name = input["name"];
        this.path = Path.deserialize(input["path"]);
        this.children = RunnerTreeDeserializationUtil.deserialize(input["children"] || []);

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
