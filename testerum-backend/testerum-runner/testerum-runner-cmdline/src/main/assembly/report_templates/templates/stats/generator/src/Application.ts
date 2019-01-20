import * as path from "path";
import * as fse from "fs-extra";
import {FsUtils} from "../../../../common/util/FsUtils";

export class Application {

    private readonly destinationDirectory: string;

    constructor(private readonly modelDirectory: string,
                properties: {[key: string]: string}) {
        this.destinationDirectory = properties["destinationDirectory"];

        if (!this.destinationDirectory) {
            throw Error("the [destinationDirectory] property is required");
        }
    }

    run() {
        this.copyTemplateFiles();
        this.replaceDevModelWithActualModel();
        this.copyLogs();
    }

    private copyTemplateFiles() {
        const templatesSrcDir = path.resolve(__dirname, "report-template");

        console.log(`copying templates from [${templatesSrcDir}] to ${this.destinationDirectory}`);

        fse.copySync(templatesSrcDir, this.destinationDirectory);
    }

    private replaceDevModelWithActualModel() {
        const indexFilePath = path.resolve(this.destinationDirectory, "index.html");
        const modelPath = path.resolve(this.modelDirectory, "stats.json");

        console.log(`replacing model in [${indexFilePath}] with the content of [${modelPath}]`);

        const originalContent = FsUtils.readFile(indexFilePath);
        const modelFileContent = FsUtils.readFile(modelPath);
        const replacedContent = originalContent.replace(
            /<!--### START: testerumRunnerStatisticsModel ### -->[\s\S]*<!--### END: testerumRunnerStatisticsModel ### -->/,
            `<script type='text/javascript'>\n    window.testerumRunnerStatisticsModel = ${modelFileContent};\n  </script>`
        );

        FsUtils.writeFile(indexFilePath, replacedContent);
    }

    private copyLogs() {
        const logsSrcDir = path.resolve(this.modelDirectory, "logs");
        const logsDestDir = path.resolve(this.destinationDirectory, "logs");

        console.log(`copying log files from [${logsSrcDir}] to [${logsDestDir}]`);

        FsUtils.createDirectories(logsDestDir);
        fse.copySync(logsSrcDir, logsDestDir);
    }

}
