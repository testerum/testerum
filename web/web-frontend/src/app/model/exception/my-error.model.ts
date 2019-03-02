
import {ErrorCode} from "./enums/error-code.enum";

export class MyError {

    errorCode: ErrorCode;
    isErrorReported: boolean = false;
}
