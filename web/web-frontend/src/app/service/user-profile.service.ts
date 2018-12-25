import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Feedback} from "../functionalities/user-profile/feedback/model/feedback.model";
import {map} from "rxjs/operators";
import {About} from "../functionalities/user-profile/about/model/about.model";

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

    getUserProfileDetails(): Observable<About[] > {
        return this.http
            .get<About[] >(this.USER_PROFILE_URL + "/about")
            .pipe(map(UserProfileService.extractUserProfile));
    }

    private static extractUserProfile(res:  About[]): About[] {
        const response: About[] = [];

        for ( let aboutAsJson of res) {
            let about = new About().deserialize(aboutAsJson);
            response.push(about)
        }

        return response;
    }
}
