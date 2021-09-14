export interface RunnerReportNode {
    readonly textLogFilePath: string;
    readonly modelLogFilePath: string;
}

export enum RunnerReportNodeType {
    SUITE,
    FEATURE,
    TEST,
    PARAMETRIZED_TEST,
    SCENARIO,
    STEP,
    REPORT_HOOKS
}
