import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {ErrorFeedback} from "./model/error.feedback";

@Injectable()
export class ErrorFeedbackService {

    private BASE_URL = "/rest/feedback";

    constructor(private http: HttpClient) {}

    sendErrorFeedback(errorReport: ErrorFeedback): Observable<ErrorFeedback> {
        const body = errorReport.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<any>(this.BASE_URL+"/error", body, httpOptions)
            .pipe(map(it => new ErrorFeedback().deserialize(it)));
    }
}
