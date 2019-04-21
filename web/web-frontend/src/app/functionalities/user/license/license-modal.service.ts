import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {AppComponent} from "../../../app.component";
import {LicenseModalComponent} from "./license-modal.component";
import {UserService} from "../../../service/user.service";
import {LicenseInfo} from "../../../model/user/license/license-info.model";

@Injectable()
export class LicenseModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private userService: UserService) {
    }

    showLicenseModal() {
        this.userService.getLicenseInfo().subscribe((licenseInfo: LicenseInfo) => {
            const factory = this.componentFactoryResolver.resolveComponentFactory(LicenseModalComponent);
            let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
            let modalInstance: LicenseModalComponent = modalComponentRef.instance;

            modalInstance.model = licenseInfo;
            modalInstance.modalComponentRef = modalComponentRef;
        });
    }
}
