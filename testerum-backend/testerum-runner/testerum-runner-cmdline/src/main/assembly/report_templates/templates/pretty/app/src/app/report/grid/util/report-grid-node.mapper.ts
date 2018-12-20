import {ReportSuite} from "../../../../../../../../common/testerum-model/model/report/report-suite";
import {ReportGridNode} from "../model/report-grid-node.model";
import {ReportGridNodeData} from "../model/report-grid-node-data.model";
import {DateUtil} from "../../../util/date.util";
import {ReportFeature} from "../../../../../../../../common/testerum-model/model/report/report-feature";
import {ReportTest} from "../../../../../../../../common/testerum-model/model/report/report-test";
import {ReportStep} from "../../../../../../../../common/testerum-model/model/report/report-step";
import {ExecutionStatus} from "../../../../../../../../common/testerum-model/model/report/execution-status";
import {StepCallUtil} from "../../../util/step-call.util";
import {ReportGridNodeType} from "../model/enums/report-grid-node-type.enum";
import {ComposedStepDef} from "../../../../../../../../common/testerum-model/model/step/def/composed-step-def";
import {BasicStepDef} from "../../../../../../../../common/testerum-model/model/step/def/basic-step-def";
import {UndefinedStepDef} from "../../../../../../../../common/testerum-model/model/step/def/undefined-step-def";

export class ReportGridNodeMapper {

    static map(suite: ReportSuite): ReportGridNode {
        let node = new ReportGridNode();
        node.leaf = false;
        node.expanded = true;
        node.data = new ReportGridNodeData();

        node.data.textAsHtml = "Test Suite - " + DateUtil.dateTimeToShortString(suite.startTime);
        node.data.status = suite.status;
        node.data.durationMillis = suite.durationMillis;
        node.data.logs = suite.logs;
        node.data.nodeType = ReportGridNodeType.SUITE;

        for (const testOrFeature of suite.children) {
            if (testOrFeature instanceof ReportFeature) {
                node.children.push(
                    this.mapFeature(testOrFeature)
                );
                continue;
            }
            if (testOrFeature instanceof ReportTest) {
                node.children.push(
                    this.mapTest(testOrFeature)
                );
            }
        }

        return node;
    }

    private static mapFeature(feature: ReportFeature): ReportGridNode {
        let node = new ReportGridNode();
        node.leaf = false;
        node.expanded = true;

        node.data = new ReportGridNodeData();
        node.data.textAsHtml = feature.featureName;
        node.data.status = feature.status;
        node.data.durationMillis = feature.durationMillis;
        node.data.logs = feature.logs;
        node.data.exceptionDetail = feature.exceptionDetail;
        node.data.nodeType = ReportGridNodeType.FEATURE;

        for (const testOrFeature of feature.children) {
            if (testOrFeature instanceof ReportFeature) {
                node.children.push(
                    this.mapFeature(testOrFeature)
                );
                continue;
            }
            if (testOrFeature instanceof ReportTest) {
                node.children.push(
                    this.mapTest(testOrFeature)
                );
            }
        }

        return node;
    }

    private static mapTest(test: ReportTest): ReportGridNode {
        let node = new ReportGridNode();
        node.leaf = !(test.children && test.children.length > 0);
        node.expanded = !node.leaf && this.hasAnFailureStatus(test.status);

        node.data = new ReportGridNodeData();
        node.data.textAsHtml = test.testName;
        node.data.status = test.status;
        node.data.durationMillis = test.durationMillis;
        node.data.logs = test.logs;
        node.data.exceptionDetail = test.exceptionDetail;
        node.data.nodeType = ReportGridNodeType.TEST;

        for (const step of test.children) {
            node.children.push(
                this.mapSteps(step)
            );
        }

        return node;
    }

    private static mapSteps(step: ReportStep): ReportGridNode {
        let node = new ReportGridNode();
        node.leaf = !(step.children && step.children.length > 0);
        node.expanded = !node.leaf && this.hasAnFailureStatus(step.status);

        node.data = new ReportGridNodeData();
        node.data.textAsHtml = StepCallUtil.getStepCallAsHtmlText(step.stepCall);
        node.data.status = step.status;
        node.data.durationMillis = step.durationMillis;
        node.data.logs = step.logs;
        node.data.exceptionDetail = step.exceptionDetail;
        if (step.stepCall.stepDef instanceof ComposedStepDef) {
            node.data.nodeType = ReportGridNodeType.COMPOSED_STEP;
        }
        if (step.stepCall.stepDef instanceof BasicStepDef) {
            node.data.nodeType = ReportGridNodeType.BASIC_STEP;
        }
        if (step.stepCall.stepDef instanceof UndefinedStepDef) {
            node.data.nodeType = ReportGridNodeType.UNDEFINED_STEP;
        }

        for (const subStep of step.children) {
            node.children.push(
                this.mapSteps(subStep)
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
}
