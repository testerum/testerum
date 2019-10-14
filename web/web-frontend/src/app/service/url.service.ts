import {NavigationExtras, Router} from "@angular/router";
import {Path} from "../model/infrastructure/path/path.model";
import {Injectable} from "@angular/core";
import {ContextService} from "./context.service";

@Injectable()
export class UrlService {

    constructor(private router: Router,
                private contextService: ContextService) {
    }

    public refreshPage() {
        window.location.reload();
    }

    public navigateToRoot() {
        this.navigate(['']);
    }

    public navigateToHomePage() {
        this.navigate(["/"]);
    }

    public navigateToLicense(url: string) {
        this.contextService.setCurrentProject(null);
        this.navigate(["/license", {url: url}]);
    }

    public navigateToLicenseBuy() {
        let win = window.open("https://www.testerum.com/pricing/", '_blank');
        win.focus();
    }

    public navigateToFeatures() {
        this.navigateToProjectPath(["/project"]);
    }

    public navigateToCreateFeature(path: Path) {
        if (!path) {
            throw new Error("A path should be provided")
        }
        this.navigateToProjectPath(["/project/create", {path: path.toString()}]);
    }

    public navigateToFeature(path: Path) {
        if (!path) {
            throw new Error("A path should be provided")
        }
        this.navigateToProjectPath(["/project/show", {path: path.toString()}]);
    }

    public navigateToCreateTest(path: Path) {
        if (!path) {
            throw new Error("A path should be provided")
        }
        this.navigateToProjectPath(["/project/tests/create", {path: path.toString()}]);
    }

    public navigateToTest(path: Path) {
        if (!path) {
            throw new Error("A path should be provided")
        }
        this.navigateToProjectPath(["/project/tests/show", {path: path.toString()}]);
    }

    public navigateToResources() {
        this.navigateToProjectPath(["/resources"]);
    }

    public navigateToResource(path: Path) {
        this.navigateToProjectPath(["/resources/show", {"path": path}]);
    }

    public navigateToCreateResource(path: Path, resourceExtension: string) {
        this.navigateToProjectPath(["/resources/create", {"path": path, "resourceExt": resourceExtension}]);
    }

    public navigateToSteps() {
        this.navigateToProjectPath(["/steps"]);
    }

    public navigateToComposedStep(path: Path) {
        if (!path) {
            throw new Error("A path should be provided")
        }
        this.navigateToProjectPath(['/steps/composed', {path: path.toString()}]);
    }

    public navigateToBasicStep(path: Path) {
        if (!path) {
            throw new Error("A path should be provided")
        }
        this.navigateToProjectPath(['/steps/basic', {path: path.toString()}]);
    }

    public navigateToCreateComposedStep(path: Path) {
        if (!path) {
            throw new Error("A path should be provided")
        }
        this.navigateToProjectPath(["/steps/composed/create", {path: path.toString()}]);
    }

    public navigateToManualExecPlans() {
        this.navigateToProjectPath(["/manual/plans"])
    }

    public navigateToManualExecPlanCreate() {
        this.navigateToProjectPath(["/manual/plans/create"])
    }

    public navigateToManualExecPlanEditor(path: Path) {
        this.navigateToProjectPath(["/manual/plans/show", {planPath: path.toString()}])
    }

    public navigateToManualExecPlanRunner(planPath: Path) {
        this.navigateToProjectPath(["/manual/plans/runner", {planPath: planPath.toString()}])
    }

    public navigateToManualExecPlanTestRunner(planPath: Path, testPath: Path) {
        this.navigateToProjectPath(["/manual/plans/runner", {planPath: planPath.toString(), testPath: testPath.toString()}])
    }

    public navigateToAutomatedResult(path: Path, url: string) {
        this.navigateToProjectPath(["/automated/results", {path : path.toString(), url: url} ])
    }

    public navigateToNotFoundPage(url: string, resourceId: string, resourceType: string) {
        if (this.contextService.isProjectSelected()) {
            this.navigateToProjectPath(["/not-found", {url: url, resourceId: resourceId, resourceType: resourceType}]);
        } else {
            this.navigate(["/not-found", {url: url, resourceId: resourceId, resourceType: resourceType}]);
        }
    }

    private navigate(commands: any[], extras?: NavigationExtras): Promise<boolean> {
        return this.router.navigate(commands, extras);
    }

    private navigateToProjectPath(commands: any[], extras?: NavigationExtras): Promise<boolean> {
        commands[0] = "/" + this.contextService.getProjectName() + commands[0];
        return this.router.navigate(commands, extras);
    }
}
