import {Injectable} from "@angular/core";
import {Http, RequestOptions, Response, Headers} from "@angular/http";
import { Observable } from 'rxjs/Observable';
import {ErrorService} from "./error.service";
import {Router} from "@angular/router";
import {Subject} from "rxjs/Rx";
import {Setting} from "../functionalities/config/settings/model/setting.model";
import {JsonUtil} from "../utils/json.util";


@Injectable()
export class SettingsService {

    private BASE_URL = "/rest/settings";

    constructor(private router: Router,
                private http:Http,
                private errorService: ErrorService) {
    }

    getSettings(): Observable<Array<Setting>> {

        return this.http
            .get(this.BASE_URL)
            .map(this.extractSettings)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    private extractSettings(res: Response): Array<Setting> {
        let json = res.json();

        let response: Array<Setting> = [];
        for (let settingAsJson of json) {
            let setting = new Setting().deserialize(settingAsJson);
            response.push(setting)
        }

        return response;
    }

    save(settings: Array<Setting>): Observable<Array<Setting>> {
        let body = JsonUtil.serializeArrayOfSerializable(settings);
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        let responseSubject: Subject<Array<Setting>> = new Subject<Array<Setting>>();
        this.http
            .post(this.BASE_URL, body, options)
            .subscribe(
                (response: Response) => {
                    responseSubject.next(
                        this.extractSettings(response)
                    )
                },
                (err) => {
                    this.errorService.handleHttpResponseException(err)
                }
            );

        return responseSubject.asObservable();
    }
}
