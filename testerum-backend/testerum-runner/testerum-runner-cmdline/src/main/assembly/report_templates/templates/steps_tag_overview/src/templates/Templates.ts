import * as path from "path";
import {FsUtils} from "../../../../common/util/FsUtils";
import {TemplateIndexData} from "./TemplateIndexData";
import {TemplateTestData} from "./TemplateTestData";

// using eval to makes sure webpack doesn't try to bundle ejs (it can't, so node will resolve it at runtime)
const ejs = eval("require")("ejs");

export class Templates {
    private static readonly TEMPLATES_DIR = path.resolve(__dirname, "templates");

    private static compileTemplate(fileName: string) {
        const templatePath = path.resolve(Templates.TEMPLATES_DIR, fileName);
        const templateContent = FsUtils.readFile(templatePath);

        return ejs.compile(templateContent);
    }

    static readonly INDEX: (data: TemplateIndexData) => string = Templates.compileTemplate("index.ejs");
    static readonly TEST: (data: TemplateTestData) => string = Templates.compileTemplate("test.ejs");

    private constructor() {}

}
