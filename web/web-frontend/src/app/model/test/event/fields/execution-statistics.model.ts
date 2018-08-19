import {Serializable} from "../../../infrastructure/serializable.model";

export class ExecutionStatistics implements Serializable<ExecutionStatistics> {

    failedTestsCount: number;
    successfulTestsCount: number;
    totalTestsCount: number;

    deserialize(input: Object): ExecutionStatistics {
        this.failedTestsCount = input["failedTestsCount"];
        this.successfulTestsCount = input["successfulTestsCount"];
        this.totalTestsCount = input["totalTestsCount"];

        return this;
    }

    serialize(): string {
        return "";
    }
}
