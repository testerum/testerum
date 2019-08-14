import { Path } from "../../infrastructure/path/path.model";
import { RunnerTreeDeserializationUtil } from "./util/runner-tree-deserialization.util";
import { RunnerTestOrFeatureNode } from "./runner-test-or-feature-node.model";
import { RunnerStepNode } from "./runner-step-node.model";
import {Serializable} from "../../infrastructure/serializable.model";
import {RunnerScenarioNode} from "./runner-scenario-node.model";

export class RunnerParametrizedTestNode implements RunnerTestOrFeatureNode, Serializable<RunnerParametrizedTestNode> {
    id: string;
    path: Path;
    name: string;
    children: Array<RunnerScenarioNode> = [];

    deserialize(input: Object): RunnerParametrizedTestNode {
        this.id = input["id"];
        this.name = input["name"];
        this.path = Path.deserialize(input["path"]);

        this.children = RunnerTreeDeserializationUtil.deserializeRunnerScenariosNodes(input["children"] || []);

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
