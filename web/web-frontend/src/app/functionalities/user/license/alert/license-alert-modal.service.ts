import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {AppComponent} from "../../../../app.component";
import {LicenseAlertModalComponent} from "./license-alert-modal.component";
import {ContextService} from "../../../../service/context.service";
import {Config} from "../../../../config";

@Injectable()
export class LicenseAlertModalService {

    private licenseAlertModalInstance: LicenseAlertModalComponent;
    private countdownUntilLicenseExpiredModalInterval: any;
    private isTrialLicense: boolean;

    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private contextService: ContextService) {
    }

    showAlertLicenseModal(isTrialLicense: boolean) {
        const factory = this.componentFactoryResolver.resolveComponentFactory(LicenseAlertModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: LicenseAlertModalComponent = modalComponentRef.instance;

        modalInstance.isTrialLicense = isTrialLicense;
        modalInstance.licenseAlertModalService = this;

        modalInstance.modalComponentRef = modalComponentRef;

        this.licenseAlertModalInstance = modalInstance;
    }

    onApplicationInitialize() {
        let licenseInfo = this.contextService.license.getLicenseInfo();
        if (licenseInfo.trialLicense) {
            if (!licenseInfo.trialLicense.expired) {
                this.isTrialLicense = false;
            }
        }
        if (licenseInfo.currentUserLicense) {
            if (!licenseInfo.currentUserLicense.expired) {
                this.isTrialLicense = false;
            }
        }
        this.startAlertLicenseModalCountdown();
    }

    public startAlertLicenseModalCountdown() {
        this.countdownUntilLicenseExpiredModalInterval = setTimeout(() => {
            this.showAlertLicenseModal(this.isTrialLicense);
        }, 1000*Config.COUNTDOWN_UNTIL_LICENSE_EXPIRED_MODAL_IS_SHOWN_IN_SECONDS);
    }
}
