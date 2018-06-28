import {EventEmitter, Injectable} from "@angular/core";
import {ErrorCode} from "../model/exception/enums/error-code.enum";
import {FullLogErrorResponse} from "../model/exception/full-log-error-response.model";
import {ErrorResponse} from "../model/exception/error-response.model";
import {Observable} from "rxjs/Rx";
import {ValidationErrorResponse} from "../model/exception/validation-error-response.model";
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";

@Injectable()
export class ErrorService implements HttpInterceptor {


    errorEventEmitter: EventEmitter<ErrorResponse> = new EventEmitter<ErrorResponse>();

    trick(errorResponse: Response | any): Observable<Response> {
        return this.handleHttpResponseException(errorResponse);
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).do(
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

                                    return Observable.empty();
                                }
                                if (errorResponse.errorCode.toString() == ErrorCode.GENERIC_ERROR.enumAsString) {
                                    let fullLogErrorResponse = new FullLogErrorResponse().deserialize(errorResponse);
                                    this.errorEventEmitter.emit(fullLogErrorResponse);
                                    console.error(fullLogErrorResponse);

                                    return Observable.empty();
                                }
                            }
                        }
                    }
                }
                this.errorEventEmitter.emit(err);
                console.error(err);

                return Observable.empty();
            }
        );
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
                        return Observable.throw(httpErrorResponse);
                    }

                    if (errorResponse.errorCode.toString() == ErrorCode.GENERIC_ERROR.enumAsString) {
                        let fullLogErrorResponse = new FullLogErrorResponse().deserialize(errorResponse);
                        this.errorEventEmitter.emit(fullLogErrorResponse);
                        console.error(fullLogErrorResponse);
                        return Observable.throw(httpErrorResponse);
                    }

                    this.errorEventEmitter.emit(errorResponse);
                    console.error(errorResponse);
                }
            }
        }

        return Observable.throw(httpErrorResponse)
    }

    showGenericValidationError(validationErrorResponse: ValidationErrorResponse) {
        this.errorEventEmitter.emit(
            validationErrorResponse
        )
    }
}
