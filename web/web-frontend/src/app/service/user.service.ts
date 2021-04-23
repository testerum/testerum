import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {map} from "rxjs/operators";
import {LicenseInfo} from "../model/user/license/license-info.model";
import {AuthRequest} from "../model/user/auth/auth-request.model";
import {AuthResponse} from "../model/user/auth/auth-response.model";
import {Location} from "@angular/common";

@Injectable()
export class UserService {

    private readonly baseUrl: string;

    constructor(private http: HttpClient,
                location: Location) {
        this.baseUrl = location.prepareExternalUrl("/rest/user");
    }

    getLicenseInfo(): Observable<LicenseInfo> {
        return this.http
            .get<LicenseInfo>(this.baseUrl + "/license-info")
            .pipe(map(res => new LicenseInfo().deserialize(res)));
    }

    loginWithCredentials(authRequest: AuthRequest): Observable<AuthResponse> {
        let body = authRequest.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<AuthResponse>(this.baseUrl + "/login/credentials", body, httpOptions)
            .pipe(map(res => new AuthResponse().deserialize(res)));
    }

    loginWithLicenseFile(file: File): Observable<AuthResponse> {
        const formData: FormData = new FormData();
        formData.append("licenseFile", file);

        return this.http
            .post<AuthResponse>(this.baseUrl + "/login/file", formData)
            .pipe(map(res => new AuthResponse().deserialize(res)));
    }
}
