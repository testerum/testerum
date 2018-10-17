import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, CanActivate, CanActivateChild, RouterStateSnapshot} from "@angular/router";
import {Observable, Subject} from "rxjs";
import {SetupService} from "../setup.service";
import {UrlService} from "../url.service";
import {LicenseService} from "../../functionalities/config/license/license.service";

@Injectable()
export class SetupGuard implements CanActivate, CanActivateChild {

    private isConfigSet: boolean = false;

    constructor(
        private setupService: SetupService,
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
        let responseSubject: Subject<boolean> = new Subject<boolean>();

        if (!this.licenseService.isLoggedIn()) {
            this.urlService.navigateToLicense();
        }

        if (this.isConfigSet) {
            return true;
        }

        this.setupService.isConfigSet().subscribe(
            (isConfigSet: boolean) => {
                if (isConfigSet) {
                    this.isConfigSet = true;
                    return responseSubject.next(true)
                }

                this.urlService.navigateToSetup();
                return responseSubject.next(false)
            }
        );

        return responseSubject;
    }
}
