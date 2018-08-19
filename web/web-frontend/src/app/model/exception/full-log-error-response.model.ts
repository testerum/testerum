
import {ErrorResponse} from "./error-response.model";
import {ErrorCode} from "./enums/error-code.enum";
import {Serializable} from "../infrastructure/serializable.model";

export class FullLogErrorResponse extends ErrorResponse implements Serializable<FullLogErrorResponse> {

    uiMessage: string;
    exceptionAsString: string;

    deserialize(input: Object): FullLogErrorResponse {
        this.errorCode = ErrorCode.fromString(input['errorCode']);
        this.uiMessage = input['uiMessage'];
        this.exceptionAsString = input['exceptionAsString'];

        return this;
    }

    serialize(): string {
        return null;
    }
}
