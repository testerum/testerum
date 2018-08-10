import { Path } from "../../infrastructure/path/path.model";
import { RunnerTreeDeserializationUtil } from "./util/runner-tree-deserialization.util";
import { RunnerStepNode } from "./runner-step-node.model";
import { StepCall } from "../../step-call.model";

export class RunnerComposedStepNode implements Serializable<RunnerComposedStepNode>, RunnerStepNode {
    id: string;
    path: Path;
    stepCall: StepCall;
    children: Array<RunnerStepNode> = [];

    deserialize(input: Object): RunnerComposedStepNode {
        this.id = input["id"];
        this.path = Path.deserialize(input["path"]);
        this.stepCall = new StepCall().deserialize(input["stepCall"]);
        this.children = RunnerTreeDeserializationUtil.deserializeRunnerStepNodes(input["children"] || []);

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
