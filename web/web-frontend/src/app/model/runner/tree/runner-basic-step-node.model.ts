import {Path} from "../../infrastructure/path/path.model";
import {StepCall} from "../../step/step-call.model";
import {RunnerStepNode} from "./runner-step-node.model";
import {Serializable} from "../../infrastructure/serializable.model";

export class RunnerBasicStepNode implements RunnerStepNode, Serializable<RunnerBasicStepNode> {
    id: string;
    path: Path;
    stepCall: StepCall;

    deserialize(input: Object): RunnerBasicStepNode {
        this.id = input["id"];
        this.path = Path.deserialize(input["path"]);
        this.stepCall = new StepCall().deserialize(input["stepCall"]);

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
