import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {AppComponent} from "../../../../app.component";
import {LicenseAboutToExpireModalComponent} from "./license-about-to-expire-modal.component";
import {ContextService} from "../../../../service/context.service";
import {DateUtil} from "../../../../utils/date.util";
import {Config} from "../../../../config";

@Injectable()
export class LicenseAboutToExpireModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private contextService: ContextService) {
    }

    showLicenseAboutToExpireModalIfIsTheCase() {
        let isTrialLicense = !!this.contextService.license.getLicenseInfo().trialLicense;
        if (isTrialLicense) {
            return;
        }

        let daysUntilExpiration: number = this.getDaysUntilExpiration();
        let lastUsedOfRemainingDaysLicenseAlert: number = this.contextService.license.getLastUsedOfRemainingDaysLicenseAlert();
        let nextAlertDaysRemaining: number = this.getNextToBeUsedOfRemainingDaysLicenseAlert(lastUsedOfRemainingDaysLicenseAlert);

        if(lastUsedOfRemainingDaysLicenseAlert > 0 &&
            daysUntilExpiration > lastUsedOfRemainingDaysLicenseAlert) {

            this.contextService.license.deleteLastUsedOfRemainingDaysLicenseAlert();
            return;
        }

        if (nextAlertDaysRemaining >= 0 && daysUntilExpiration >=0 &&
            daysUntilExpiration <= nextAlertDaysRemaining) {

            this.contextService.license.saveLastUsedOfRemainingDaysLicenseAlert(daysUntilExpiration);

            const factory = this.componentFactoryResolver.resolveComponentFactory(LicenseAboutToExpireModalComponent);
            let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
            let modalInstance: LicenseAboutToExpireModalComponent = modalComponentRef.instance;

            modalInstance.daysUntilExpiration = daysUntilExpiration;

            modalInstance.modalComponentRef = modalComponentRef;
        }
    }

    private getDaysUntilExpiration() {
        if (this.contextService.license.getLicenseInfo().trialLicense) {
            return this.contextService.license.getLicenseInfo().trialLicense.daysUntilExpiration;
        }

        if (this.contextService.license.getLicenseInfo().currentUserLicense) {
            return this.contextService.license.getLicenseInfo().currentUserLicense.daysUntilExpiration;
        }
        return -1;
    }

    private getNextToBeUsedOfRemainingDaysLicenseAlert(lastUsedOfRemainingDaysLicenseAlert: number): number {
        for (const alertDay of Config.REMAINING_DAYS_LICENSE_ALERT) {
            if(alertDay >= lastUsedOfRemainingDaysLicenseAlert) {
                continue;
            }

            return alertDay;
        }
        return -1;
    }
}
