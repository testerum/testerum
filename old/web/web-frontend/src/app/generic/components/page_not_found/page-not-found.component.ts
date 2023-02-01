
import {Component, OnInit} from "@angular/core";
import {UrlService} from "../../../service/url.service";
import {Project} from "../../../model/home/project.model";
import {ProjectService} from "../../../service/project.service";
import {ActivatedRoute} from "@angular/router";
import {UrlUtil} from "../../../utils/url.util";
@Component({
    moduleId:module.id,
    templateUrl:"page-not-found.component.html",
    styleUrls: ["page-not-found.component.scss"]
})
export class PageNotFoundComponent implements OnInit {

    projectName: string;

    constructor(private activatedRoute: ActivatedRoute,
                private urlService: UrlService,
                private projectService: ProjectService,) {
    }

    ngOnInit(): void {
        let projectNameFromUrl = UrlUtil.getProjectNameFromActivatedRoute(this.activatedRoute);
        if (projectNameFromUrl) {
            this.projectService.getAllProjects().subscribe(((projects: Array<Project>) => {
                projects.forEach((project: Project) => {
                    if (project.name.toUpperCase() === projectNameFromUrl.toUpperCase()) {
                        this.projectName = projectNameFromUrl
                    }
                })
            }))
        }
    }

    onReturnToHome() {
        this.urlService.navigateToHomePage()
    }
}
