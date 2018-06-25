import {Injectable} from "@angular/core";
import {Http, RequestOptions, Response, Headers} from "@angular/http";
import { Observable } from 'rxjs/Observable';
import {ErrorService} from "./error.service";
import {Router} from "@angular/router";
import {Subject} from "rxjs/Rx";
import {Setup} from "../functionalities/config/setup/model/setup.model";

@Injectable()
export class SetupService {

    private START_CONFIG_URL = "/rest/setup";

    private configIsSet: boolean = false;

    constructor(private router: Router,
                private http:Http,
                private errorService: ErrorService) {
    }

    isConfigSet(): Observable<boolean> {
        if(this.configIsSet) {
            let responseSubject: Subject<boolean> = new Subject<boolean>();
            responseSubject.next(true);
            return responseSubject;
        }

        return this.http
            .get(this.START_CONFIG_URL + "/is_completed")
            .map(this.extractBooleanResponse)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    private extractBooleanResponse(res: Response):boolean {
        let response = res.json();
        this.configIsSet = response;

        return response;
    }

    save(model: Setup): Observable<Setup> {
        let body = model.serialize();
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        let responseSubject: Subject<Setup> = new Subject<Setup>();
        this.http
            .post(this.START_CONFIG_URL, body, options)
            .subscribe(
                (response: Response) => {
                    responseSubject.next(
                        SetupService.extractStartConfig(response)
                    )
                },
                (err) => {
                    this.errorService.handleHttpResponseException(err)
                }
            );

        return responseSubject.asObservable();
    }

    private static extractStartConfig(res: Response):Setup {
        return new Setup().deserialize(res.json());
    }
}
