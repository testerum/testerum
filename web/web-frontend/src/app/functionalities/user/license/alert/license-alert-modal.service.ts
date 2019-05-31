import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {AppComponent} from "../../../../app.component";
import {LicenseAlertModalComponent} from "./license-alert-modal.component";
import {ContextService} from "../../../../service/context.service";
import {Config} from "../../../../config";
import {Router} from "@angular/router";

@Injectable()
export class LicenseAlertModalService {

    private licenseAlertModalInstance: LicenseAlertModalComponent;
    private countdownUntilLicenseExpiredModalInterval: any;
    private isTrialLicense: boolean;

    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private router: Router,
                private contextService: ContextService) {
    }

    private showAlertLicenseModal(isTrialLicense: boolean) {

        if (window.location.pathname.startsWith("/license")) {
            return;
        }

        const factory = this.componentFactoryResolver.resolveComponentFactory(LicenseAlertModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: LicenseAlertModalComponent = modalComponentRef.instance;

        modalInstance.isTrialLicense = isTrialLicense;
        modalInstance.licenseAlertModalService = this;

        modalInstance.modalComponentRef = modalComponentRef;

        this.licenseAlertModalInstance = modalInstance;
    }

    public onApplicationInitialize() {
        let licenseInfo = this.contextService.license.getLicenseInfo();
        if (!licenseInfo.trialLicense && !licenseInfo.currentUserLicense && licenseInfo.serverHasLicenses) {
            return;
        }
        this.isTrialLicense = false;

        if (licenseInfo.currentUserLicense &&
            !licenseInfo.currentUserLicense.expired) {
                return
        }
        if (licenseInfo.trialLicense) {
            if (!licenseInfo.trialLicense.expired) {
                return;
            }
            this.isTrialLicense = true;
        }

        this.showAlertLicenseModal(this.isTrialLicense);
    }

    public startAlertLicenseModalCountdown() {
        this.countdownUntilLicenseExpiredModalInterval = setTimeout(() => {
            this.showAlertLicenseModal(this.isTrialLicense);
        }, 1000*Config.COUNTDOWN_UNTIL_LICENSE_EXPIRED_MODAL_IS_SHOWN_IN_SECONDS);
    }
}
