import {ReportGridNode} from "../model/report-grid-node.model";
import {ArrayUtil} from "../../../util/array.util";

export class ReportGridTagsUtil {

    static getTags(reportNodes: ReportGridNode[]): string [] {
        let result: string[] = [];

        ReportGridTagsUtil.mapTagsToResult(reportNodes, result);
        ArrayUtil.sort(result);

        return result;
    }

    private static mapTagsToResult(reportNodes: ReportGridNode[], result: string[]) {
        for (const reportNode of reportNodes) {
            let nodeTags = reportNode.data.tags;

            for (const tag of nodeTags) {
                if (!ArrayUtil.containsElement(result, tag)) {
                    result.push(tag)
                }
            }
            ReportGridTagsUtil.mapTagsToResult(reportNode.children as ReportGridNode[], result);
        }
    }
}
