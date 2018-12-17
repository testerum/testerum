import {ReportSuite} from "../../../common/testerum-model/model/report/report-suite";
import {FsUtils} from "../../../common/util/FsUtils";
import {ReportModelExtractor} from "../../../common/testerum-model/model-extractor/report-model-extractor";
import {IndexRenderer} from "./renderers/index.renderer";

class Application {

    private readonly destinationDirectory: string;

    constructor(private readonly dataFilePath: string,
                properties: {[key: string]: string}) {
        this.destinationDirectory = properties["destinationDirectory"];

        if (!this.destinationDirectory) {
            throw Error("the [destinationDirectory] property is required");
        }
    }

    run() {
        const reportSuite = this.loadModel();
        const reportModelExtractor = new ReportModelExtractor(reportSuite);

        new IndexRenderer().render(this.destinationDirectory, reportModelExtractor);

    }

    private loadModel(): ReportSuite {
        const dataFileContent = FsUtils.readFile(this.dataFilePath);
        const dataFileJson = JSON.parse(dataFileContent);

        return ReportSuite.parse(dataFileJson);
    }
}

const dataFilePath = process.argv[2];
const properties=JSON.parse(process.argv[3]);

new Application(dataFilePath, properties).run();
