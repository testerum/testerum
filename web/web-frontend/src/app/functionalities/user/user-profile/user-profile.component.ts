import {AfterViewInit, Component, ComponentRef, OnInit, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {UserProfileService} from "../../../service/user-profile.service";
import {UserProfile} from "../../../model/license/profile/user-profile.model";

@Component({
  selector: 'user-profile.component',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit, AfterViewInit {

    model: UserProfile;

    @ViewChild("userProfileModal") modal:ModalDirective;
    modalComponentRef: ComponentRef<UserProfileComponent>;

    constructor(private userProfileService: UserProfileService) { }

    ngOnInit(): void {
        this.userProfileService.getCurrentUserProfile().subscribe((userProfile) => {
            this.model = userProfile;
        });
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

}
