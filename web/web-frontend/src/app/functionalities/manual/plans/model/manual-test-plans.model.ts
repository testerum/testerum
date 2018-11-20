import {Serializable} from "../../../../model/infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";
import {ManualTestPlan} from "./manual-test-plan.model";

export class ManualTestPlans implements Serializable<ManualTestPlans>{

    activeTestPlans: Array<ManualTestPlan> = [];
    finalizedTestPlans: Array<ManualTestPlan> = [];

    deserialize(input: Object): ManualTestPlans {
        for (let activeExecPlan of (input['activeTestPlans']) || []) {
            this.activeTestPlans.push(new ManualTestPlan().deserialize(activeExecPlan));
        }

        for (let finalizedExecPlan of (input['finalizedTestPlans']) || []) {
            this.finalizedTestPlans.push(new ManualTestPlan().deserialize(finalizedExecPlan));
        }

        return this;
    }

    serialize(): string {
        let response = "{";

        if (this.activeTestPlans) {
            response += '"activeTestPlans":' + JsonUtil.serializeArrayOfSerializable(this.activeTestPlans);
        }
        if (this.finalizedTestPlans) {
            response += ',"finalizedTestPlans":' + JsonUtil.serializeArrayOfSerializable(this.finalizedTestPlans);
        }
        response += '}';

        return response;
    }
}
