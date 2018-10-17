import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {AuthRequest} from "./model/authRequest";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AuthResponse} from "./model/authResponse";
import {map} from "rxjs/operators";
import {UrlService} from "../../../service/url.service";
import {StringUtils} from "../../../utils/string-utils.util";

@Injectable()
export class LicenseService {
    private LOCAL_STORAGE_AUTH_TOKEN_KEY = "authToken";

    private BASE_URL = "/rest/license";

    constructor(private http: HttpClient,
                private urlService: UrlService) {
    }

    isLoggedIn(): boolean {
        let authToken = localStorage.getItem(this.LOCAL_STORAGE_AUTH_TOKEN_KEY);
        return !StringUtils.isEmpty(authToken)
    }

    setLicense(authToken: string) {
        localStorage.setItem(this.LOCAL_STORAGE_AUTH_TOKEN_KEY, authToken);
        if (this.isLoggedIn()) {
            this.urlService.navigateToRoot();
        }
    }

    logout() {
        localStorage.removeItem(this.LOCAL_STORAGE_AUTH_TOKEN_KEY);
        this.urlService.navigateToLicense();
    }

    createTrialAccount(authModel: AuthRequest): Observable<AuthResponse> {
        const body = authModel.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<AuthResponse>(this.BASE_URL + "/create-trial-account", body, httpOptions)
            .pipe(map(res => new AuthResponse().deserialize(res)));
    }

    loginWithCredentials(authModel: AuthRequest): Observable<AuthResponse> {
        const body = authModel.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<AuthResponse>(this.BASE_URL + "/login/credentials", body, httpOptions)
            .pipe(map(res => new AuthResponse().deserialize(res)));
    }

    loginWithLicenseFile(file: File): Observable<AuthResponse> {
        const formData: FormData = new FormData();
        formData.append("licenseFile", file);

        return this.http
            .post<AuthResponse>(this.BASE_URL + "/login/file", formData)
            .pipe(map(res => new AuthResponse().deserialize(res)));
    }

}
