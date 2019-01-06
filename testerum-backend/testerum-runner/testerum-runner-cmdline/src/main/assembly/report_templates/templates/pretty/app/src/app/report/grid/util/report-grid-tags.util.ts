import {ReportGridNode} from "../model/report-grid-node.model";
import {ArrayUtil} from "../../../util/array.util";

export class ReportGridTagsUtil {

    static getTags(reportNodes: ReportGridNode[]): string [] {
        let result: string[] = [];

        ReportGridTagsUtil.mapNodeTagsAndChildTagsToResult(reportNodes, result);
        for (const reportNode of reportNodes) {
            ReportGridTagsUtil.mapNodeParentTagsToResult(reportNode.parent, result);
        }
        ArrayUtil.sort(result);

        return result;
    }

    private static mapNodeTagsAndChildTagsToResult(reportNodes: ReportGridNode[], result: string[]) {
        for (const reportNode of reportNodes) {
            this.addNodeTagsToResult(reportNode, result);
            ReportGridTagsUtil.mapNodeTagsAndChildTagsToResult(reportNode.children as ReportGridNode[], result);
        }
    }

    private static addNodeTagsToResult(reportNode, result: string[]) {
        let nodeTags = reportNode.data.tags;

        for (const tag of nodeTags) {
            if (!ArrayUtil.containsElement(result, tag)) {
                result.push(tag)
            }
        }
    }

    private static mapNodeParentTagsToResult(reportNode: ReportGridNode, result: string[]) {
        if (!reportNode) {
            return;
        }

        this.addNodeTagsToResult(reportNode, result);
        this.mapNodeParentTagsToResult(reportNode.parent, result);
    }
}
