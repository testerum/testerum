import {Component, OnInit} from '@angular/core';
import {Setup} from "./model/setup.model";
import {SetupService} from "../../../service/setup.service";
import {UrlService} from "../../../service/url.service";

@Component({
    selector: 'setup',
    templateUrl: 'setup.component.html',
    styleUrls: ['setup.component.scss']
})
export class SetupComponent implements OnInit {

    repositoryAbsoluteJavaPath: string;

    constructor(private urlService: UrlService,
                private startConfigService: SetupService) {
    }

    ngOnInit() {
        this.startConfigService.isConfigSet().subscribe(
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

        this.startConfigService.save(startConfig).subscribe(
            result => {
                this.urlService.navigateToRoot()
            }
        )
    }
}
