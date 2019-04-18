import {AfterViewInit, Component, ComponentRef, OnInit, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {UserService} from "../../../service/user.service";
import {UserProfile} from "../../../model/license/profile/user-profile.model";
import {DateUtil} from "../../../utils/date.util";

@Component({
  selector: 'license-component',
  templateUrl: './license-modal.component.html',
  styleUrls: ['./license-modal.component.scss']
})
export class LicenseModalComponent implements OnInit, AfterViewInit {

    model: UserProfile;

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
}
