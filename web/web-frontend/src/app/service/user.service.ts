import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Feedback} from "../functionalities/user/feedback/model/feedback.model";
import {map} from "rxjs/operators";
import {UserProfile} from "../model/license/profile/user-profile.model";
import {UserProfileMarshaller} from "../model/license/profile/user-profile-marshaller";

@Injectable()
export class UserService {

    private USER_PROFILE_URL = "/rest/user";

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

    getCurrentUserProfile(): Observable<UserProfile > {
        // todo: delete this method (the controller no longer exists)
        throw new Error("delete this method");

        // return this.http
        //     .get<UserProfile >(this.USER_PROFILE_URL)
        //     .pipe(map(it => UserProfileMarshaller.deserialize(it)));
    }

}
