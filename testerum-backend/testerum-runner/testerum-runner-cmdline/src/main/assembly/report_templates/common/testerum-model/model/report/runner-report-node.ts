export interface RunnerReportNode {
    readonly textLogFilePath: string;
    readonly modelLogFilePath: string;
}

export enum RunnerReportNodeType {
    SUITE,
    FEATURE,
    TEST,
    STEP
}
