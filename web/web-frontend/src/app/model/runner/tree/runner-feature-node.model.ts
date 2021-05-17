import { RunnerTreeDeserializationUtil } from "./util/runner-tree-deserialization.util";
import { Path } from "../../infrastructure/path/path.model";
import { RunnerTestOrFeatureNode } from "./runner-test-or-feature-node.model";
import {Serializable} from "../../infrastructure/serializable.model";
import {RunnerStepNode} from "./runner-step-node.model";
import {RunnerHooksNode} from "./runner-hooks-node.model";

export class RunnerFeatureNode implements RunnerTestOrFeatureNode, Serializable<RunnerFeatureNode> {
    id: string;
    path: Path;
    name: string;
    beforeAllHooks: RunnerHooksNode;
    afterAllHooks: RunnerHooksNode;

    children: Array<RunnerTestOrFeatureNode> = [];

    deserialize(input: Object): RunnerFeatureNode {
        this.id = input["id"];
        this.path = Path.deserialize(input["path"]);
        this.name = input["name"];
        this.beforeAllHooks = RunnerTreeDeserializationUtil.deserializeRunnerHooksNodes(input["beforeAllHooks"] || []);
        this.afterAllHooks = RunnerTreeDeserializationUtil.deserializeRunnerHooksNodes(input["afterAllHooks"] || []);

        this.children = RunnerTreeDeserializationUtil.deserializeRunnerTestOrFeatureNodes(input["children"] || []);

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
