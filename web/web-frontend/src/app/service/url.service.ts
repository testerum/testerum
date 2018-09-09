import {Router} from "@angular/router";
import {Path} from "../model/infrastructure/path/path.model";
import {Injectable} from "@angular/core";

@Injectable()
export class UrlService {

    constructor(private router: Router) {}

    public navigateToRoot() {
        this.router.navigate(['']);
    }

    public navigateToFeatures() {
        this.router.navigate(["/features"]);
    }

    public navigateToCreateFeature(path: Path) {
        if (!path) { throw new Error("A path should be provided")}
        this.router.navigate(["/features/create", {path : path.toString()} ]);
    }

    public navigateToFeature(path: Path) {
        if (!path) { throw new Error("A path should be provided")}
        this.router.navigate(["/features/show", {path : path.toString()} ]);
    }

    public navigateToCreateTest(path: Path) {
        if (!path) { throw new Error("A path should be provided")}
        this.router.navigate(["/features/tests/create", { path : path.toString()}]);
    }

    public navigateToTest(path: Path) {
        if (!path) { throw new Error("A path should be provided")}
        this.router.navigate(["/features/tests/show", {path : path.toString()} ]);
    }

    public navigateToResources() {
        this.router.navigate(["/resources"]);
    }

    public navigateToSteps() {
        this.router.navigate(["/steps"]);
    }

    public navigateToComposedStep(path: Path) {
        if (!path) { throw new Error("A path should be provided")}
        this.router.navigate(['/steps/composed', {path: path.toString()}]);
    }

    public navigateToBasicStep(path: Path) {
        if (!path) { throw new Error("A path should be provided")}
        this.router.navigate(['/steps/basic', {path: path.toString()}]);
    }

    public navigateToCreateComposedStep(path: Path) {
        if (!path) { throw new Error("A path should be provided")}
        this.router.navigate(["/steps/composed/create", { path : path.toString()}]);
    }

    public navigateToSetup() {
        this.router.navigate(["/setup"]);
    }

    public navigateToManualExecPlanCreate() {
        this.router.navigate(["/manual/plans/create"])
    }

    public navigateToManualExecPlanEditor(path: Path) {
        this.router.navigate(["/manual/plans/show", {path : path.toString()} ])
    }

    public navigateToManualExecPlanRunner(path: Path) {
        this.router.navigate(["/manual/runner", {path : path.toString()} ])
    }
}
