import {MyError} from "./my-error.model";
import {ErrorCode} from "./enums/error-code.enum";

export class JavaScriptError extends MyError {

    readonly errorCode: ErrorCode = ErrorCode.GENERIC_ERROR;
    error: Error;

    constructor(error: Error) {
        super();
        this.error = error;
    }
}
