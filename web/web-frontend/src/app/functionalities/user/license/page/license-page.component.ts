import {Component, OnInit} from '@angular/core';
import {LicenseModalService} from "../modal/license-modal.service";
import {UrlUtil} from "../../../../utils/url.util";
import {ActivatedRoute} from "@angular/router";
import {ContextService} from "../../../../service/context.service";

@Component({
    selector: 'license-page',
    templateUrl: './license-page.component.html',
    styleUrls: ['./license-page.component.scss']
})
export class LicensePageComponent implements OnInit {

    constructor(private activatedRoute: ActivatedRoute,
                private contextService: ContextService) {
    }

    ngOnInit() {
        let urlParam = UrlUtil.getParamFromUrl(this.activatedRoute, "url");
        if (this.contextService.license.hasValidLicenseOrValidTrial()) {
            window.location.href = urlParam
        }
    }

    onAuthentication() {
        let urlParam = UrlUtil.getParamFromUrl(this.activatedRoute, "url");
        window.location.href = urlParam;
    }
}
