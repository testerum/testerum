import {Router} from "@angular/router";
import {Injectable} from "@angular/core";

@Injectable()
export class ReportUrlService {

    constructor(private router: Router) {}

    public navigateReport() {
        this.router.navigate(['']);
    }

    public navigateToTagsOverview() {
        this.router.navigate(["tags"]);
    }

    public navigateToTagReport(tag: string) {
        this.router.navigate([".", "report", {tag : tag}]);
    }
}
