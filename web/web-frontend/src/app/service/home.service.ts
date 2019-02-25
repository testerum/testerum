import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {map} from "rxjs/operators";
import {Home} from "../model/home/home.model";


@Injectable()
export class HomeService {

    private HOME_URL = "/rest/home";

    constructor(private http: HttpClient) {}

    getHomePageModel(): Observable<Home> {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .get<Home>(this.HOME_URL, httpOptions)
            .pipe(map(it => new Home().deserialize(it)));
    }
}
