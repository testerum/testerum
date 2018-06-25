import {Injectable} from '@angular/core';
import {Http, RequestOptions, Response, Headers, URLSearchParams} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import {RdbmsConnectionConfig} from "../../../model/resource/rdbms/rdbms-connection-config.model";
import {RdbmsDriver} from "../../../functionalities/resources/editors/database/connection/model/rdbms-driver.model";
import {RdbmsSchemas} from "../../../functionalities/resources/editors/database/connection/model/rdbms-schemas.model";
import {RdbmsSchema} from "../../../model/resource/rdbms/schema/rdbms-schema.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {ErrorService} from "../../error.service";

@Injectable()
export class RdbmsService {

    private DB_CONNECTION_URL = "/rest/rdbms";
    private DB_DRIVES_URL = "/rest/rdbms/drivers";
    private DB_SCHEMA_URL = "/rest/rdbms/schema";

    constructor(private http: Http,
                private errorService: ErrorService) {
    }

    showSchemasChooser(dbConnection:RdbmsConnectionConfig): Observable<RdbmsSchemas> {
        let body = dbConnection.serialize();
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        return this.http
            .post(this.DB_CONNECTION_URL + "/schemas", body, options)
            .map(response => new RdbmsSchemas().deserialize(response.json()))
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    ping(host:string, port:number): Observable<boolean> {

        let pingParams: URLSearchParams = new URLSearchParams();
        pingParams.set('host', host);
        pingParams.set('port', ""+port);

        let options = new RequestOptions({ search: pingParams });

        return this.http
            .get(this.DB_CONNECTION_URL + "/ping", options)
            .map(response => response.text() == "true")
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    getDrivers(): Observable<Array<RdbmsDriver>> {
        return this.http
            .get(this.DB_DRIVES_URL)
            .map(RdbmsService.extractDrivers)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    getSchema(path:Path):  Observable<RdbmsSchema> {
        let params: URLSearchParams = new URLSearchParams();
        params.set('path', path.toString());

        return this.http
            .get(this.DB_SCHEMA_URL, {search: params})
            .map(RdbmsService.extractSchema)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    private static extractSchema(res: Response): RdbmsSchema {
        let json = res.json();
        return new RdbmsSchema().deserialize(json);
    }

    private static extractDrivers(res: Response): Array<RdbmsDriver> {
        let json = res.json();

        let response: Array<RdbmsDriver> = [];
        for (let dbDriverAsJson of json) {
            let dbDriver = new RdbmsDriver().deserialize(dbDriverAsJson);
            response.push(dbDriver)
        }

        return response;
    }
}
