import {Component, OnDestroy, OnInit} from '@angular/core';
import {Project} from "../../model/home/project.model";
import {HomeService} from "../../service/home.service";
import {Subscription} from "rxjs";
import {ContextService} from "../../service/context.service";
import {Home} from "../../model/home/home.model";
import {ArrayUtil} from "../../utils/array.util";
import {CreateProjectService} from "./create-project/create-project.service";
import {FileDirChooserModalService} from "../../generic/components/form/file_dir_chooser/dialog/file-dir-chooser-modal.service";
import {ProjectService} from "../../service/project.service";
import {UrlService} from "../../service/url.service";
import {AreYouSureModalService} from "../../generic/components/are_you_sure_modal/are-you-sure-modal.service";
import {AreYouSureModalEnum} from "../../generic/components/are_you_sure_modal/are-you-sure-modal.enum";

@Component({
    selector: 'home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {

    quote: string;
    author: string;
    testerumVersion: string;
    recentProjects: Project[] = [];

    private getHomePageModelSubscription: Subscription;
    constructor(private homeService: HomeService,
                private projectService: ProjectService,
                private contextService: ContextService,
                private createProjectService: CreateProjectService,
                private fileDirChooserModalService: FileDirChooserModalService,
                private areYouSureModalService: AreYouSureModalService,
                private urlService: UrlService) {
    }

    ngOnInit() {
        this.getHomePageModelSubscription = this.homeService.getHomePageModel().subscribe((home: Home) => {
            this.contextService.setCurrentProject(null);
            this.quote = home.quote.text;
            this.author = home.quote.author;
            this.testerumVersion = home.testerumVersion;
            ArrayUtil.replaceElementsInArray(this.recentProjects, home.recentProjects);
        });
        this.contextService.setCurrentProject(null);
    }

    ngOnDestroy(): void {
        if (this.getHomePageModelSubscription) this.getHomePageModelSubscription.unsubscribe();
    }

    navigateToProject(project: Project) {
        this.contextService.setCurrentProject(project);
        this.urlService.navigateToFeatures()
    }

    onCreateNewProject() {
        this.createProjectService.showCreteProjectModal();
    }

    onOpenProject() {
        this.fileDirChooserModalService.showTesterumProjectChooserModal().subscribe((selectedPathAsString: string) => {
            this.projectService.openProject(selectedPathAsString).subscribe((project: Project) => {
                this.contextService.setCurrentProject(project);
                this.urlService.navigateToFeatures()
            });
        })
    }

    removeProject(project: Project) {
        this.areYouSureModalService.showAreYouSureModal(
            "Remove Project",
            `Are you sure you want to remove project <br/><b>${project.name}</b><br/>  from Recent Project list?`
        ).subscribe((action: AreYouSureModalEnum) => {
            if (action == AreYouSureModalEnum.OK) {
                this.projectService.deleteProject(project.path).subscribe( value => {
                    ArrayUtil.removeElementFromArray(this.recentProjects, project);
                });            }
        });
    }
}
