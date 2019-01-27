import {EMPTY, Observable, throwError as observableThrowError} from 'rxjs';

import {tap} from 'rxjs/operators';
import {EventEmitter, Injectable} from "@angular/core";
import {ErrorCode} from "../../model/exception/enums/error-code.enum";
import {FullLogErrorResponse} from "../../model/exception/full-log-error-response.model";
import {MyError} from "../../model/exception/my-error.model";
import {ValidationErrorResponse} from "../../model/exception/validation-error-response.model";
import {
    HttpClient,
    HttpErrorResponse,
    HttpEvent,
    HttpHandler,
    HttpInterceptor,
    HttpRequest
} from "@angular/common/http";
import {ServerNotAvailableModalService} from "../../generic/error/server-not-available/server-not-available-modal.service";
import {UtilService} from "../util.service";

@Injectable()
export class ErrorHttpInterceptor implements HttpInterceptor {

    errorEventEmitter: EventEmitter<MyError> = new EventEmitter<MyError>();
    static isServerAvailable: boolean = true;

    constructor(private http: HttpClient,
                private utilService: UtilService,
                private serverNotAvailableModalService: ServerNotAvailableModalService) {
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(tap(
            (event: HttpEvent<any>) => {},
            (err: any) => {

                if (err instanceof HttpErrorResponse) {
                    let httpErrorResponse: HttpErrorResponse = err;
                    if (httpErrorResponse.status >= 400 || httpErrorResponse.status == 0) {

                        if (err.status == 504 || err.status == 0) {
                            if (!ErrorHttpInterceptor.isServerAvailable) {
                                return EMPTY;
                            }
                            this.utilService.checkIfServerIsAvailable().subscribe((isServerAvailable: boolean) => {
                                if (!isServerAvailable) {
                                    if (ErrorHttpInterceptor.isServerAvailable) {
                                        ErrorHttpInterceptor.isServerAvailable = false;

                                        let shouldRefreshWhenServerIsBack: boolean = true;
                                        let contentTypeHeader = request.headers.get('content-type');
                                        if(contentTypeHeader) {
                                            let contentTypeToLowerCase = contentTypeHeader.toLowerCase();
                                            if (contentTypeToLowerCase.startsWith("application/json", 0)) {
                                                shouldRefreshWhenServerIsBack = false
                                            }
                                        }

                                        this.serverNotAvailableModalService.show(shouldRefreshWhenServerIsBack);
                                    }
                                }
                            });
                            return EMPTY;
                        }
                    }
                }

                return EMPTY;
            }
        ));
    }


    handleHttpResponseException(httpErrorResponse: HttpErrorResponse | any) {
        if (httpErrorResponse.status >= 400) {
            let contentTypeHeader = httpErrorResponse.headers.get('content-type');
            if(contentTypeHeader) {
                let contentTypeToLowerCase = contentTypeHeader.toLowerCase();
                if (contentTypeToLowerCase.startsWith("application/json", 0)) {

                    let errorResponse: MyError = httpErrorResponse.error;

                    if (errorResponse.errorCode.toString() == ErrorCode.VALIDATION.enumAsString) {
                        let validationException = new ValidationErrorResponse().deserialize(errorResponse);
                        this.errorEventEmitter.emit(validationException);
                        return observableThrowError(httpErrorResponse);
                    }

                    if (errorResponse.errorCode.toString() == ErrorCode.GENERIC_ERROR.enumAsString) {
                        let fullLogErrorResponse = new FullLogErrorResponse().deserialize(errorResponse);
                        this.errorEventEmitter.emit(fullLogErrorResponse);
                        console.error(fullLogErrorResponse);
                        return observableThrowError(httpErrorResponse);
                    }

                    this.errorEventEmitter.emit(errorResponse);
                    console.error(errorResponse);
                }
            }
        }

        return observableThrowError(httpErrorResponse)
    }

    showGenericValidationError(validationErrorResponse: ValidationErrorResponse) {
        this.errorEventEmitter.emit(
            validationErrorResponse
        )
    }
}
