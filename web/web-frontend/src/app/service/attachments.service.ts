import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';


import {Path} from "../model/infrastructure/path/path.model";
import {HttpClient, HttpParams} from "@angular/common/http";
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
            .get<Array<Attachment>>(this.ATTACHMENTS_URL, httpOptions).pipe(
            map(AttachmentsService.extractAttachments));
    }

    delete(path: Path): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', path.toString())
        };

        return this.http
            .delete<void>(this.ATTACHMENTS_URL, httpOptions);
    }

    private static extractAttachments(res: Array<Attachment>): Array<Attachment> {
        let result: Array<Attachment> = [];
        if(res == null) return result;

        for (const resAttachment of res) {
            result.push(
                new Attachment().deserialize(resAttachment)
            )
        }
        return result;
    }
}


