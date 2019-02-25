import {RunnerResultFileInfo} from "./runner-result-file-info.model";
import {Serializable} from "../infrastructure/serializable.model";

export class RunnerResultDirInfo implements Serializable<RunnerResultDirInfo> {

    directoryName: string;
    runnerResultFilesInfo: Array<RunnerResultFileInfo> = [];

    deserialize(input: Object): RunnerResultDirInfo {
        this.directoryName = input["directoryName"];

        const runnerResultFilesInfo = [];
        for (let runnerResultFileInfo of input["runnerResultFilesInfo"] || []) {
            runnerResultFilesInfo.push(new RunnerResultFileInfo().deserialize(runnerResultFileInfo));
        }
        this.runnerResultFilesInfo = runnerResultFilesInfo;

        return this;
    }

    serialize(): string {
        throw Error("serialize is not implemented")
    }
}
