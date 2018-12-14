import {ReportSuite} from "../../../common/testerum-model/model/report/report-suite";
import {FsUtils} from "../../../common/util/FsUtils";
import {ReportTest} from "../../../common/testerum-model/model/report/report-test";
import {RunnerReportNode} from "../../../common/testerum-model/model/report/runner-report-node";
import {ReportFeature} from "../../../common/testerum-model/model/report/report-feature";
import * as path from "path";
import {ComposedStepDef} from "../../../common/testerum-model/model/step/def/composed-step-def";
import {BasicStepDef} from "../../../common/testerum-model/model/step/def/basic-step-def";
import {UndefinedStepDef} from "../../../common/testerum-model/model/step/def/undefined-step-def";
import {Templates} from "./templates/Templates";
import {ExecutionStatus} from "../../../common/testerum-model/model/report/execution-status";

class Application {

    private readonly destinationDirectory: string;

    constructor(private readonly dataFilePath: string,
                properties: {[key: string]: string}) {
        this.destinationDirectory = properties["destinationDirectory"];
    }

    run() {
        const reportSuite = this.loadModel();
        const tests = this.getTests(reportSuite);

        // render index
        FsUtils.writeFile(
            path.resolve(this.destinationDirectory, "index.html"),
            Templates.INDEX({tests: tests})
        );

        // render tests
        for (const test of tests) {
            FsUtils.writeFile(
                // todo: save put tests under feature directories
                path.resolve(properties.destinationDirectory, `${test.testFilePath.fileName}.html`),
                Templates.TEST({
                    test: test,
                    BasicStepDef: BasicStepDef,
                    ComposedStepDef: ComposedStepDef,
                    UndefinedStepDef: UndefinedStepDef,
                    ExecutionStatus: ExecutionStatus
                })
            );
        }
    }

    private loadModel(): ReportSuite {
        const dataFileContent = FsUtils.readFile(dataFilePath);
        const dataFileJson = JSON.parse(dataFileContent);

        return ReportSuite.parse(dataFileJson);
    }

    private getTests(reportSuite: ReportSuite): Array<ReportTest> {
        const tests: Array<ReportTest> = [];

        this.addTests(tests, reportSuite);

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

}

const dataFilePath = process.argv[2];
const properties=JSON.parse(process.argv[3]);

new Application(dataFilePath, properties).run();
