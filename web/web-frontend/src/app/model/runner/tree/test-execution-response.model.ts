import { RunnerRootNode } from "./runner-root-node.model";

export class TestExecutionResponse implements Serializable<TestExecutionResponse>{
    executionId: number;
    runnerRootNode: RunnerRootNode;

    deserialize(input: Object): TestExecutionResponse {
        this.executionId = input["executionId"];
        this.runnerRootNode = new RunnerRootNode().deserialize(input["runnerRootNode"]);

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }

}
