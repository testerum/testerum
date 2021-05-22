import {RunnerNode} from "./runner-node.model";
import {RunnerStepNode} from "./runner-step-node.model";
import {Serializable} from "../../infrastructure/serializable.model";
import {Path} from "../../infrastructure/path/path.model";
import {RunnerTreeDeserializationUtil} from "./util/runner-tree-deserialization.util";
import {RunnerHooksNode} from "./runner-hooks-node.model";

export class RunnerScenarioNode implements RunnerNode, Serializable<RunnerScenarioNode> {
    id: string;
    path: Path;
    scenarioIndex: number;
    name: string;
    enabled: boolean;
    beforeEachHooks: RunnerHooksNode;
    afterEachHooks: RunnerHooksNode;
    afterHooks: RunnerHooksNode;

    children: Array<RunnerStepNode> = [];

    deserialize(input: Object): RunnerScenarioNode {
        this.id = input["id"];
        this.path = Path.deserialize(input["path"]);
        this.scenarioIndex = input["scenarioIndex"];
        this.name = input["name"];
        this.enabled = input["enabled"];
        this.beforeEachHooks = RunnerTreeDeserializationUtil.deserializeRunnerHooksNodes(input["beforeEachHooks"] || []);
        this.afterEachHooks = RunnerTreeDeserializationUtil.deserializeRunnerHooksNodes(input["afterEachHooks"] || []);
        this.afterHooks = RunnerTreeDeserializationUtil.deserializeRunnerHooksNodes(input["afterHooks"] || []);

        this.children = RunnerTreeDeserializationUtil.deserializeRunnerStepNodes(input["children"] || []);

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
