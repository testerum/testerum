import {ReportSuite} from "../../../../../../../common/testerum-model/report-model/model/report/report-suite";
import {ReportGridNode} from "../model/report-grid-node.model";
import {ReportGridNodeData} from "../model/report-grid-node-data.model";
import {DateUtil} from "../../../util/date.util";
import {ReportFeature} from "../../../../../../../common/testerum-model/report-model/model/report/report-feature";
import {ReportTest} from "../../../../../../../common/testerum-model/report-model/model/report/report-test";
import {ReportStep} from "../../../../../../../common/testerum-model/report-model/model/report/report-step";
import {ExecutionStatus} from "../../../../../../../common/testerum-model/report-model/model/report/execution-status";
import {StepCallUtil} from "../../../util/step-call.util";
import {ReportGridNodeType} from "../model/enums/report-grid-node-type.enum";
import {ReportComposedStepDef} from "../../../../../../../common/testerum-model/report-model/model/step/def/report-composed-step-def";
import {ReportBasicStepDef} from "../../../../../../../common/testerum-model/report-model/model/step/def/report-basic-step-def";
import {ReportUndefinedStepDef,} from "../../../../../../../common/testerum-model/report-model/model/step/def/report-undefined-step-def";
import {ReportGridFilter} from "../model/report-grid-filter.model";
import {ReportGridTagsUtil} from "./report-grid-tags.util";
import {ArrayUtil} from "../../../util/array.util";
import {ReportStepDef} from "../../../../../../../common/testerum-model/report-model/model/step/def/report-step-def";
import {ReportParametrizedTest} from "../../../../../../../common/testerum-model/report-model/model/report/report-parametrized-test";
import {ReportScenario} from "../../../../../../../common/testerum-model/report-model/model/report/report-scenario";
import {ReportHooks} from "../../../../../../../common/testerum-model/report-model/model/report/report-hooks";
import {HookPhase} from "../../../../../../../common/testerum-model/report-model/model/report/hook-phase";

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
            if (testOrFeature instanceof ReportParametrizedTest) {
                let parametrizedTest = this.mapParametrizedTest(testOrFeature, filter, node);
                if (parametrizedTest) {
                    node.children.push(
                        parametrizedTest
                    );
                }
            }
            if (testOrFeature instanceof ReportHooks) {
                let hooks = this.mapHooks(testOrFeature, filter, node);
                if (hooks) {
                    node.children.push(
                        hooks
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
                    node.children.push(
                        test
                    );
                }
            }
            if (testOrFeature instanceof ReportParametrizedTest) {
                let parametrizedTest = this.mapParametrizedTest(testOrFeature, filter, node);
                if (parametrizedTest) {
                    node.children.push(
                        parametrizedTest
                    );
                }
            }
            if (testOrFeature instanceof ReportHooks) {
                let hooks = this.mapHooks(testOrFeature, filter, node);
                if (hooks) {
                    node.children.push(
                        hooks
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

        for (const testChild of test.children) {
            if (testChild instanceof ReportStep) {
                let stepNode = this.mapSteps(testChild, filter, node);
                stepNode.parent = node;

                node.children.push(
                    stepNode
                );
            }

            if (testChild instanceof ReportHooks) {
                let hooks = this.mapHooks(testChild, filter, node);
                if (hooks) {
                    node.children.push(
                        hooks
                    );
                }
            }
        }

        if(filter.selectedTags.length != 0) {
            if(!this.nodeOrParentOrSubNodesMatchesAnyOfTheTags(node, filter.selectedTags)) {
                return null;
            }
        }

        return node;
    }

    private static mapParametrizedTest(parametrizedTest: ReportParametrizedTest, filter: ReportGridFilter, parentNode: ReportGridNode): ReportGridNode {

        let node = new ReportGridNode();
        node.parent = parentNode;
        node.leaf = !(parametrizedTest.children && parametrizedTest.children.length > 0);
        node.expanded = true; //!node.leaf && this.hasAnFailureStatus(test.status);

        node.data = new ReportGridNodeData();
        node.data.textAsHtml = parametrizedTest.testName;
        node.data.status = parametrizedTest.status;
        node.data.durationMillis = parametrizedTest.durationMillis;
        node.data.textLogFilePath = parametrizedTest.textLogFilePath;
        node.data.modelLogFilePath = parametrizedTest.modelLogFilePath;
        node.data.nodeType = ReportGridNodeType.PARAMETRIZED_TEST;
        node.data.tags = parametrizedTest.tags;

        for (const scenario of parametrizedTest.children) {
            let scenarioNode = this.mapScenario(scenario, filter, node);

            if (scenarioNode) {
                node.children.push(scenarioNode);
            }
        }

        if(filter.selectedTags.length != 0) {
            if(!this.nodeOrParentOrSubNodesMatchesAnyOfTheTags(node, filter.selectedTags)) {
                return null;
            }
        }

        if (node.children.length == 0 && parametrizedTest.children.length != 0) {
            return null;
        }

        return node;
    }

    private static mapScenario(scenario: ReportScenario, filter: ReportGridFilter, parentNode: ReportGridNode): ReportGridNode {

        if (scenario.status == ExecutionStatus.PASSED && !filter.showPassed ||
            scenario.status == ExecutionStatus.FAILED && !filter.showFailed ||
            scenario.status == ExecutionStatus.DISABLED && !filter.showDisabled ||
            scenario.status == ExecutionStatus.UNDEFINED && !filter.showUndefined ||
            scenario.status == ExecutionStatus.SKIPPED && !filter.showSkipped) {
            return null;
        }

        let node = new ReportGridNode();
        node.parent = parentNode;
        node.leaf = !(scenario.children && scenario.children.length > 0);
        node.expanded = false; //!node.leaf && this.hasAnFailureStatus(test.status);

        node.data = new ReportGridNodeData();
        node.data.textAsHtml = scenario.testName;
        node.data.status = scenario.status;
        node.data.durationMillis = scenario.durationMillis;
        node.data.textLogFilePath = scenario.textLogFilePath;
        node.data.modelLogFilePath = scenario.modelLogFilePath;
        node.data.nodeType = ReportGridNodeType.SCENARIO;
        node.data.tags = scenario.tags;

        for (const scenarioChild of scenario.children) {
            if (scenarioChild instanceof ReportStep) {
                let stepNode = this.mapSteps(scenarioChild, filter, node);
                stepNode.parent = node;

                node.children.push(
                    stepNode
                );
            }

            if (scenarioChild instanceof ReportHooks) {
                let hooks = this.mapHooks(scenarioChild, filter, node);
                if (hooks) {
                    node.children.push(
                        hooks
                    );
                }
            }
        }

        if(filter.selectedTags.length != 0) {
            if(!this.nodeOrParentOrSubNodesMatchesAnyOfTheTags(node, filter.selectedTags)) {
                return null;
            }
        }

        return node;
    }

    private static mapHooks(hook: ReportHooks, filter: ReportGridFilter, parentNode: ReportGridNode): ReportGridNode {

        let node = new ReportGridNode();
        node.parent = parentNode;
        node.leaf = !(hook.children && hook.children.length > 0);
        node.expanded = false;

        node.data = new ReportGridNodeData();
        switch (+hook.hookPhase) {
            case HookPhase.BEFORE_ALL_TESTS: node.data.textAsHtml = "Before all hooks"; break;
            case HookPhase.BEFORE_EACH_TEST: node.data.textAsHtml = "Before each hooks"; break;
            case HookPhase.AFTER_EACH_TEST: node.data.textAsHtml = "After each hooks"; break;
            case HookPhase.AFTER_ALL_TESTS: node.data.textAsHtml = "After all hooks"; break;
            case HookPhase.AFTER_TEST: node.data.textAsHtml = "After test hooks"; break;
        }
        node.data.status = hook.status;
        node.data.durationMillis = hook.durationMillis;
        node.data.textLogFilePath = hook.textLogFilePath;
        node.data.modelLogFilePath = hook.modelLogFilePath;
        node.data.nodeType = ReportGridNodeType.HOOKS;

        for (const step of hook.children) {
            let stepNode = this.mapSteps(step, filter, node);
            stepNode.parent = node;

            node.children.push(
                stepNode
            );
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
