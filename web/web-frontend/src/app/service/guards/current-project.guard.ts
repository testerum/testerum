import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, CanActivate, CanActivateChild, RouterStateSnapshot} from "@angular/router";
import {Observable} from "rxjs";
import {SelectProjectModalService} from "../../functionalities/home/multiple-projects-found/select-project-modal.service";
import {UrlUtil} from "../../utils/url.util";
import {ContextService} from "../context.service";
import {Project} from "../../model/home/project.model";
import {ProjectService} from "../project.service";
import {InfoModalService} from "../../generic/components/info_modal/info-modal.service";
import {UrlService} from "../url.service";

@Injectable()
export class CurrentProjectGuard implements CanActivate, CanActivateChild {

    constructor(
        private selectProjectModalService: SelectProjectModalService,
        private contextService: ContextService,
        private projectService: ProjectService,
        private infoModalService: InfoModalService,
        private urlService: UrlService
    ) {}

    async canActivate(
        next: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): Promise<boolean> {

        let projectName = UrlUtil.getProjectNameFromUrl(state.url);
        if (projectName == null) {
            this.contextService.setCurrentProject(null);
            return false;
        }

        if (this.contextService.getCurrentProject() != null && this.contextService.getCurrentProject().name.toUpperCase() == projectName.toUpperCase()) {
            return true;
        }

        return await this.isKnownProject(projectName);
    }

    canActivateChild(
        next: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {

        return true;
    }

    private async isKnownProject(projectName: string): Promise<boolean> {
        const knownProjects: Array<Project> = await this.projectService.getAllProjects().toPromise();
        let foundProjects = knownProjects.filter((project: Project) => projectName.toUpperCase() === project.name.toUpperCase());

        if (foundProjects.length == 0) {
            this.infoModalService.showInfoModal("Project Not Found",
                "Project \"<b>"+projectName+"</b>\" is not known by this Testerum instance.",
                [
                    "Please first open this project with Testerum and retry your URL."
                ]
            ).subscribe( value => {
                this.urlService.navigateToHomePage();
            });
            return false;
        }

        if (foundProjects.length > 1) {
            this.contextService.setCurrentProject(
                await this.selectProjectModalService.showModal(foundProjects).toPromise()
            );

            return true;
        }

        this.contextService.setCurrentProject (foundProjects[0]);

        return true;
    }
}
