import { RunnerTreeDeserializationUtil } from "./util/runner-tree-deserialization.util";
import { Path } from "../../infrastructure/path/path.model";
import { RunnerTestOrFeatureNode } from "./runner-test-or-feature-node.model";
import {Serializable} from "../../infrastructure/serializable.model";

export class RunnerFeatureNode implements RunnerTestOrFeatureNode, Serializable<RunnerFeatureNode> {
    id: string;
    path: Path;
    name: string;
    children: Array<RunnerTestOrFeatureNode> = [];

    deserialize(input: Object): RunnerFeatureNode {
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
