import {ComponentRef, Injectable} from "@angular/core";
import {AppComponent} from "../../../app.component";
import {CreateProjectComponent} from "./create-project.component";
import {Project} from "../../../model/home/project.model";
import {ContextService} from "../../../service/context.service";
import {ProjectService} from "../../../service/project.service";
import {CreateProjectRequest} from "../../../model/home/create-project-request.model";
import {UrlService} from "../../../service/url.service";

@Injectable()
export class CreateProjectService {

    private modalComponentRef: ComponentRef<CreateProjectComponent>;
    private modalComponent: CreateProjectComponent;


    constructor(private projectService: ProjectService,
                private contextService: ContextService,
                private urlService: UrlService) {
    }

    showCreteProjectModal() {

        const factory = AppComponent.componentFactoryResolver.resolveComponentFactory(CreateProjectComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        modalComponentRef.instance.createProjectService = this;

        this.modalComponentRef = modalComponentRef;
        this.modalComponent = modalComponentRef.instance;
    }

    onCreateProjectAction(createProjectRequest: CreateProjectRequest) {
        this.projectService.createProject(createProjectRequest).subscribe((project: Project) => {
            this.contextService.setCurrentProject(project);
            this.urlService.navigateToFeatures();
        })
    }

    clearModal() {
        this.modalComponentRef.destroy();

        this.modalComponentRef = null;
        this.modalComponent = null;
    }
}
