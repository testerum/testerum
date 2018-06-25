
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import {ErrorService} from "./error.service";
import {Feature} from "../model/feature/feature.model";
import {Path} from "../model/infrastructure/path/path.model";
import {HttpClient, HttpHeaders, HttpParams, HttpResponse} from "@angular/common/http";
import {Headers, RequestOptions, Response} from "@angular/http";
import {ManualTestsService} from "../manual-tests/tests/service/manual-tests.service";
import {ManualTestModel} from "../manual-tests/model/manual-test.model";
import {Attachment} from "../model/file/attachment.model";
import {FeatureService} from "./feature.service";

@Injectable()
export class AttachmentsService {

    private ATTACHMENTS_URL = "/rest/attachments";

    constructor(private http: HttpClient,
                private errorService: ErrorService) {
    }

    getAttachments(path: Path): Observable<Array<Attachment>> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', path.toString())
        };

        return this.http
            .get<any>(this.ATTACHMENTS_URL, httpOptions)
            .map(AttachmentsService.extractAttachments)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    delete(path: Path): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', path.toString())
        };

        return this.http
            .delete<any>(this.ATTACHMENTS_URL, httpOptions)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    private static extractAttachments(res:  HttpResponse<any>): Array<Attachment> {
        let result: Array<Attachment> = [];
        if(res == null) return result;

        for (const resAttachment of res.body) {
            result.push(
                new Attachment().deserialize(resAttachment)
            )
        }
        return result;
    }
}


