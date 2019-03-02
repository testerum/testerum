import {Serializable} from "../../../../model/infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";

export class ErrorFeedback implements Serializable<ErrorFeedback> {

    contactName: string;
    contactEmail: string;
    stepsToReproduce: string;

    errorMessage: string;
    errorStacktrace: string;

    deserialize(input: Object): ErrorFeedback {
        this.contactName = input['contactName'];
        this.contactEmail = input['contactEmail'];
        this.stepsToReproduce = input['stepsToReproduce'];
        this.errorMessage = input['errorMessage'];
        this.errorStacktrace = input['errorStacktrace'];
        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"contactName":' + JsonUtil.stringify(this.contactName) +
            ',"contactEmail":' + JsonUtil.stringify(this.contactEmail) +
            ',"stepsToReproduce":' + JsonUtil.stringify(this.stepsToReproduce) +
            ',"errorMessage":' + JsonUtil.stringify(this.errorMessage) +
            ',"errorStacktrace":' + JsonUtil.stringify(this.errorStacktrace) +
            '}'
    }
}
