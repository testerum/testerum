import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {AppComponent} from "../../../app.component";
import {LicenseModalComponent} from "./license-modal.component";
import {UserProfileService} from "../../../service/user-profile.service";
import {UserProfile} from "../../../model/license/profile/user-profile.model";

@Injectable()
export class LicenseModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private userProfileService: UserProfileService) {
    }

    showUserProfileModal() {
        this.userProfileService.getCurrentUserProfile().subscribe((userProfile: UserProfile) => {
            const factory = this.componentFactoryResolver.resolveComponentFactory(LicenseModalComponent);
            let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
            let modalInstance: LicenseModalComponent = modalComponentRef.instance;

            modalInstance.model = userProfile;
            modalInstance.modalComponentRef = modalComponentRef;
        });
    }
}
