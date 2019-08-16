import { RunnerNode } from "./runner-node.model";
import { StepCall } from "../../step-call.model";
import {Scenario} from "../../test/scenario/scenario.model";
import {RunnerStepNode} from "./runner-step-node.model";
import {Serializable} from "../../infrastructure/serializable.model";
import {Path} from "../../infrastructure/path/path.model";
import {RunnerTreeDeserializationUtil} from "./util/runner-tree-deserialization.util";

export class RunnerScenarioNode implements RunnerNode, Serializable<RunnerScenarioNode> {
    id: string;
    path: Path;
    name: string;
    enabled: boolean;

    children: Array<RunnerStepNode> = [];

    deserialize(input: Object): RunnerScenarioNode {
        this.id = input["id"];
        this.path = Path.deserialize(input["path"]);
        this.name = input["name"];
        this.enabled = input["enabled"];

        this.children = RunnerTreeDeserializationUtil.deserializeRunnerStepNodes(input["children"] || []);

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
