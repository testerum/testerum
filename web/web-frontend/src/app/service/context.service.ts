import {Injectable} from "@angular/core";
import {StepCallContainerComponent} from "../generic/components/step-call-tree/nodes/step-call-container/step-call-container.component";
import {StepCallContainerModel} from "../generic/components/step-call-tree/model/step-call-container.model";
import {Project} from "../model/project/project.model";
import {Path} from "../model/infrastructure/path/path.model";
import {NavigationStart, Router} from "@angular/router";
import {StringUtils} from "../utils/string-utils.util";

@Injectable()
export class ContextService {

    private _project: Project;

    stepToCut: StepCallContainerComponent = null;
    stepToCopy: StepCallContainerComponent = null;


    constructor(private router: Router) {
        router.events.forEach((event) => {
            if (event instanceof NavigationStart) {
                let allUrlParts = event.url.split("/");
                let urlParts = allUrlParts.filter((it: string) => StringUtils.isNotEmpty(it));

                if (urlParts.length == 0 ) {
                    this._project = null;
                    return;
                }

                let projectNameAsString = urlParts[0];
                this._project = new Project(projectNameAsString, Path.createInstanceOfEmptyPath());
            }
        });
    }

    get project(): Project {
        return this._project;
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
}
