import {
    AfterViewInit,
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    ComponentRef,
    OnInit,
    ViewChild
} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {DateUtil} from "../../../../utils/date.util";
import {LicenseInfo} from "../../../../model/user/license/license-info.model";
import {UserService} from "../../../../service/user.service";
import {ContextService} from "../../../../service/context.service";
import {UrlService} from "../../../../service/url.service";

@Component({
    selector: 'license-modal',
    templateUrl: './license-modal.component.html',
    styleUrls: ['./license-modal.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
})
export class LicenseModalComponent implements AfterViewInit {

    model: LicenseInfo;

    @ViewChild("userProfileModal", { static: true }) modal: ModalDirective;
    modalComponentRef: ComponentRef<LicenseModalComponent>;

    constructor(private cd: ChangeDetectorRef,
                private userService: UserService,
                private contextService: ContextService,
                private urlService: UrlService) {
    }

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalComponentRef.destroy();
            this.modalComponentRef = null;
        });
        this.refresh();
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
    }

    cancel() {
        this.modal.hide();
    }

    logout() {
        this.contextService.license.logout();
        this.modal.hide();
        this.urlService.navigateToLicense(window.location.href);
    }

    ok() {
        this.modal.hide();
    }

    getDateAsString(date: Date): string {
        if (!date) {
            return "";
        }
        return DateUtil.dateToShortString(date);
    }

    isTrialLicense(): boolean {
        return !!this.model.trialLicense;
    }

    isUserAuthenticated(): boolean {
        return !!this.model.currentUserLicense;
    }

    serverRequiresAuthentication(): boolean {
        return this.model.serverHasLicenses && !this.model.currentUserLicense;
    }

    getTrialRemainingDays(): number {
        return this.model.trialLicense.daysUntilExpiration;
    }

    isTrialLicenseExpired(): boolean {
        return this.model.trialLicense.expired;
    }

    isUserLicenseExpired(): boolean {
        return this.model.currentUserLicense.expired;
    }

    onAuthenticationChanged() {
        this.userService.getLicenseInfo().subscribe((licenseInfo: LicenseInfo) => {
            this.model = licenseInfo;
            this.refresh();
        });
    }
}
