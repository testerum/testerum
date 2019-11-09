import {Component, OnDestroy, OnInit, ViewEncapsulation} from '@angular/core';
import {Project} from "../../model/home/project.model";
import {HomeService} from "../../service/home.service";
import {Subscription} from "rxjs";
import {ContextService} from "../../service/context.service";
import {Home} from "../../model/home/home.model";
import {ArrayUtil} from "../../utils/array.util";
import {CreateProjectService} from "./create-project/create-project.service";
import {FileChooserModalService} from "../../generic/components/form/file_chooser/dialog/file-chooser-modal.service";
import {ProjectService} from "../../service/project.service";
import {UrlService} from "../../service/url.service";
import {AreYouSureModalService} from "../../generic/components/are_you_sure_modal/are-you-sure-modal.service";
import {AreYouSureModalEnum} from "../../generic/components/are_you_sure_modal/are-you-sure-modal.enum";
import {DemoService} from "../../service/demo.service";

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
                private fileDirChooserModalService: FileChooserModalService,
                private areYouSureModalService: AreYouSureModalService,
                private urlService: UrlService,
                private demoService: DemoService) {
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
        this.projectService.openProject(project.path).subscribe((project: Project) => { //we call open project just to register the open date
            this.contextService.setCurrentProject(project);
            this.urlService.navigateToProject();
        });
    }

    onCreateNewProject() {
        this.createProjectService.showCreteProjectModal();
    }

    onOpenProject() {
        this.fileDirChooserModalService.showTesterumProjectChooserModal().subscribe((selectedPathAsString: string) => {
            this.projectService.openProject(selectedPathAsString).subscribe((project: Project) => {
                this.contextService.setCurrentProject(project);
                this.urlService.navigateToProject()
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

    onOpenDemoProject() {
        this.demoService.openDemoProject().subscribe((project: Project) => {
            this.contextService.setCurrentProject(project);
            this.urlService.navigateToProject();
        });
    }

    navigateToVideos() {
        this.urlService.navigateToVideos('https://testerum.com/videos/');
    }

    navigateToDocumentation() {
        this.urlService.navigateToDocumentation('https://testerum.com/documentation/');
    }

    getDemoDescription(): string {
        return "This demo will start an embedded application on localhost:9966\n" +
            "A free port at this address is required, otherwise\n" +
            "the demo-app will not start properly and the tests will not pass.\n" +
            "Open this sample project and you will learn " +
            "step-by-step how to use Testerum."
    }
}
