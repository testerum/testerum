import {ExecutionStatus} from "../../../../common/testerum-model/model/report/execution-status";

export interface TemplateIndexData {
    statusByTagMap: Map<string, ExecutionStatus>;
    ExecutionStatus: typeof ExecutionStatus;
}
