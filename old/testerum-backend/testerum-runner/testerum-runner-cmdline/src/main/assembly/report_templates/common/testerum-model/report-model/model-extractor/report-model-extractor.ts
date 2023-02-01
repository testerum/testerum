import {ReportSuite} from "../model/report/report-suite";
import {ReportTest} from "../model/report/report-test";
import {RunnerReportNode} from "../model/report/runner-report-node";
import {ReportFeature} from "../model/report/report-feature";
import {ReportStep} from "../model/report/report-step";
import {ReportParametrizedTest} from "../model/report/report-parametrized-test";

export class ReportModelExtractor {

    readonly reportSuite: ReportSuite;

    private reportTests: Array<ReportTest>;
    private reportParametrizedTests: Array<ReportParametrizedTest>;

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

    public getParametrizedTests(): Array<ReportParametrizedTest> {
        if (this.reportParametrizedTests) {
            return this.reportParametrizedTests;
        }

        const parametrizedTests: Array<ReportParametrizedTest> = [];

        this.addParametrizedTests(parametrizedTests, this.reportSuite);

        this.reportParametrizedTests = parametrizedTests;
        return parametrizedTests;
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

    private addParametrizedTests(destinationParametrizedTests: Array<ReportParametrizedTest>, node: RunnerReportNode) {
        if (node instanceof ReportSuite || node instanceof ReportFeature) {
            for (const child of node.children) {
                this.addParametrizedTests(destinationParametrizedTests, child)
            }
        } else if (node instanceof ReportParametrizedTest) {
            destinationParametrizedTests.push(node);
        }
    }
}
