import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Setup} from "./model/setup.model";
import {SetupService} from "../../../service/setup.service";
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
                private setupService: SetupService,
                private licenseService: LicenseService) {
    }

    ngOnInit() {
        if (!this.licenseService.isLoggedIn()) {
            this.urlService.navigateToLicense();
        }

        this.setupService.isConfigSet().subscribe(
            (isConfigSet: boolean) => {
                if(isConfigSet) {
                    this.urlService.navigateToRoot()
                }
            }
        );
    }

    save(): void {
        let startConfig = new Setup();
        startConfig.repositoryAbsoluteJavaPath = this.repositoryAbsoluteJavaPath;

        this.setupService.save(startConfig).subscribe(
            result => {
                this.urlService.navigateToRoot()
            }
        )
    }
}
