import {ErrorHandler, Injectable} from "@angular/core";
import {ErrorHttpInterceptor} from "./error.http-interceptor";
import {JavaScriptError} from "../../model/exception/javascript-error.model";
import {HttpErrorResponse} from "@angular/common/http";
import {ErrorCode} from "../../model/exception/enums/error-code.enum";
import {ValidationErrorResponse} from "../../model/exception/validation-error-response.model";
import {FullLogErrorResponse} from "../../model/exception/full-log-error-response.model";
import {MyError} from "../../model/exception/my-error.model";


@Injectable()
export class ErrorsHandlerInterceptor implements ErrorHandler {

    constructor (private errorService: ErrorHttpInterceptor) {
    }

    handleError(error: Error) {
        if (error instanceof HttpErrorResponse) {

            let contentTypeHeader = error.headers.get('content-type');
            if(contentTypeHeader) {
                let contentTypeToLowerCase = contentTypeHeader.toLowerCase();
                if (contentTypeToLowerCase.startsWith("application/json", 0)) {

                    let errorResponse: MyError = error.error;

                    if (errorResponse.errorCode.toString() == ErrorCode.CLOUD_ERROR.enumAsString) {
                        return;
                    }

                    if (errorResponse.errorCode.toString() == ErrorCode.VALIDATION.enumAsString) {
                        let validationException = new ValidationErrorResponse().deserialize(errorResponse);
                        this.errorService.errorEventEmitter.emit(validationException);

                       return;
                    }

                    if (errorResponse.errorCode.toString() == ErrorCode.GENERIC_ERROR.enumAsString) {
                        let fullLogErrorResponse = new FullLogErrorResponse().deserialize(errorResponse);
                        this.errorService.errorEventEmitter.emit(fullLogErrorResponse);
                        console.error(fullLogErrorResponse);

                        return;
                    }
                }
            }
        }

        if (error['rejection'] && error['rejection'] instanceof HttpErrorResponse) { //To handle exception when server is down
            let httpError = error['rejection'] as HttpErrorResponse;
            if (httpError.status == 504 || httpError.status == 0) {
                console.error('Exception: ', error);
                return;
            }
        }
        let javaScriptError = new JavaScriptError(error);
        this.errorService.errorEventEmitter.emit(javaScriptError);
        console.error('Exception: ', error);
    }
}
