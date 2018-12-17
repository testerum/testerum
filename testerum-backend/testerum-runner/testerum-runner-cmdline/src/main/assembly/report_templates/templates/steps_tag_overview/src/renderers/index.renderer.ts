import {ReportModelExtractor} from "../../../../common/testerum-model/model-extractor/report-model-extractor";
import {FsUtils} from "../../../../common/util/FsUtils";
import * as path from "path";
import {Templates} from "../templates/Templates";
import {ReportStep} from "../../../../common/testerum-model/model/report/report-step";
import {ExecutionStatus} from "../../../../common/testerum-model/model/report/execution-status";

export class IndexRenderer {

    render(destinationDirectory: string, reportModelExtractor: ReportModelExtractor) {
        let statusByTagMap: Map<string, ExecutionStatus> = this.extractStatusByTagMap(reportModelExtractor);

        // render index
        FsUtils.writeFile(
            path.resolve(destinationDirectory, "index.html"),
            Templates.INDEX({statusByTagMap: statusByTagMap, ExecutionStatus: ExecutionStatus})
        );
    }

    private extractStatusByTagMap(reportModelExtractor: ReportModelExtractor): Map<string, ExecutionStatus> {
        let result = new Map<string, ExecutionStatus>();

        const reportStepsByTag: Map<string, Array<ReportStep>> = reportModelExtractor.getReportStepsMapByTag();
        let tags = Array.from(reportStepsByTag.keys());
        tags.sort();

        for (const tag of tags) {
            let status: ExecutionStatus = this.extractStatusFromReportSteps(reportStepsByTag.get(tag));

            result.set(tag, status);
        }

        return result;
    }

    private extractStatusFromReportSteps(reportSteps: Array<ReportStep>): ExecutionStatus {
        let result: ExecutionStatus = ExecutionStatus.SKIPPED;

        for (const reportStep of reportSteps) {
            if (result < reportStep.status) {
                result = reportStep.status;
            }
        }

        return result;
    }
}
