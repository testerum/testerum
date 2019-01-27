import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, CanActivate, CanActivateChild, RouterStateSnapshot} from "@angular/router";
import {Observable} from "rxjs";
import {UrlService} from "../url.service";
import {LicenseService} from "../../functionalities/config/license/license.service";

@Injectable()
export class SetupGuard implements CanActivate, CanActivateChild {

    constructor(
        private licenseService: LicenseService,
        private urlService: UrlService
    ) {}

    canActivate(
        next: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {

        return this.startConfigCanActivate();
    }

    canActivateChild(
        next: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {

        return this.startConfigCanActivate();
    }

    private startConfigCanActivate() {
        if (!this.licenseService.isLoggedIn()) {
            this.urlService.navigateToLicense();
        }

        return true;
    }
}
