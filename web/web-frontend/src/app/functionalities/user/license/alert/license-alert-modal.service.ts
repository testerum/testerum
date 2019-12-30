import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {AppComponent} from "../../../../app.component";
import {LicenseAlertModalComponent} from "./license-alert-modal.component";
import {ContextService} from "../../../../service/context.service";
import {Config} from "../../../../config";
import {Router} from "@angular/router";
import {DateUtil} from "../../../../utils/date.util";

@Injectable()
export class LicenseAlertModalService {

    private licenseAlertModalInstance: LicenseAlertModalComponent;
    private countdownUntilLicenseExpiredModalInterval: any;
    private isExpiredLicense: boolean;

    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private router: Router,
                private contextService: ContextService) {
    }

    private showAlertLicenseModal(isExpiredLicense: boolean) {

        if (window.location.pathname.startsWith("/license")) {
            return;
        }

        const factory = this.componentFactoryResolver.resolveComponentFactory(LicenseAlertModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: LicenseAlertModalComponent = modalComponentRef.instance;

        modalInstance.isExpiredLicense = isExpiredLicense;
        modalInstance.licenseAlertModalService = this;

        modalInstance.modalComponentRef = modalComponentRef;

        this.licenseAlertModalInstance = modalInstance;
    }

    public onApplicationInitialize() {
        let licenseInfo = this.contextService.license.getLicenseInfo();

        this.isExpiredLicense = false;

        if (licenseInfo.currentUserLicense) {
            if (!licenseInfo.currentUserLicense.expired) {
                return;
            }
            this.isExpiredLicense = true;
        }

        if (licenseInfo.trialLicense) {
            if (!licenseInfo.trialLicense.expired) {
                return;
            }
        }

        if (!this.shouldShowLicenseAlert()) {
            return;
        }

        this.showAlertLicenseModal(this.isExpiredLicense);
    }

    public startAlertLicenseModalCountdown() {
        this.countdownUntilLicenseExpiredModalInterval = setTimeout(() => {
            this.showAlertLicenseModal(this.isExpiredLicense);
        }, 1000*Config.COUNTDOWN_LICENSE_ALERT_MODAL_IN_SECONDS);
    }

    private shouldShowLicenseAlert(): boolean {
        let licenseAlertModalShownDate = this.contextService.license.getLicenseAlertModalShownDate();
        let secondsFromLastShown: number = DateUtil.getSecondsBetweenDates(new Date(), licenseAlertModalShownDate);
        return Config.COUNTDOWN_LICENSE_ALERT_MODAL_IN_SECONDS < secondsFromLastShown;
    }
}
