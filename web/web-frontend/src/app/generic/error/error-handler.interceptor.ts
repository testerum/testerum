import {ErrorHandler, Injectable} from "@angular/core";
import {ErrorService} from "../../service/error.service";
import {JavaScriptError} from "../../model/exception/javascript-error.model";
import {HttpErrorResponse} from "@angular/common/http";


@Injectable()
export class ErrorsHandlerInterceptor implements ErrorHandler {

    constructor (private errorService: ErrorService) {
    }

    handleError(error: Error) {
        if (error.rejection && error.rejection instanceof HttpErrorResponse) {
            let httpError = error.rejection as HttpErrorResponse;
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
