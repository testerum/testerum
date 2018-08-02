import {EMPTY, Observable, throwError as observableThrowError} from 'rxjs';

import {tap} from 'rxjs/operators';
import {EventEmitter, Injectable} from "@angular/core";
import {ErrorCode} from "../model/exception/enums/error-code.enum";
import {FullLogErrorResponse} from "../model/exception/full-log-error-response.model";
import {ErrorResponse} from "../model/exception/error-response.model";
import {ValidationErrorResponse} from "../model/exception/validation-error-response.model";
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";

@Injectable()
export class ErrorService implements HttpInterceptor {


    errorEventEmitter: EventEmitter<ErrorResponse> = new EventEmitter<ErrorResponse>();

    trick(errorResponse: Response | any): Observable<Response> {
        return this.handleHttpResponseException(errorResponse);
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(tap(
            (event: HttpEvent<any>) => {},
            (err: any) => {
                if (err instanceof HttpErrorResponse) {
                    let httpErrorResponse: HttpErrorResponse = err;
                    if (httpErrorResponse.status >= 400) {
                        let contentTypeHeader = httpErrorResponse.headers.get('content-type');
                        if(contentTypeHeader) {
                            let contentTypeToLowerCase = contentTypeHeader.toLowerCase();
                            if (contentTypeToLowerCase.startsWith("application/json", 0)) {

                                let errorResponse: ErrorResponse = httpErrorResponse.error;

                                if (errorResponse.errorCode.toString() == ErrorCode.VALIDATION.enumAsString) {
                                    let validationException = new ValidationErrorResponse().deserialize(errorResponse);
                                    console.warn(validationException);

                                    return EMPTY;
                                }
                                if (errorResponse.errorCode.toString() == ErrorCode.GENERIC_ERROR.enumAsString) {
                                    let fullLogErrorResponse = new FullLogErrorResponse().deserialize(errorResponse);
                                    this.errorEventEmitter.emit(fullLogErrorResponse);
                                    console.error(fullLogErrorResponse);

                                    return EMPTY;
                                }
                            }
                        }
                    }
                }
                this.errorEventEmitter.emit(err);
                console.error(err);

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

                    let errorResponse: ErrorResponse = httpErrorResponse.error;

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
