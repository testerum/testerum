import { Path } from "../../infrastructure/path/path.model";
import { RunnerTreeDeserializationUtil } from "./util/runner-tree-deserialization.util";
import { RunnerTestOrFeatureNode } from "./runner-test-or-feature-node.model";
import { RunnerNode } from "./runner-node.model";
import {Serializable} from "../../infrastructure/serializable.model";

export class RunnerRootNode implements RunnerNode, Serializable<RunnerRootNode> {
    id: string;
    path: Path;
    name: string;
    children: Array<RunnerTestOrFeatureNode> = [];

    deserialize(input: Object): RunnerRootNode {
        this.id = input["id"];
        this.path = Path.deserialize(input["path"]);
        this.name = input["name"];
        this.children = RunnerTreeDeserializationUtil.deserializeRunnerTestOrFeatureNodes(input["children"] || []);

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
