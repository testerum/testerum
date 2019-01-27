import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {UrlService} from "../../../service/url.service";
import {LicenseService} from "../license/license.service";

@Component({
    selector: 'setup',
    templateUrl: 'setup.component.html',
    styleUrls: ['setup.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class SetupComponent implements OnInit {

    repositoryAbsoluteJavaPath: string;

    constructor(private urlService: UrlService,
                private licenseService: LicenseService) {
    }

    ngOnInit() {
        if (!this.licenseService.isLoggedIn()) {
            this.urlService.navigateToLicense();
        }

        this.urlService.navigateToRoot();
    }

    save(): void { }
}
