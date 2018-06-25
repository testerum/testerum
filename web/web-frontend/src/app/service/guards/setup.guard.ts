import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, CanActivate, CanActivateChild, Router, RouterStateSnapshot} from "@angular/router";
import {Observable} from "rxjs/Rx";
import {Subject} from "rxjs/Rx";
import {SetupService} from "../setup.service";
import {Setup} from "../../functionalities/config/setup/model/setup.model";

@Injectable()
export class SetupGuard implements CanActivate, CanActivateChild {

    constructor(
        private startCnfigService: SetupService,
        private router: Router
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
        this.startCnfigService.isConfigSet().subscribe(
            (isConfigSet: boolean) => {
                if (isConfigSet) {
                    return responseSubject.next(true)
                }

                this.router.navigate(['setup']);
                return responseSubject.next(false)
            }
        );

        return responseSubject;
    }
}
