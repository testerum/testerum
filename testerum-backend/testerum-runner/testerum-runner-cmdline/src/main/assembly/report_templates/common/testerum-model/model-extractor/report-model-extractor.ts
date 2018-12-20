import {ReportSuite} from "../model/report/report-suite";
import {ReportTest} from "../model/report/report-test";
import {RunnerReportNode} from "../model/report/runner-report-node";
import {ReportFeature} from "../model/report/report-feature";
import {ReportStep} from "../model/report/report-step";

export class ReportModelExtractor {

    readonly reportSuite: ReportSuite;

    private reportTests: Array<ReportTest>;
    private reportSteps: Array<ReportStep>;

    constructor(reportSuite: ReportSuite) {
        this.reportSuite = reportSuite;
    }

    public getTests(): Array<ReportTest> {
        if (this.reportTests) {
            return this.reportTests;
        }

        const tests: Array<ReportTest> = [];

        this.addTests(tests, this.reportSuite);

        this.reportTests = tests;
        return tests;
    }

    private addTests(destinationTests: Array<ReportTest>, node: RunnerReportNode) {
        if (node instanceof ReportSuite || node instanceof ReportFeature) {
            for (const child of node.children) {
                this.addTests(destinationTests, child)
            }
        } else if (node instanceof ReportTest) {
            destinationTests.push(node);
        }
    }

    getAllReportSteps(): Array<ReportStep> {
        if (this.reportSteps) {
            return this.reportSteps;
        }

        let result: Array<ReportStep> = [];
        let tests = this.getTests();
        for (const test of tests) {
            this.extractAllReportStepsFromArray(test.children, result);
        }

        this.reportSteps = result;
        return result;
    }

    private extractAllReportStepsFromArray(testReportSteps: Array<ReportStep>, result: Array<ReportStep>) {
        for (const testReportStep of testReportSteps) {
            result.push(testReportStep);
            if (testReportStep.children && testReportStep.children.length > 0) {
                this.extractAllReportStepsFromArray(testReportStep.children, result);
            }
        }
    }

    getReportStepsMapByTag(): Map<string, Array<ReportStep>> {
        let result = new Map<string, Array<ReportStep>>();
        let allReportSteps = this.getAllReportSteps();

        for (const reportStep of allReportSteps) {
            this.addStepByTagToReportStepMap(reportStep, result);
        }

        return result;
    }

    private addStepByTagToReportStepMap(reportStep: ReportStep, result: Map<string, Array<ReportStep>>) {
        let tags = reportStep.stepCall.stepDef.tags;
        for (const tag of tags) {

            let values: Array<ReportStep> = result.get(tag);
            if (!values) {
                values = [];
                result.set(tag, values);
            }

            values.push(reportStep);
        }
    }
}