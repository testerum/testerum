import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';
import {Setting} from "../functionalities/config/settings/model/setting.model";
import {JsonUtil} from "../utils/json.util";
import {HttpClient, HttpHeaders} from "@angular/common/http";

@Injectable()
export class SettingsService {

    private BASE_URL = "/rest/settings";

    constructor(private http: HttpClient) {}

    getSettings(): Observable<Setting[]> {
        return this.http
            .get<Setting[]>(this.BASE_URL)
            .pipe(map(SettingsService.extractSettings));
    }

    private static extractSettings(res:  Setting[]): Setting[] {
        const response: Setting[] = [];

        for (let settingAsJson of res) {
            let setting = new Setting().deserialize(settingAsJson);
            response.push(setting)
        }

        return response;
    }

    save(settings: Setting[]): Observable<Setting[]> {
        const body = JsonUtil.stringify(
            SettingsService.settingsToKeyValueMap(settings)
        );
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<any>(this.BASE_URL, body, httpOptions).pipe(
            map(SettingsService.extractSettings));
    }

    private static settingsToKeyValueMap(settings: Setting[]): any {
        const result = {};

        for (let setting of settings) {
            result[setting.definition.key] = setting.unresolvedValue;
        }

        return result;
    }


}
