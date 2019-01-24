import {Injectable, Injector} from "@angular/core";
import {StepCallContainerComponent} from "../generic/components/step-call-tree/nodes/step-call-container/step-call-container.component";
import {StepCallContainerModel} from "../generic/components/step-call-tree/model/step-call-container.model";
import {ActivatedRouteSnapshot, NavigationEnd, Params, Router, RouterEvent} from "@angular/router";
import {ProjectService} from "./project.service";
import {Project} from "../model/home/project.model";
import {filter, map} from "rxjs/operators";
import {Path} from "../model/infrastructure/path/path.model";
import {Subscription} from "rxjs";
import {UrlUtil} from "../utils/url.util";
import {InfoModalService} from "../generic/components/info_modal/info-modal.service";
import {UrlService} from "./url.service";

@Injectable()
export class ContextService {

    currentProject: Project;

    stepToCut: StepCallContainerComponent = null;
    stepToCopy: StepCallContainerComponent = null;

    private knownProjects: Project[] = [];
    constructor(private injector: Injector,
                private projectService: ProjectService,
                private infoModalService: InfoModalService) {
    }

    init() {
        return new Promise((resolve, reject) => {
            this.getRouter().events.pipe(
                filter(event => event instanceof NavigationEnd))
                .subscribe((value: RouterEvent) => {
                    let projectName = UrlUtil.getProjectNameFromUrl(value.url);
                    this.resolveCurrentProject(projectName);
                });

            let projectName = this.getProjectFromUrl();

            this.projectService.getAllProjects().subscribe((value: Project[]) => {
                this.knownProjects = value;

                this.resolveCurrentProject(projectName);

                resolve(true);
            });
        })
    }

    private resolveCurrentProject(projectName) {
        if (projectName == null) {
            this.currentProject = null;
            return;
        }

        let foundProjects = this.knownProjects.filter((project: Project) => projectName.toUpperCase() === project.name.toUpperCase());
        if (foundProjects.length == 0) {
            this.infoModalService.showInfoModal(
                "Project Not Found",
                "Project <b>"+projectName+"</b> is not known by this Testerum instance.",
                [
                    "Please first open this project with Testerum and retry your URL."
                ]
            ).subscribe(value => {
                this.navigateToHomePage();
            })
        }
        if (foundProjects.length > 1) {
            // showChooseProjectForUrl();
        }

        this.currentProject = foundProjects[0];
    }

    private navigateToHomePage() {
        this.getRouter().navigate(["/"]);
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
