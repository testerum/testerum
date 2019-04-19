import {AfterViewInit, Component, ComponentRef, OnInit, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {DateUtil} from "../../../utils/date.util";
import {LicenseInfo} from "../../../model/user/license/license-info.model";

@Component({
  selector: 'license-component',
  templateUrl: './license-modal.component.html',
  styleUrls: ['./license-modal.component.scss']
})
export class LicenseModalComponent implements OnInit, AfterViewInit {

    model: LicenseInfo;

    @ViewChild("userProfileModal") modal:ModalDirective;
    modalComponentRef: ComponentRef<LicenseModalComponent>;

    constructor() { }

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

    getExpirationDateAsString(): string {
        if (this.model.trialLicense) {
            return this.getDateAsString(this.model.trialLicense.endDate)
        }
        if (this.model.currentUserLicense) {
            return this.getDateAsString(this.model.currentUserLicense.expirationDate)
        }
        return "";
    }
}
