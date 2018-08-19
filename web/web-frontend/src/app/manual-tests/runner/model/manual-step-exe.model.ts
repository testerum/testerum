import {JsonUtil} from "../../../utils/json.util";
import {StepPhaseEnum} from "../../../model/enums/step-phase.enum";
import {ManualTestStepStatus} from "../../model/enums/manual-test-step-status.enum";
import {ManualTestStepModel} from "../../model/manual-step.model";
import {Serializable} from "../../../model/infrastructure/serializable.model";

export class ManualTestStepExeModel extends ManualTestStepModel implements Serializable<ManualTestStepExeModel> {

    phase: StepPhaseEnum = StepPhaseEnum.GIVEN;
    description:string;
    stepStatus: ManualTestStepStatus = ManualTestStepStatus.NOT_EXECUTED;

    static createInstanceFrom(manualTestStepModel: ManualTestStepModel) {
        let instance = new ManualTestStepExeModel();

        instance.phase = manualTestStepModel.phase;
        instance.description = manualTestStepModel.description;

        return instance;
    }

    deserialize(input: Object): ManualTestStepExeModel {
        this.phase = StepPhaseEnum["" + input["phase"]];
        this.description = input['description'];

        if (input["stepStatus"]) {
            this.stepStatus = ManualTestStepStatus.fromString(input["stepStatus"]);
        }

        return this;
    }

    serialize() {
        let response = "" +
            '{' +
            '"phase":' + JsonUtil.stringify(StepPhaseEnum[this.phase].toUpperCase()) + ',' +
            '"description":' + JsonUtil.stringify(this.description);

        if (this.stepStatus) {
            response += ',"stepStatus":' + JsonUtil.stringify(this.stepStatus.toString())
        }
        response += '}';
        return response;
    }
}
