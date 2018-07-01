import {Router} from "@angular/router";
import {Path} from "../model/infrastructure/path/path.model";
import {Injectable} from "@angular/core";

@Injectable()
export class UrlService {

    constructor(private router: Router) {}

    public navigateToFeatures() {
        this.router.navigate(["/features"]);
    }

    public navigateToFeature(path: Path) {
        if (!path) { throw new Error("A path should be provided")}
        this.router.navigate(["/features/show", {path : path.toString()} ]);
    }

    public navigateToCreateTest(path: Path) {
        if (!path) { throw new Error("A path should be provided")}
        this.router.navigate(["/features/tests/create", { path : path.toString()}]);
    }
}
