import * as path from "path";
import * as fse from "fs-extra";
import {FsUtils} from "../../../../common/util/FsUtils";

export class Application {

    private readonly destinationDirectory: string;

    constructor(private readonly dataFilePath: string,
                properties: {[key: string]: string}) {
        this.destinationDirectory = properties["destinationDirectory"];

        if (!this.destinationDirectory) {
            throw Error("the [destinationDirectory] property is required");
        }
    }

    run() {
        // copy template
        const srcDir = path.resolve(__dirname, "report-template");

        fse.copySync(srcDir, this.destinationDirectory);

        const indexFilePath = path.resolve(this.destinationDirectory, "index.html");
        const originalContent = FsUtils.readFile(indexFilePath);
        // replace dev model with actual model

        const dataFileContent = FsUtils.readFile(this.dataFilePath);
        const replacedContent = originalContent.replace(
            /<!--### START: testerumRunnerReportModel ### -->[\s\S]*<!--### END: testerumRunnerReportModel ### -->/,
            `<script type='text/javascript'>\n    window.testerumRunnerReportModel = ${dataFileContent};\n  </script>`
        );

        FsUtils.writeFile(indexFilePath, replacedContent);
    }

}
