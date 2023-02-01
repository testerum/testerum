import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';
import {HttpClient} from "@angular/common/http";
import {Location} from "@angular/common";

@Injectable()
export class TagsService {

    private readonly baseUrl: string;

    constructor(private http: HttpClient,
                location: Location) {
        this.baseUrl = location.prepareExternalUrl("/rest/tags");
    }

    getTags(): Observable<Array<string>> {
        return this.http
            .get<Array<string>>(this.baseUrl);
    }

    getManualTestTags(): Observable<Array<string>> {
        return this.http
            .get<Array<string>>(this.baseUrl + "/manual");
    }
}
