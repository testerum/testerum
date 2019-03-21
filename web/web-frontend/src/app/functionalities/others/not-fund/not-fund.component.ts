import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {UrlService} from "../../../service/url.service";
import {StringUtils} from "../../../utils/string-utils.util";

@Component({
    selector: 'not-fund',
    templateUrl: './not-fund.component.html',
    styleUrls: ['./not-fund.component.scss']
})
export class NotFundComponent implements OnInit {

    url: string;
    resourceId: string;
    resourceType: string;

    constructor(private route: ActivatedRoute,
                private urlService: UrlService) {
    }

    ngOnInit() {
        this.url = this.route.snapshot.params["url"];
        this.resourceId = this.route.snapshot.params["resourceId"];
        this.resourceType = this.route.snapshot.params["resourceType"];

        this.resourceId = this.resourceId != 'null' ? this.resourceId : null;
        this.resourceType = this.resourceType != 'null' ? this.resourceType : null;
    }

    isAKnownResource(): boolean {
        return StringUtils.isNotEmpty(this.resourceType);
    }

    goToHomePage() {
        this.urlService.navigateToHomePage();
    }

    getResourceTypeForUI(): string {
        if(!this.resourceType) return "";

        if (this.resourceType.endsWith(".yaml")) {
            let result = StringUtils.substringBeforeLast(this.resourceType, ".");
            result = result.replace(".", " ");
            return result.toUpperCase();
        }

        return this.resourceType.toUpperCase();
    }
}
