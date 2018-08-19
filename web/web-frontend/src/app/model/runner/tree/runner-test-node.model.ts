import { Path } from "../../infrastructure/path/path.model";
import { RunnerTreeDeserializationUtil } from "./util/runner-tree-deserialization.util";
import { RunnerTestOrFeatureNode } from "./runner-test-or-feature-node.model";
import { RunnerStepNode } from "./runner-step-node.model";
import {Serializable} from "../../infrastructure/serializable.model";

export class RunnerTestNode implements RunnerTestOrFeatureNode, Serializable<RunnerTestNode> {
    id: string;
    path: Path;
    name: string;
    children: Array<RunnerStepNode> = [];

    deserialize(input: Object): RunnerTestNode {
        this.id = input["id"];
        this.name = input["name"];
        this.path = Path.deserialize(input["path"]);

        this.children = RunnerTreeDeserializationUtil.deserializeRunnerStepNodes(input["children"] || []);

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
