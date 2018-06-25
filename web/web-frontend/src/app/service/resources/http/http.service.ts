import {Injectable} from '@angular/core';
import {Http, RequestOptions, Headers} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import {HttpResponse} from "../../../model/resource/http/http-response.model";
import {HttpRequest} from "../../../model/resource/http/http-request.model";
import {ErrorService} from "../../error.service";

@Injectable()
export class HttpService {

    private HTTP_URL = "/rest/http";

    constructor(private http: Http,
                private errorService: ErrorService) {
    }

    executeRequest(httpRequest:HttpRequest): Observable<HttpResponse> {
        let body = httpRequest.serialize();
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        return this.http
            .post(this.HTTP_URL + "/execute", body, options)
            .map(response => new HttpResponse().deserialize(response.json()))
            .catch(err => {return this.errorService.handleHttpResponseException(err)} );
    }

}
