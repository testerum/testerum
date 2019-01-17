import {EMPTY, Observable, Subject, throwError as observableThrowError} from 'rxjs';

import {tap} from 'rxjs/operators';
import {EventEmitter, Injectable} from "@angular/core";
import {ErrorCode} from "../model/exception/enums/error-code.enum";
import {FullLogErrorResponse} from "../model/exception/full-log-error-response.model";
import {ErrorResponse} from "../model/exception/error-response.model";
import {ValidationErrorResponse} from "../model/exception/validation-error-response.model";
import {
    HttpClient,
    HttpErrorResponse,
    HttpEvent,
    HttpHandler,
    HttpInterceptor,
    HttpRequest
} from "@angular/common/http";
import {ServerNotAvailableModalService} from "../generic/error/server-not-available/server-not-available-modal.service";

@Injectable()
export class UtilService {

    private PING_REQUEST_PATH = "/rest/version";

    constructor(private http: HttpClient) {
    }

    checkIfServerIsAvailable(): Observable<boolean> {

        let responseSubject: Subject<boolean> = new Subject<boolean>();
        this.http
            .get<string>(this.PING_REQUEST_PATH)
            .subscribe(
                (data: string) => responseSubject.next(true), // success path
                error => {responseSubject.next(false)} // error path
            );

        return responseSubject;
    }
}
