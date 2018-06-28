import {Injectable} from "@angular/core";
import { Observable } from 'rxjs/Observable';
import {Setting} from "../functionalities/config/settings/model/setting.model";
import {JsonUtil} from "../utils/json.util";
import {HttpClient, HttpHeaders, HttpResponse} from "@angular/common/http";

@Injectable()
export class SettingsService {

    private BASE_URL = "/rest/settings";

    constructor(private http: HttpClient) {}

    getSettings(): Observable<Array<Setting>> {
        return this.http
            .get<Array<Setting>>(this.BASE_URL)
            .map(this.extractSettings);
    }

    private extractSettings(res:  Array<Setting>): Array<Setting> {
        let response: Array<Setting> = [];
        for (let settingAsJson of res) {
            let setting = new Setting().deserialize(settingAsJson);
            response.push(setting)
        }

        return response;
    }

    save(settings: Array<Setting>): Observable<Array<Setting>> {
        let body = JsonUtil.serializeArrayOfSerializable(settings);
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<Array<Setting>>(this.BASE_URL, body, httpOptions)
            .map(this.extractSettings);
    }
}
