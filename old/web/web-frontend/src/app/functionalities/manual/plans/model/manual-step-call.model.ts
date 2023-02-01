import {Serializable} from "../../../../model/infrastructure/serializable.model";
import {StepCall} from "../../../../model/step/step-call.model";
import {ManualTestStepStatus} from "./enums/manual-test-step-status.enum";
import {JsonUtil} from "../../../../utils/json.util";

export class ManualStepCall implements Serializable<ManualStepCall> {

    stepCall: StepCall;
    status: ManualTestStepStatus;

    deserialize(input: Object): ManualStepCall {
        this.stepCall = new StepCall().deserialize(input["stepCall"]);
        this.status = ManualTestStepStatus.fromString(input["status"]);

        return this;

    }

    serialize(): string {
        return "" +
            '{' +
            '"stepCall":' + JsonUtil.serializeSerializable(this.stepCall) +
            ',"status":' + JsonUtil.serializeSerializable(this.status) +
            '}'
    }

    clone(): ManualStepCall {
        return new ManualStepCall().deserialize(JSON.parse(this.serialize()));
    }

}
