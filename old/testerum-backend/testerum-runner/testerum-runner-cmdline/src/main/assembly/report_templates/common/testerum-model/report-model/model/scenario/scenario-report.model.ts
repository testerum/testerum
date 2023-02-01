import {MarshallingUtils} from "../../../json-marshalling/marshalling-utils";
import {ScenarioParamReportModel} from "./scenario-param-report.model";

export class ScenarioReportModel {

    constructor(
        public readonly name: string,
        public readonly params: Array<ScenarioParamReportModel> = [],
        public readonly enabled: boolean = true
    ) {}

    static parse(input: Object): ScenarioReportModel {
        if (!input) {
            return null;
        }

        const name = input["name"];
        const params = MarshallingUtils.parseList(input["params"], ScenarioParamReportModel);
        const enabled = input["enabled"];

        return new ScenarioReportModel(name, params, enabled);
    }
}