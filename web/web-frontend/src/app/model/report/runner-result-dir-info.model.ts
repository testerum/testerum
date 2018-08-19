import {RunnerResultFileInfo} from "./runner-result-file-info.model";
import {Serializable} from "../infrastructure/serializable.model";

export class RunnerResultDirInfo implements Serializable<RunnerResultDirInfo> {

    directoryName: string;
    runnerResultFilesInfo: Array<RunnerResultFileInfo> = [];

    deserialize(input: Object): RunnerResultDirInfo {
        this.directoryName = input["directoryName"];

        for (let runnerResultFileInfo of input["runnerResultFilesInfo"] || []) {
            this.runnerResultFilesInfo.push(new RunnerResultFileInfo().deserialize(runnerResultFileInfo));
        }
        return this;
    }

    serialize(): string {
        return null;
    }
}
