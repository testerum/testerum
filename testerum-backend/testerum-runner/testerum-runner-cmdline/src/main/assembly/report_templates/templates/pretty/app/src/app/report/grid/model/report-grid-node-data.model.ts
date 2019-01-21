import {ExecutionStatus} from "../../../../../../../../common/testerum-model/report-model/model/report/execution-status";
import {ReportGridNodeType} from "./enums/report-grid-node-type.enum";

export class ReportGridNodeData {
    textAsHtml: string;
    status: ExecutionStatus;
    durationMillis: number;
    textLogFilePath: string;
    modelLogFilePath: string;
    nodeType: ReportGridNodeType;
    tags: string[] = [];
}
