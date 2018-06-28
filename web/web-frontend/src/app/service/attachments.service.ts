import {Injectable} from "@angular/core";
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import {Path} from "../model/infrastructure/path/path.model";
import {HttpClient, HttpParams, HttpResponse} from "@angular/common/http";
import {Attachment} from "../model/file/attachment.model";

@Injectable()
export class AttachmentsService {

    private ATTACHMENTS_URL = "/rest/attachments";

    constructor(private http: HttpClient) {
    }

    getAttachments(path: Path): Observable<Array<Attachment>> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', path.toString())
        };

        return this.http
            .get<any>(this.ATTACHMENTS_URL, httpOptions)
            .map(AttachmentsService.extractAttachments);
    }

    delete(path: Path): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', path.toString())
        };

        return this.http
            .delete<any>(this.ATTACHMENTS_URL, httpOptions);
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


