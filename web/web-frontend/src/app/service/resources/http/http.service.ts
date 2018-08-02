import {map} from 'rxjs/operators';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';


import {HttpResponse} from "../../../model/resource/http/http-response.model";
import {HttpRequest} from "../../../model/resource/http/http-request.model";
import {HttpClient, HttpHeaders} from "@angular/common/http";

@Injectable()
export class HttpService {

    private HTTP_URL = "/rest/http";

    constructor(private http: HttpClient) {}

    executeRequest(httpRequest:HttpRequest): Observable<HttpResponse> {
        let body = httpRequest.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<HttpResponse>(this.HTTP_URL + "/execute", body, httpOptions).pipe(
            map(res => new HttpResponse().deserialize(res)));
    }
}
