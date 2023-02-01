import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {ErrorFeedback} from "./model/error.feedback";
import {Location} from "@angular/common";

@Injectable()
export class ErrorFeedbackService {

    private readonly baseUrl: string;

    constructor(private http: HttpClient,
                location: Location) {
        this.baseUrl = location.prepareExternalUrl("/rest/feedback");
    }

    sendErrorFeedback(errorReport: ErrorFeedback): Observable<ErrorFeedback> {
        const body = errorReport.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<any>(this.baseUrl+"/error", body, httpOptions)
            .pipe(map(it => new ErrorFeedback().deserialize(it)));
    }
}
