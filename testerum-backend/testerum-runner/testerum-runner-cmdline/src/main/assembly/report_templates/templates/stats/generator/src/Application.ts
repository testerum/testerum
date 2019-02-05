import * as path from "path";
import * as fse from "fs-extra";
import {FsUtils} from "../../../../common/util/FsUtils";

export class Application {

    constructor(private readonly modelFile: string,
                private readonly destinationDirectory: string) {}

    run() {
        this.copyTemplateFiles();
        this.replaceDevModelWithActualModel();
    }

    private copyTemplateFiles() {
        const templatesSrcDir = path.resolve(__dirname, "report-template");

        console.log(`[stats/generator] copying templates from [${templatesSrcDir}] to ${this.destinationDirectory}`);

        fse.copySync(templatesSrcDir, this.destinationDirectory);
    }

    private replaceDevModelWithActualModel() {
        const indexFilePath = path.resolve(this.destinationDirectory, "index.html");
        console.log(`[stats/generator] replacing model in [${indexFilePath}] with the content of [${this.modelFile}]`);

        const originalContent = FsUtils.readFile(indexFilePath);
        const modelFileContent = FsUtils.readFile(this.modelFile);
        const replacedContent = originalContent.replace(
            /<!--### START: testerumRunnerStatisticsModel ### -->[\s\S]*<!--### END: testerumRunnerStatisticsModel ### -->/,
            `<script type='text/javascript'>\n    window.testerumRunnerStatisticsModel = ${modelFileContent};\n  </script>`
        );

        FsUtils.writeFile(indexFilePath, replacedContent);
    }

}
