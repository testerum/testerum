import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Config} from "../model/infrastructure/config.model";
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import {ErrorService} from "./error.service";

class ConfigName {
    name:string;
    constructor(name: string) {
        this.name = name;
    }
}

@Injectable()
export class ConfigService {

    private CONFIG_URL = "/rest/config/";

    public static COMPARE_MODE_FOR_HTTP_RESPONSE_VERIFY_HEADERS = new ConfigName("COMPARE_MODE_FOR_HTTP_RESPONSE_VERIFY_HEADERS");

    private cache = {};
    constructor(private http: Http,
                private errorService: ErrorService) {
    }

    getConfig(configName: ConfigName): Observable<Array<Config>> {
        let configFromCache = this.cache[configName.name];
        if(configFromCache) {
            return configFromCache;
        }

        let configs = this.http
            .get(this.CONFIG_URL + configName.name)
            .map(ConfigService.extractConfig)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
        configs.subscribe(
            result => this.cache[configName.name] = result
        );
        return configs;
    }

    private static extractConfig(res: Response): Array<Config> {
        let json:Array<string> = res.json();

        let response: Array<Config> = [];

        for (let configsAsString of json) {
            response.push(
                new Config().deserialize(configsAsString)
            )
        }

        return response;
    }
}


