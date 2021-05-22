import {Observable, Subject} from 'rxjs';
import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Location} from "@angular/common";

@Injectable()
export class UtilService {

    private readonly pingRequestPath: string;

    constructor(private http: HttpClient,
                location: Location) {
        this.pingRequestPath = location.prepareExternalUrl("/rest/version");
    }

    checkIfServerIsAvailable(): Observable<boolean> {

        let responseSubject: Subject<boolean> = new Subject<boolean>();
        this.http
            .get<string>(this.pingRequestPath)
            .subscribe(
                (data: string) => responseSubject.next(true), // success path
                error => {
                    responseSubject.next(false)
                } // error path
            );

        return responseSubject;
    }
}
