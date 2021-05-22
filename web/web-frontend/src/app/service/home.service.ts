import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {map} from "rxjs/operators";
import {Home} from "../model/home/home.model";
import {Location} from "@angular/common";

@Injectable()
export class HomeService {

    private readonly baseUrl: string;

    constructor(private http: HttpClient,
                location: Location) {
        this.baseUrl = location.prepareExternalUrl("/rest/home");

    }

    getHomePageModel(): Observable<Home> {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .get<Home>(this.baseUrl, httpOptions)
            .pipe(map(it => new Home().deserialize(it)));
    }
}
