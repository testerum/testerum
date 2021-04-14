import {map} from 'rxjs/operators';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ValidHttpResponse} from "../../../model/resource/http/http-response/valid-http-response.model";
import {HttpRequest} from "../../../model/resource/http/http-request.model";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {HttpResponse} from "../../../model/resource/http/http-response/http-response.model";
import {HttpResponseDeserializationUtil} from "../../../model/resource/http/http-response/http-response-deserialization.util";
import {Location} from "@angular/common";

@Injectable()
export class HttpService {

    private readonly baseUrl: string;

    constructor(private http: HttpClient,
                location: Location) {
        this.baseUrl = location.prepareExternalUrl("/rest/http");
    }

    executeRequest(httpRequest:HttpRequest): Observable<HttpResponse> {
        let body = httpRequest.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<ValidHttpResponse>(this.baseUrl + "/execute", body, httpOptions).pipe(
            map(res => HttpResponseDeserializationUtil.deserialize(res)));
    }
}
