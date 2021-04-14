import {map} from 'rxjs/operators';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';


import {RdbmsConnectionConfig} from "../../../model/resource/rdbms/rdbms-connection-config.model";
import {RdbmsDriver} from "../../../functionalities/resources/editors/database/connection/model/rdbms-driver.model";
import {RdbmsSchemas} from "../../../functionalities/resources/editors/database/connection/model/rdbms-schemas.model";
import {RdbmsSchema} from "../../../model/resource/rdbms/schema/rdbms-schema.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Location} from "@angular/common";

@Injectable()
export class RdbmsService {

    private readonly dbConnectionUrl: string;
    private readonly dbDrivesUrl: string;
    private readonly dbSchemaUrl: string;

    constructor(private http: HttpClient,
                location: Location) {
        this.dbConnectionUrl = location.prepareExternalUrl("/rest/rdbms");
        this.dbDrivesUrl = location.prepareExternalUrl("/rest/rdbms/drivers");
        this.dbSchemaUrl = location.prepareExternalUrl("/rest/rdbms/schema");
    }

    showSchemasChooser(dbConnection:RdbmsConnectionConfig): Observable<RdbmsSchemas> {
        let body = dbConnection.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<RdbmsSchemas>(this.dbConnectionUrl + "/schemas", body, httpOptions).pipe(
            map(response => new RdbmsSchemas().deserialize(response)));
    }

    ping(host:string, port:number): Observable<boolean> {

        const httpOptions = {
            params: new HttpParams()
                .append('host', host)
                .append('port', ""+port)
        };

        return this.http
            .get<boolean>(this.dbConnectionUrl + "/ping", httpOptions)
    }

    getDrivers(): Observable<Array<RdbmsDriver>> {
        return this.http
            .get<Array<RdbmsDriver>>(this.dbDrivesUrl).pipe(
            map(RdbmsService.extractDrivers));
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
            .get<RdbmsSchema>(this.dbSchemaUrl, httpOptions).pipe(
            map(res => new RdbmsSchema().deserialize(res)));
    }
}
