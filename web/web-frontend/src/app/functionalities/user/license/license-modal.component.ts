import {AfterViewInit, Component, ComponentRef, OnInit, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {DateUtil} from "../../../utils/date.util";
import {LicenseInfo} from "../../../model/user/license/license-info.model";
import {UserService} from "../../../service/user.service";

@Component({
  selector: 'license-component',
  templateUrl: './license-modal.component.html',
  styleUrls: ['./license-modal.component.scss']
})
export class LicenseModalComponent implements OnInit, AfterViewInit {

    model: LicenseInfo;

    @ViewChild("userProfileModal") modal:ModalDirective;
    modalComponentRef: ComponentRef<LicenseModalComponent>;

    constructor(private userService: UserService) { }

    ngOnInit(): void {

    }

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalComponentRef.destroy();
            this.modalComponentRef = null;
        })
    }

    cancel() {
        this.modal.hide();
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
        return DateUtil.getDaysBetweenDates(new Date(), this.model.trialLicense.endDate)
    }

    isTrialLicenseExpired(): boolean {
        return this.model.trialLicense.endDate < new Date();
    }

    isUserLicenseExpired(): boolean {
        return this.model.currentUserLicense.expirationDate < new Date();
    }

    onAuthenticationChanged() {
        this.userService.getLicenseInfo().subscribe((licenseInfo: LicenseInfo) => {
            this.model.init(licenseInfo);
        });
    }
}
