import { Path } from "../../infrastructure/path/path.model";
import { StepCall } from "../../step-call.model";
import { RunnerStepNode } from "./runner-step-node.model";

export class RunnerBasicStepNode implements RunnerStepNode, Serializable<RunnerBasicStepNode> {
    id: string;
    path: Path;
    name: string;
    stepCall: StepCall;

    deserialize(input: Object): RunnerBasicStepNode {
        this.id = input["id"];
        this.name = input["name"];
        this.path = Path.deserialize(input["path"]);
        this.stepCall = new StepCall().deserialize(input["stepCall"]);

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
