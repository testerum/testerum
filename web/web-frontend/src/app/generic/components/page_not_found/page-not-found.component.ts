
import {Component} from "@angular/core";
import {UrlService} from "../../../service/url.service";
@Component({
    moduleId:module.id,
    templateUrl:"page-not-found.component.html",
    styleUrls: ["page-not-found.component.scss"]
})
export class PageNotFoundComponent {


    constructor(private urlService: UrlService) {
    }

    onReturnToHome() {
        this.urlService.navigateToHomePage()
    }
}
