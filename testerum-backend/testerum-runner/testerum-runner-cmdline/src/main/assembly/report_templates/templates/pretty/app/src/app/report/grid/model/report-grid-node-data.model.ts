import {ExecutionStatus} from "../../../../../../../../common/testerum-model/model/report/execution-status";
import {ReportGridNodeType} from "./enums/report-grid-node-type.enum";
import {ExceptionDetail} from "../../../../../../../../common/testerum-model/model/exception/exception-detail";

export class ReportGridNodeData {
    textAsHtml: string;
    status: ExecutionStatus;
    durationMillis: number;
    textLogFilePath: string;
    modelLogFilePath: string;
    exceptionDetail: ExceptionDetail;
    nodeType: ReportGridNodeType;
    tags: string[] = [];
}
