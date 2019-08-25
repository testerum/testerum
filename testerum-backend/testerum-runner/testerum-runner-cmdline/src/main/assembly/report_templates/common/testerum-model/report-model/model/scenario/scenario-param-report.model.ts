import {ScenarioParamTypeReportEnum} from "./scenario-param-type-report.enum";

export class ScenarioParamReportModel {

    constructor(
        public readonly name: string,
        public readonly type: ScenarioParamTypeReportEnum = ScenarioParamTypeReportEnum.TEXT,
        public readonly value: string
    ){}

    static parse(input: Object): ScenarioParamReportModel {
        if (!input) {
            return null;
        }

        const name = input["name"];
        const type = ScenarioParamTypeReportEnum.fromString(input["testFilePath"]);
        const value = input["value"];

        return new ScenarioParamReportModel(name, type, value);
    }
}