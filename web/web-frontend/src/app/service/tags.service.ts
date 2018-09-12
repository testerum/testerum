import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';
import {HttpClient} from "@angular/common/http";

@Injectable()
export class TagsService {

    private TAGS_URL = "/rest/tags";

    constructor(private http: HttpClient) {}

    getTags(): Observable<Array<string>> {
        return this.http
            .get<Array<string>>(this.TAGS_URL);
    }

    getManualTestTags(): Observable<Array<string>> {
        return this.http
            .get<Array<string>>(this.TAGS_URL + "/manual");
    }
}
