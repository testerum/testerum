import {ReportSuite} from "../../../../../../../../common/testerum-model/report-model/model/report/report-suite";
import {ReportGridNode} from "../model/report-grid-node.model";
import {ReportGridNodeData} from "../model/report-grid-node-data.model";
import {DateUtil} from "../../../util/date.util";
import {ReportFeature} from "../../../../../../../../common/testerum-model/report-model/model/report/report-feature";
import {ReportTest} from "../../../../../../../../common/testerum-model/report-model/model/report/report-test";
import {ReportStep} from "../../../../../../../../common/testerum-model/report-model/model/report/report-step";
import {ExecutionStatus} from "../../../../../../../../common/testerum-model/report-model/model/report/execution-status";
import {StepCallUtil} from "../../../util/step-call.util";
import {ReportGridNodeType} from "../model/enums/report-grid-node-type.enum";
import {ReportComposedStepDef} from "../../../../../../../../common/testerum-model/report-model/model/step/def/report-composed-step-def";
import {ReportBasicStepDef} from "../../../../../../../../common/testerum-model/report-model/model/step/def/report-basic-step-def";
import {ReportUndefinedStepDef,} from "../../../../../../../../common/testerum-model/report-model/model/step/def/report-undefined-step-def";
import {ReportGridFilter} from "../model/report-grid-filter.model";
import {ReportGridTagsUtil} from "./report-grid-tags.util";
import {ArrayUtil} from "../../../util/array.util";
import {ReportStepDef} from "../../../../../../../../common/testerum-model/report-model/model/step/def/report-step-def";

export class ReportGridNodeMapper {

    private static suite: ReportSuite;
    static map(suite: ReportSuite, filter: ReportGridFilter): ReportGridNode[] {
        ReportGridNodeMapper.suite = suite;

        let node = new ReportGridNode();
        node.leaf = false;
        node.expanded = true;
        node.data = new ReportGridNodeData();

        node.data.textAsHtml = "Test Suite - " + DateUtil.dateTimeToShortString(suite.startTime);
        node.data.status = suite.status;
        node.data.durationMillis = suite.durationMillis;
        node.data.textLogFilePath = suite.textLogFilePath;
        node.data.modelLogFilePath = suite.modelLogFilePath;
        node.data.nodeType = ReportGridNodeType.SUITE;

        for (const testOrFeature of suite.children) {
            if (testOrFeature instanceof ReportFeature) {
                let featureChildren: ReportGridNode[] = this.mapFeature(testOrFeature, filter, node);
                for (const featureChild of featureChildren) {
                    node.children.push(featureChild);
                }
                continue;
            }
            if (testOrFeature instanceof ReportTest) {
                let test = this.mapTest(testOrFeature, filter, node);
                if (test) {
                    node.children.push(
                        test
                    );
                }
            }
        }

        let result: ReportGridNode[] = [];
        result.push(node);
        return result;
    }

    private static mapFeature(feature: ReportFeature, filter: ReportGridFilter, parentNode: ReportGridNode): ReportGridNode[] {

        let node = new ReportGridNode();
        node.parent = parentNode;
        node.leaf = false;
        node.expanded = true;

        node.data = new ReportGridNodeData();
        node.data.textAsHtml = feature.featureName;
        node.data.status = feature.status;
        node.data.durationMillis = feature.durationMillis;
        node.data.textLogFilePath = feature.textLogFilePath;
        node.data.modelLogFilePath = feature.modelLogFilePath;
        node.data.nodeType = ReportGridNodeType.FEATURE;
        node.data.tags = feature.tags;

        for (const testOrFeature of feature.children) {
            if (testOrFeature instanceof ReportFeature) {
                let featureChildren: ReportGridNode[] = this.mapFeature(testOrFeature, filter, node);
                for (const featureChild of featureChildren) {
                    featureChild.parent = node;
                    node.children.push(featureChild);
                }
                continue;
            }
            if (testOrFeature instanceof ReportTest) {
                let test = this.mapTest(testOrFeature, filter, node);
                if (test) {
                    test.parent = node;
                    node.children.push(
                        test
                    );
                }
            }
        }

        if (!filter.areTestFoldersShown) {
            return node.children as ReportGridNode[];
        }


        let result: ReportGridNode[] = [];

        if (node.children.length == 0) {
            return result;
        }

        if(filter.selectedTags.length != 0) {
            if(!this.nodeOrParentOrSubNodesMatchesAnyOfTheTags(node, filter.selectedTags)) {
                return result
            }
        }

        result.push(node);
        return result;
    }

