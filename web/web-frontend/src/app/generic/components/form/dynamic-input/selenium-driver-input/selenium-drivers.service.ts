import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {map} from "rxjs/operators";
import {SeleniumBrowserType} from "./model/selenium-browser-type.enum";
import {SeleniumDriverInfo} from "./model/selenium-driver-info.model";

@Injectable()
export class SeleniumDriversService {

    private BASE_URL = "/rest/selenium-drivers";

    constructor(private http: HttpClient) {}

    getDriversInfo(): Observable<Map<SeleniumBrowserType, SeleniumDriverInfo[]>> {
        return this.http
            .get<Object>(this.BASE_URL)
            .pipe(map(SeleniumDriversService.extractDriversInfo));
    }

    private static extractDriversInfo(res: Object): Map<SeleniumBrowserType, SeleniumDriverInfo[]> {
        const response: Map<SeleniumBrowserType, SeleniumDriverInfo[]> = new Map<SeleniumBrowserType, SeleniumDriverInfo[]>();

        Object.keys(res).forEach( key => {
            let value = res[key];

            let seleniumDriverInfos: Array<SeleniumDriverInfo> = [];
            for (const valueElement of value || []) {
                seleniumDriverInfos.push(
                    new SeleniumDriverInfo().deserialize(valueElement)
                )
            }

            response.set(SeleniumBrowserType.fromSerialization(key), seleniumDriverInfos);
        });

        return response;
    }
}
