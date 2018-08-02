import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable, Subject} from 'rxjs';
import {Setup} from "../functionalities/config/setup/model/setup.model";
import {HttpClient, HttpHeaders} from "@angular/common/http";

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
            .get<boolean>(this.START_CONFIG_URL + "/is_completed").pipe(
            map(this.extractBooleanResponse));
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
            .put<Setup>(this.START_CONFIG_URL, body, httpOptions).pipe(
            map(SetupService.extractStartConfig));
    }

    private static extractStartConfig(res:  Setup):Setup {
        return new Setup().deserialize(res);
    }
}
