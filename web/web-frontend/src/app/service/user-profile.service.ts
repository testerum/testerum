import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Feedback} from "../functionalities/user-profile/feedback/model/feedback.model";
import {map} from "rxjs/operators";

@Injectable()
export class UserProfileService {

    private USER_PROFILE_URL = "/rest/userProfile";

    constructor(private http: HttpClient) {
    }

    saveFeedback(feedback: Feedback): Observable<Feedback> {
        let body = feedback.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json',
            })
        };

        return this.http
            .post<Feedback>(this.USER_PROFILE_URL + "/feedback", body, httpOptions)
            .pipe(map(res => new Feedback().deserialize(res)));
    }
}
