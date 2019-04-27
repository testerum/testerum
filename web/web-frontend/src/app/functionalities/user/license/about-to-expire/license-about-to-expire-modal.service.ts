import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {AppComponent} from "../../../../app.component";
import {LicenseAboutToExpireModalComponent} from "./license-about-to-expire-modal.component";
import {ContextService} from "../../../../service/context.service";
import {DateUtil} from "../../../../utils/date.util";
import {Config} from "../../../../config";

@Injectable()
export class LicenseAboutToExpireModalService {

    LAST_USED_OF_REMAINING_DAYS_LICENSE_ALERT = "LAST_USED_OF_REMAINING_DAYS_LICENSE_ALERT";

    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private contextService: ContextService) {
    }

    showLicenseAboutToExpireModalIfIsTheCase() {
        let isTrialLicense = !!this.contextService.license.getLicenseInfo().trialLicense;

        let daysUntilExpiration: number = this.getDaysUntilLicenseExpires();
        let lastUsedOfRemainingDaysLicenseAlert: number = this.getLastUsedOfRemainingDaysLicenseAlert();
        let nextAlertDaysRemaining: number = this.getNextToBeUsedOfRemainingDaysLicenseAlert(lastUsedOfRemainingDaysLicenseAlert, isTrialLicense);

        if(lastUsedOfRemainingDaysLicenseAlert > 0 &&
            daysUntilExpiration > lastUsedOfRemainingDaysLicenseAlert) {

            this.deleteLastUsedOfRemainingDaysLicenseAlert();
            return;
        }

        if (nextAlertDaysRemaining >= 0 && daysUntilExpiration >=0 &&
            daysUntilExpiration <= nextAlertDaysRemaining) {

            this.saveLastUsedOfRemainingDaysLicenseAlert(daysUntilExpiration);

            const factory = this.componentFactoryResolver.resolveComponentFactory(LicenseAboutToExpireModalComponent);
            let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
            let modalInstance: LicenseAboutToExpireModalComponent = modalComponentRef.instance;

            modalInstance.daysUntilExpiration = daysUntilExpiration;
            modalInstance.isTrialLicense = isTrialLicense;

            modalInstance.modalComponentRef = modalComponentRef;
        }
    }

    private getDaysUntilLicenseExpires(): number {
        let expirationDate: Date | null = this.contextService.license.getLicenseInfo().getExpirationDate();
        if (!expirationDate) {
            return -1;
        }
        return DateUtil.getDaysBetweenDates(new Date(), expirationDate);
    }

    private getLastUsedOfRemainingDaysLicenseAlert(): number {
        let lastUsedOfRemainingDaysLicenseAlert = localStorage.getItem(this.LAST_USED_OF_REMAINING_DAYS_LICENSE_ALERT);
        //if is not initialized, set the initial value as an big number
        return lastUsedOfRemainingDaysLicenseAlert ? +lastUsedOfRemainingDaysLicenseAlert : 99999;
    }

    private getNextToBeUsedOfRemainingDaysLicenseAlert(lastUsedOfRemainingDaysLicenseAlert: number,
                                                       isTrialLicense: boolean): number {
        for (const alertDay of Config.REMAINING_DAYS_LICENSE_ALERT) {
            if (isTrialLicense && alertDay > 10) {
                continue;
            }

            if(alertDay >= lastUsedOfRemainingDaysLicenseAlert) {
                continue;
            }

            return alertDay;
        }
        return -1;
    }

    private deleteLastUsedOfRemainingDaysLicenseAlert() {
        localStorage.removeItem(this.LAST_USED_OF_REMAINING_DAYS_LICENSE_ALERT);
    }

    private saveLastUsedOfRemainingDaysLicenseAlert(nextAlertDaysRemaining: number) {
        localStorage.setItem(this.LAST_USED_OF_REMAINING_DAYS_LICENSE_ALERT, ""+nextAlertDaysRemaining);
    }
}
