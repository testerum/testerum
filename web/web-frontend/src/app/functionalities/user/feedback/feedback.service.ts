import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';
import {Setting} from "../../config/settings/model/setting.model";
import {JsonUtil} from "../../../utils/json.util";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {ErrorFeedback} from "../../../generic/error/report-modal/model/error.feedback";
import {Feedback} from "./model/feedback.model";

@Injectable()
export class FeedbackService {

    private BASE_URL = "/rest/feedback";

    constructor(private http: HttpClient) {}

    sendFeedback(errorReport: Feedback): Observable<Feedback> {
        const body = errorReport.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<any>(this.BASE_URL, body, httpOptions)
            .pipe(map(it => new Feedback().deserialize(it)));
    }
}
