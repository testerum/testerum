import { Path } from "../../infrastructure/path/path.model";
import { StepCall } from "../../step-call.model";
import { RunnerStepNode } from "./runner-step-node.model";

export class RunnerUndefinedStepNode implements RunnerStepNode, Serializable<RunnerUndefinedStepNode> {
    id: string;
    path: Path;
    stepCall: StepCall;

    deserialize(input: Object): RunnerUndefinedStepNode {
        this.id = input["id"];
        this.path = Path.deserialize(input["path"]);
        this.stepCall = new StepCall().deserialize(input["stepCall"]);

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
