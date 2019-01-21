import {Serializable} from "../infrastructure/serializable.model";
import {RunnerResultDirInfo} from "./runner-result-dir-info.model";

export class RunnerResultsInfoModel implements Serializable<RunnerResultsInfoModel> {

    reportDirs: Array<RunnerResultDirInfo> = [];

    deserialize(input: Object): RunnerResultsInfoModel {
        const reportDirs = [];
        for (let dir of input["reportDirs"] || []) {
            reportDirs.push(new RunnerResultDirInfo().deserialize(dir));
        }
        this.reportDirs = reportDirs;


        return this;
    }

    serialize(): string {
        throw Error("serialize is not implemented")
    }

}
