import {ReportLog} from "./report-log";

export interface RunnerReportNode {
    readonly logs: Array<ReportLog>;
}

export enum RunnerReportNodeType {
    SUITE,
    FEATURE,
    TEST,
    STEP
}
