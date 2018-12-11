import {Injectable} from "@angular/core";
import {StepCallContainerComponent} from "../generic/components/step-call-tree/nodes/step-call-container/step-call-container.component";
import {StepCallContainerModel} from "../generic/components/step-call-tree/model/step-call-container.model";
import {Project} from "../model/home/project.model";
import {Path} from "../model/infrastructure/path/path.model";
import {ActivatedRouteSnapshot, NavigationStart, Router} from "@angular/router";
import {StringUtils} from "../utils/string-utils.util";

@Injectable()
export class ContextService {

    stepToCut: StepCallContainerComponent = null;
    stepToCopy: StepCallContainerComponent = null;


    constructor(private router: Router) {
    }

    get project(): Project {
        return this.getProjectFromUrl();
    }

    set project(projectName: string) {
        this.router.navigate(["/" + projectName + "/features"]);
    }

    getProjectName(): string {
        return this._project? this._project.name: null;
    }

    setPathToCut(stepCallContainerComponent: StepCallContainerComponent) {
        this.stepToCopy = null;
        this.stepToCut = stepCallContainerComponent;
    }

    setPathToCopy(stepCallContainerComponent: StepCallContainerComponent) {
        this.stepToCopy = stepCallContainerComponent;
        this.stepToCut = null;
    }

    canPaste(stepCallContainerModel: StepCallContainerModel): boolean {
        return (this.stepToCopy != null && !(this.stepToCopy.model.stepCall == stepCallContainerModel.stepCall))
            || (this.stepToCut != null && !(this.stepToCut.model.stepCall == stepCallContainerModel.stepCall));
    }

    private getProjectFromUrl(): Project {
        let root: ActivatedRouteSnapshot = this.router.routerState.snapshot.root;
        if(root.children.length == 0) {
            return null;
        }

        let firstActivatedRoute = root.children[0];
        let projectParam: string = firstActivatedRoute.params['project'];

        if (!projectParam) {
            return null
        }

        return new Project(projectParam, Path.createInstanceOfEmptyPath());
    }
}
