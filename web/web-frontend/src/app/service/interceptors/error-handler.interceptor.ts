import {ErrorHandler, Injectable} from "@angular/core";
import {ErrorHttpInterceptor} from "./error.http-interceptor";
import {JavaScriptError} from "../../model/exception/javascript-error.model";
import {HttpErrorResponse} from "@angular/common/http";


@Injectable()
export class ErrorsHandlerInterceptor implements ErrorHandler {

    constructor (private errorService: ErrorHttpInterceptor) {
    }

    handleError(error: Error) {
        if (error instanceof HttpErrorResponse) {
            return
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
