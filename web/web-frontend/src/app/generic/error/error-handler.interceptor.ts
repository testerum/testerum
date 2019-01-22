import {ErrorHandler, Injectable} from "@angular/core";
import {ErrorService} from "../../service/error.service";
import {JavaScriptError} from "../../model/exception/javascript-error.model";


@Injectable()
export class ErrorsHandlerInterceptor implements ErrorHandler {

    constructor (private errorService: ErrorService) {
    }

    handleError(error: Error) {
        let javaScriptError = new JavaScriptError(error);
        this.errorService.errorEventEmitter.emit(javaScriptError);
        console.error('Exception: ', error);
    }
}
