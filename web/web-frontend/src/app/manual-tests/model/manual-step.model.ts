import {JsonUtil} from "../../utils/json.util";
import {StepPhaseEnum} from "../../model/enums/step-phase.enum";

export class ManualTestStepModel implements Serializable<ManualTestStepModel> {

    phase: StepPhaseEnum = StepPhaseEnum.GIVEN;
    description:string;

    deserialize(input: Object): ManualTestStepModel {
        this.phase = StepPhaseEnum["" + input["phase"]];
        this.description = input['description'];

        return this;
    }

    serialize() {
        let response = "" +
            '{' +
            '"phase":' + JsonUtil.stringify(StepPhaseEnum[this.phase].toUpperCase()) + ',' +
            '"description":' + JsonUtil.stringify(this.description);

        response += '}';
        return response;
    }
}