    private static mapTest(test: ReportTest, filter: ReportGridFilter, parentNode: ReportGridNode): ReportGridNode {
        if (test.status == ExecutionStatus.PASSED && !filter.showPassed ||
            test.status == ExecutionStatus.FAILED && !filter.showFailed ||
            test.status == ExecutionStatus.DISABLED && !filter.showDisabled ||
            test.status == ExecutionStatus.UNDEFINED && !filter.showUndefined ||
            test.status == ExecutionStatus.SKIPPED && !filter.showSkipped) {
            return null;
        }

        let node = new ReportGridNode();
        node.parent = parentNode;
        node.leaf = !(test.children && test.children.length > 0);
        node.expanded = false; //!node.leaf && this.hasAnFailureStatus(test.status);

        node.data = new ReportGridNodeData();
        node.data.textAsHtml = test.testName;
        node.data.status = test.status;
        node.data.durationMillis = test.durationMillis;
        node.data.textLogFilePath = test.textLogFilePath;
        node.data.modelLogFilePath = test.modelLogFilePath;
        node.data.nodeType = ReportGridNodeType.TEST;
        node.data.tags = test.tags;

        for (const step of test.children) {
            let stepNode = this.mapSteps(step, filter, node);
            stepNode.parent = node;

            node.children.push(
                stepNode
            );
        }

        if(filter.selectedTags.length != 0) {
            if(!this.nodeOrParentOrSubNodesMatchesAnyOfTheTags(node, filter.selectedTags)) {
                return null;
            }
        }

        return node;
    }

    private static mapSteps(step: ReportStep, filter: ReportGridFilter, parentNode: ReportGridNode): ReportGridNode {
        let reportStepDef: ReportStepDef = ReportGridNodeMapper.suite.stepDefsById.get(step.stepCall.stepDefId);

        let node = new ReportGridNode();
        node.parent = parentNode;
        node.leaf = !(step.children && step.children.length > 0);
        node.expanded = !node.leaf && this.hasAnFailureStatus(step.status);

        node.data = new ReportGridNodeData();
        node.data.textAsHtml = StepCallUtil.getStepCallAsHtmlText(step.stepCall, reportStepDef);
        node.data.status = step.status;
        node.data.durationMillis = step.durationMillis;
        node.data.textLogFilePath = step.textLogFilePath;
        node.data.modelLogFilePath = step.modelLogFilePath;

        if (reportStepDef instanceof ReportComposedStepDef) {
            node.data.tags = reportStepDef.tags;
        }

        if (reportStepDef instanceof ReportComposedStepDef) {
            node.data.nodeType = ReportGridNodeType.COMPOSED_STEP;
        }
        if (reportStepDef instanceof ReportBasicStepDef) {
            node.data.nodeType = ReportGridNodeType.BASIC_STEP;
        }
        if (reportStepDef instanceof ReportUndefinedStepDef) {
            node.data.nodeType = ReportGridNodeType.UNDEFINED_STEP;
        }

        for (const subStep of step.children) {
            let stepNode = this.mapSteps(subStep, filter, node);
            stepNode.parent = node;

            node.children.push(
                stepNode
            );
        }

        return node;
    }

    private static hasAnFailureStatus(status: ExecutionStatus): boolean {
        switch (status) {
            case ExecutionStatus.FAILED: return true;
            case ExecutionStatus.UNDEFINED: return true;
        }
        return false;
    }

    private static nodeOrParentOrSubNodesMatchesAnyOfTheTags(node: ReportGridNode, selectedTags: Array<string>): boolean {
        let nodeTags = ReportGridTagsUtil.getTags([node]);
        for (const selectedTag of selectedTags) {
            if(ArrayUtil.containsElement(nodeTags, selectedTag)) {
                return true;
            }
        }
        return false;
    }
}
