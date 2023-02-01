import {
    AfterViewInit,
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    ComponentRef,
    ViewChild
} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {LicenseAlertModalService} from "./license-alert-modal.service";
import {UrlService} from "../../../../service/url.service";
import {ContextService} from "../../../../service/context.service";

@Component({
    selector: 'license-alert-component',
    templateUrl: './license-alert-modal.component.html',
    styleUrls: ['./license-alert-modal.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
})
export class LicenseAlertModalComponent implements AfterViewInit {

    isExpiredLicense: boolean;
    licenseAlertModalService: LicenseAlertModalService;

    @ViewChild("userProfileModal", {static: true}) modal: ModalDirective;
    modalComponentRef: ComponentRef<LicenseAlertModalComponent>;

    constructor(private cd: ChangeDetectorRef,
                private contextService: ContextService,
                private urlService: UrlService) {
    }

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalComponentRef.destroy();
            this.modalComponentRef = null;
        });
        this.refresh();
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
    }

    onContinueEvaluating() {
        this.contextService.license.saveLicenseAlertModalShownDate(new Date());
        this.modal.hide();
        this.licenseAlertModalService.startAlertLicenseModalCountdown();
    }

    openBuyPage() {
        window.open("https://testerum.com/pricing/", '_blank').focus();
    }

    onAuthenticate() {
        this.modal.hide();
        this.urlService.navigateToLicense("");
    }
}
