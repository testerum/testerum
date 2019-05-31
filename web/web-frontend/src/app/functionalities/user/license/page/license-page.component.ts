import {Component, OnInit} from '@angular/core';
import {LicenseModalService} from "../modal/license-modal.service";
import {UrlUtil} from "../../../../utils/url.util";
import {ActivatedRoute} from "@angular/router";
import {ContextService} from "../../../../service/context.service";
import {PlatformLocation} from "@angular/common";

@Component({
    selector: 'license-page',
    templateUrl: './license-page.component.html',
    styleUrls: ['./license-page.component.scss']
})
export class LicensePageComponent implements OnInit {

    constructor(private activatedRoute: ActivatedRoute,
                private contextService: ContextService,
                private platformLocation: PlatformLocation) {
    }

    ngOnInit() {
        // let urlParam = UrlUtil.getParamFromUrl(this.activatedRoute, "url");
        // if (this.contextService.license.hasValidLicenseOrTrialValidOrExpired()) {
        //     window.location.href = urlParam
        // }

        this.platformLocation.onPopState(() => {
            window.location.href = "/";
        });
    }

    onAuthentication() {
        let urlParam = UrlUtil.getParamFromUrl(this.activatedRoute, "url");
        window.location.href = urlParam ? urlParam : "/";
    }
}
