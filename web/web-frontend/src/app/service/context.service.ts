import {Injectable, Injector} from "@angular/core";
import {StepCallContainerComponent} from "../generic/components/step-call-tree/nodes/step-call-container/step-call-container.component";
import {StepCallContainerModel} from "../generic/components/step-call-tree/model/step-call-container.model";
import {ActivatedRouteSnapshot, Router} from "@angular/router";
import {ProjectService} from "./project.service";
import {Project} from "../model/home/project.model";

@Injectable()
export class ContextService {

    currentProject: Project;

    stepToCut: StepCallContainerComponent = null;
    stepToCopy: StepCallContainerComponent = null;

    constructor(private injector: Injector,
                private projectService: ProjectService) {
    }

    init() {
        return new Promise((resolve, reject) => {
            let projectName = this.getProjectFromUrl();

            if (projectName) {
                this.projectService.getAllProjects().subscribe((value: Project[]) => {
                    let foundProjects = value.filter((project:Project) => projectName.toUpperCase() === project.name.toUpperCase());
                    if (foundProjects.length == 0) {
                        // showUnknownProjectAlert();
                    }
                    if (foundProjects.length > 1) {
                        // showChooseProjectForUrl();
                    }

                    this.currentProject = foundProjects[0];

                    resolve(true);
                });
            } else {
                resolve(true);
            }
        })
    }

    isProjectSelected(): boolean {
        return this.currentProject != null;
    }

    getProjectName(): string {
        return this.currentProject ? this.currentProject.name: null;
    }

    setCurrentProject(currentProject: Project) {
        this.currentProject = currentProject;
        this.getRouter().navigate(["/" + currentProject.name + "/features"]);
    }

    getProjectPath(): string {
        return this.currentProject ? this.currentProject.path: null;
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

    private getProjectFromUrl(): string {
        let root: ActivatedRouteSnapshot = this.getRouter().routerState.snapshot.root;
        if(root.children.length == 0) {
            return null;
        }

        let firstActivatedRoute = root.children[0];
        return firstActivatedRoute.params['project'];
    }

    private getRouter(): Router {
        return this.injector.get(Router);
    }
}
