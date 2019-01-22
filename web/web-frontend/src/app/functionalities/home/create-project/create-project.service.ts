import {ComponentRef, Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {AppComponent} from "../../../app.component";
import {CreateProjectComponent} from "./create-project.component";
import {Project} from "../../../model/home/project.model";
import {HomeService} from "../../../service/home.service";
import {ContextService} from "../../../service/context.service";
import {ProjectService} from "../../../service/project.service";

@Injectable()
export class CreateProjectService {

    private modalComponentRef: ComponentRef<CreateProjectComponent>;
    private modalComponent: CreateProjectComponent;


    constructor(private projectService: ProjectService,
                private contextService: ContextService) {
    }

    showCreteProjectModal() {

        const factory = AppComponent.componentFactoryResolver.resolveComponentFactory(CreateProjectComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        modalComponentRef.instance.createProjectService = this;

        this.modalComponentRef = modalComponentRef;
        this.modalComponent = modalComponentRef.instance;
    }

    onCreateProjectAction(project: Project) {
        this.projectService.createProject(project).subscribe((project: Project) => {
            this.contextService.setProjectName(project.name)
        })
    }

    clearModal() {
        this.modalComponentRef.destroy();

        this.modalComponentRef = null;
        this.modalComponent = null;
    }
}
