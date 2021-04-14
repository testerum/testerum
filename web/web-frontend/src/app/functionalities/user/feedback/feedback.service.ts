import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Feedback} from "./model/feedback.model";
import {Location} from "@angular/common";

@Injectable()
export class FeedbackService {

    private readonly baseUrl: string;

    constructor(private http: HttpClient,
                location: Location) {
        this.baseUrl = location.prepareExternalUrl("/rest/feedback");
    }

    sendFeedback(errorReport: Feedback): Observable<Feedback> {
        const body = errorReport.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<any>(this.baseUrl, body, httpOptions)
            .pipe(map(it => new Feedback().deserialize(it)));
    }
}
