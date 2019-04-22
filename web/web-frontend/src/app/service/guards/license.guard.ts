import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, CanActivate, CanActivateChild, RouterStateSnapshot} from "@angular/router";
import {Observable} from "rxjs";
import {UrlService} from "../url.service";
import {ContextService} from "../context.service";
import { Location } from "@angular/common";

@Injectable()
export class LicenseGuard implements CanActivate, CanActivateChild {

    constructor(
        private contextService: ContextService,
        private urlService: UrlService,
        private location: Location
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
        if (!this.contextService.license.isLoggedIn()) {
            this.urlService.navigateToLicense(this.location.path());
        }

        return true;
    }
}
