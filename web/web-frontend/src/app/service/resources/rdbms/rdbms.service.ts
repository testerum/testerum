import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import {RdbmsConnectionConfig} from "../../../model/resource/rdbms/rdbms-connection-config.model";
import {RdbmsDriver} from "../../../functionalities/resources/editors/database/connection/model/rdbms-driver.model";
import {RdbmsSchemas} from "../../../functionalities/resources/editors/database/connection/model/rdbms-schemas.model";
import {RdbmsSchema} from "../../../model/resource/rdbms/schema/rdbms-schema.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";

@Injectable()
export class RdbmsService {

    private DB_CONNECTION_URL = "/rest/rdbms";
    private DB_DRIVES_URL = "/rest/rdbms/drivers";
    private DB_SCHEMA_URL = "/rest/rdbms/schema";

    constructor(private http: HttpClient) {}

    showSchemasChooser(dbConnection:RdbmsConnectionConfig): Observable<RdbmsSchemas> {
        let body = dbConnection.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<RdbmsSchemas>(this.DB_CONNECTION_URL + "/schemas", body, httpOptions)
            .map(response => new RdbmsSchemas().deserialize(response));
    }

    ping(host:string, port:number): Observable<boolean> {

        const httpOptions = {
            params: new HttpParams()
                .append('host', host)
                .append('port', ""+port)
        };

        return this.http
            .get<boolean>(this.DB_CONNECTION_URL + "/ping", httpOptions)
    }

    getDrivers(): Observable<Array<RdbmsDriver>> {
        return this.http
            .get<Array<RdbmsDriver>>(this.DB_DRIVES_URL)
            .map(RdbmsService.extractDrivers);
    }

    private static extractDrivers(res: Array<RdbmsDriver>): Array<RdbmsDriver> {
        let response: Array<RdbmsDriver> = [];
        for (let dbDriverAsJson of res) {
            let dbDriver = new RdbmsDriver().deserialize(dbDriverAsJson);
            response.push(dbDriver)
        }

        return response;
    }

    getSchema(path:Path):  Observable<RdbmsSchema> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', path.toString())
        };

        return this.http
            .get<RdbmsSchema>(this.DB_SCHEMA_URL, httpOptions)
            .map(res => new RdbmsSchema().deserialize(res));
    }
}
