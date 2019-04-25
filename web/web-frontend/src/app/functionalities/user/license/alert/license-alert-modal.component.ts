import {
    AfterViewInit,
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    ComponentRef,
    OnInit,
    ViewChild
} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {LicenseAlertModalService} from "./license-alert-modal.service";
import {Config} from "../../../../config";
import {LicenseGuard} from "../../../../service/guards/license.guard";

@Component({
    selector: 'license-alert-component',
    templateUrl: './license-alert-modal.component.html',
    styleUrls: ['./license-alert-modal.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
})
export class LicenseAlertModalComponent implements OnInit, AfterViewInit {

    isTrialLicense: boolean;
    licenseAlertModalService: LicenseAlertModalService;

    counter: number;

    @ViewChild("userProfileModal") modal: ModalDirective;
    modalComponentRef: ComponentRef<LicenseAlertModalComponent>;

    constructor(private cd: ChangeDetectorRef) {
    }

    ngOnInit(): void {

    }

    ngAfterViewInit(): void {
        this.counter = Config.COUNTDOWN_LICENSE_EXPIRED_MODAL_IN_SECONDS;

        LicenseGuard.isLicenseExpiredAlertModalShown = true;

        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalComponentRef.destroy();
            this.modalComponentRef = null;
        });
        this.refresh();

        this.startCountdown();
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
    }

    onContinueEvaluating() {
        LicenseGuard.isLicenseExpiredAlertModalShown = false;
        this.modal.hide();
        this.licenseAlertModalService.startAlertLicenseModalCountdown();
    }

    private startCountdown() {

        let interval = setInterval(() => {
            this.counter--;

            if(this.counter == 0 ){
                clearInterval(interval);
            }
            this.refresh();

        }, 1000);
    }

    openBuyPage() {
        window.open("https://testerum.com/pricing/", '_blank').focus();
    }
}
