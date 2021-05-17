import { RunnerTreeDeserializationUtil } from "./util/runner-tree-deserialization.util";
import { Path } from "../../infrastructure/path/path.model";
import { RunnerTestOrFeatureNode } from "./runner-test-or-feature-node.model";
import {Serializable} from "../../infrastructure/serializable.model";
import {RunnerStepNode} from "./runner-step-node.model";
import {RunnerNode} from "./runner-node.model";

export class RunnerHooksNode implements RunnerNode, Serializable<RunnerHooksNode> {
    id: string;
    path: Path;
    name: string;

    children: Array<RunnerTestOrFeatureNode> = [];

    deserialize(input: Object): RunnerHooksNode {
        this.id = input["id"];
        this.path = Path.deserialize(input["path"]);
        this.name = input["name"];

        this.children = RunnerTreeDeserializationUtil.deserializeRunnerStepNodes(input["children"] || []);

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
