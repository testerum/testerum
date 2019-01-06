import {AfterViewInit, Component, ComponentRef, OnInit, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {UserProfile} from "./model/user-profile.model";

@Component({
  selector: 'user-profile.component',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit, AfterViewInit {

    model: UserProfile;

    @ViewChild("userProfileModal") modal:ModalDirective;
    modalComponentRef: ComponentRef<UserProfileComponent>;

    constructor() { }

    ngOnInit(): void {
        let userProfile = new UserProfile(); //Must be replaced later
        userProfile.name = "marius.gradinaru";
        userProfile.license = "{SDFHJB-4J4K-3KCZ-ADFBK8}";
        userProfile.version = "1.01.19.111";
        userProfile.expirationDate = new Date();
        this.model = userProfile;
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
