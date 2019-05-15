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

@Component({
    selector: 'license-about-to-expire-modal',
    templateUrl: './license-about-to-expire-modal.component.html',
    styleUrls: ['./license-about-to-expire-modal.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
})
export class LicenseAboutToExpireModalComponent implements OnInit, AfterViewInit {

    daysUntilExpiration: number;
    isTrialLicense: boolean;

    @ViewChild("userProfileModal") modal: ModalDirective;
    modalComponentRef: ComponentRef<LicenseAboutToExpireModalComponent>;

    constructor(private cd: ChangeDetectorRef) {
    }

    ngOnInit(): void {
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

    cancel() {
        this.modal.hide();
        this.refresh();
    }

    ok() {
        this.modal.hide();
        this.refresh();
    }

    navigateToBuyPage() {
        let win = window.open("https://www.testerum.com/pricing/", '_blank');
        win.focus();
    }
}
