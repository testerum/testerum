import {EventEmitter, Injectable} from "@angular/core";
import {ErrorCode} from "../model/exception/enums/error-code.enum";
import {FullLogErrorResponse} from "../model/exception/full-log-error-response.model";
import {ErrorResponse} from "../model/exception/error-response.model";
import {Observable} from "rxjs/Rx";
import {ValidationErrorResponse} from "../model/exception/validation-error-response.model";

@Injectable()
export class ErrorService {


    errorEventEmitter: EventEmitter<ErrorResponse> = new EventEmitter<ErrorResponse>();

    trick(errorResponse: Response | any): Observable<Response> {
        return this.handleHttpResponseException(errorResponse);
    }

    handleHttpResponseException(errorResponse: Response | any) {
        if (errorResponse.status >= 400) {
            let contentTypeHeader = errorResponse.headers.get('content-type');
            if(contentTypeHeader) {
                let contentTypeToLowerCase = contentTypeHeader.toLowerCase();
                if (contentTypeToLowerCase.startsWith("application/json", 0)) {

                    let json = JSON.parse(errorResponse.text());
                    let errorCode = json["errorCode"];

                    if (errorCode == ErrorCode.VALIDATION.enumAsString) {
                        let validationException = new ValidationErrorResponse().deserialize(json);
                        this.errorEventEmitter.emit(validationException);
                        return Observable.throw(errorResponse);
                    }

                    if (errorCode) {
                        let fullLogErrorResponse = new FullLogErrorResponse().deserialize(json);
                        this.errorEventEmitter.emit(fullLogErrorResponse);
                        console.error(fullLogErrorResponse);
                        return Observable.throw(errorResponse);
                    }

                    this.errorEventEmitter.emit(errorCode);
                    console.error(errorCode);
                }
            }
        }

        return Observable.throw(errorResponse)
    }

    showGenericValidationError(validationErrorResponse: ValidationErrorResponse) {
        this.errorEventEmitter.emit(
            validationErrorResponse
        )
    }
}
