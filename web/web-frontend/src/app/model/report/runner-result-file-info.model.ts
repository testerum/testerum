import {Path} from "../infrastructure/path/path.model";
import {ExecutionStatusEnum} from "../test/event/enums/execution-status.enum";
import {Serializable} from "../infrastructure/serializable.model";

export class RunnerResultFileInfo implements Serializable<RunnerResultFileInfo> {

    path: Path;
    name: string;
    executionResult: ExecutionStatusEnum;
    durationMillis: number;

    deserialize(input: Object): RunnerResultFileInfo {
        this.path = Path.deserialize(input["path"]);
        this.name = input["name"];

        let executionResultAsString:string = input["executionResult"];
        this.executionResult = ExecutionStatusEnum[executionResultAsString];

        this.durationMillis = input["durationMillis"];
        return this;
    }

    serialize(): string {
        return null;
    }
}
