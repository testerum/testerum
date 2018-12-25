import {ReportStepCallArg} from "./report-step-call-arg";
import {MarshallingUtils} from "../../../json-marshalling/marshalling-utils";

export class ReportStepCall {

    constructor(public readonly id: string,
                public readonly stepDefId: string,
                public readonly args: Array<ReportStepCallArg>) {}

    static parse(input: Object): ReportStepCall {
        const id = input["id"];
        const stepDefId = input["stepDefId"];
        const args = MarshallingUtils.parseList(input["args"], ReportStepCallArg);

        return new ReportStepCall(id, stepDefId, args);
    }

}
