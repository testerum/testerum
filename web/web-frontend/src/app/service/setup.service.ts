import {Injectable} from "@angular/core";
import { Observable } from 'rxjs/Observable';
import {Subject} from "rxjs/Rx";
import {Setup} from "../functionalities/config/setup/model/setup.model";
import {HttpClient, HttpHeaders, HttpResponse} from "@angular/common/http";

@Injectable()
export class SetupService {

    private START_CONFIG_URL = "/rest/setup";

    private configIsSet: boolean = false;

    constructor(private http: HttpClient) {}

    isConfigSet(): Observable<boolean> {
        if(this.configIsSet) {
            let responseSubject: Subject<boolean> = new Subject<boolean>();
            responseSubject.next(true);
            return responseSubject;
        }

        return this.http
            .get<boolean>(this.START_CONFIG_URL + "/is_completed")
            .map(this.extractBooleanResponse);
    }

    private extractBooleanResponse(res:  boolean):boolean {
        this.configIsSet = res;
        return res;
    }

    save(model: Setup): Observable<Setup> {
        let body = model.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .put<Setup>(this.START_CONFIG_URL, body, httpOptions)
            .map(SetupService.extractStartConfig);
    }

    private static extractStartConfig(res:  Setup):Setup {
        return new Setup().deserialize(res);
    }
}
