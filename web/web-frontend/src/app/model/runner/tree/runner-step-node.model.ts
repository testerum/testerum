import { RunnerNode } from "./runner-node.model";
import { StepCall } from "../../step-call.model";

export interface RunnerStepNode extends RunnerNode {
    stepCall: StepCall
}
