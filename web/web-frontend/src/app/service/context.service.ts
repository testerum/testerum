import {Injectable, Injector} from "@angular/core";
import {StepCallContainerComponent} from "../generic/components/step-call-tree/nodes/step-call-container/step-call-container.component";
import {StepCallContainerModel} from "../generic/components/step-call-tree/model/step-call-container.model";
import {NavigationEnd, Router, RouterEvent} from "@angular/router";
import {ProjectService} from "./project.service";
import {Project} from "../model/home/project.model";
import {filter} from "rxjs/operators";
import {UrlUtil} from "../utils/url.util";
import {MultipleProjectsFound} from "../functionalities/home/alerts/multiple-projects-found/model/multiple-projects-found.model";

@Injectable()
export class ContextService {

    readonly routsWithoutProject: string[] = [
        "/multipleProjectsFound",
        "/noProjectFound"
    ];

    currentProject: Project;

    stepToCut: StepCallContainerComponent = null;
    stepToCopy: StepCallContainerComponent = null;

    private knownProjects: Project[] = [];
    constructor(private injector: Injector,
                private projectService: ProjectService) {
    }

    init() {
        return new Promise((resolve, reject) => {
            this.getRouter().events.pipe(
                filter(event => event instanceof NavigationEnd))
                .subscribe((value: RouterEvent) => {
                    if(this.isPathWithoutRequiredProject()) {
                        return
                    }

                    let projectName = UrlUtil.getProjectNameFromUrl(value.url);
                    this.resolveCurrentProject(projectName);
                });

            if(this.isPathWithoutRequiredProject()) {
                resolve(true);
                return;
            }

            let projectName = this.getProjectFromUrl();

            this.projectService.getAllProjects().subscribe((value: Project[]) => {
                this.knownProjects = value;

                this.resolveCurrentProject(projectName);

                resolve(true);
            });
        })
    }

    private resolveCurrentProject(projectName: string) {
        if (projectName == null) {
            this.currentProject = null;
            return;
        }

        if (this.currentProject != null && this.currentProject.name.toUpperCase() == projectName.toUpperCase()) {
            return
        }

        let foundProjects = this.knownProjects.filter((project: Project) => projectName.toUpperCase() === project.name.toUpperCase());
        if (foundProjects.length == 0) {
            this.navigateToNoProjectFound(projectName);
        }
        if (foundProjects.length > 1) {
            this.navigateToMultipleProjectsFoundChooser(foundProjects);
        }

        this.currentProject = foundProjects[0];
    }

    isProjectSelected(): boolean {
        return this.currentProject != null;
    }

    getProjectName(): string {
        return this.currentProject ? this.currentProject.name: null;
    }

    setCurrentProject(currentProject: Project) {
        this.currentProject = currentProject;
        this.navigateToProject();
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
        let pathAsString = window.location.pathname;
        return UrlUtil.getProjectNameFromUrl(pathAsString)
    }

    private getRouter(): Router {
        return this.injector.get(Router);
    }

    private navigateToHomePage() {
        this.getRouter().navigate(["/"]);
    }

    private navigateToProject() {
        this.getRouter().navigate(["/" + this.currentProject.name + "/features"]);
    }

    private navigateToMultipleProjectsFoundChooser(projectsToChoose: Project[]) {
        let pathAsString = window.location.pathname;
        let model: MultipleProjectsFound = new MultipleProjectsFound();
        model.projects = projectsToChoose;
        model.urlToNavigate = pathAsString;

        this.getRouter().navigate(["/multipleProjectsFound", {data: model.serialize()}]);
    }

    private navigateToNoProjectFound(projectName: string) {
        this.getRouter().navigate(["/noProjectFound", {projectName: projectName}]);
    }

    private isPathWithoutRequiredProject(): boolean {
        let currentUrl: string = window.location.pathname;
        if(currentUrl == null) {
            return true;
        }
        for (const routeWithoutProject of this.routsWithoutProject) {
            if (currentUrl.startsWith(routeWithoutProject)) {
                return true
            }
        }
        return false;
    }
}
