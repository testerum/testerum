import {Injectable} from "@angular/core";
import { Observable } from 'rxjs/Observable';
import {Subject} from "rxjs/Rx";
import {Setup} from "../functionalities/config/setup/model/setup.model";
import {HttpClient, HttpHeaders} from "@angular/common/http";

@Injectable()
export class TagsService {

    private TAGS_URL = "/rest/tags";

    constructor(private http: HttpClient) {}

    getTags(): Observable<Array<string>> {
        return this.http
            .get<Array<string>>(this.TAGS_URL);
    }
}
