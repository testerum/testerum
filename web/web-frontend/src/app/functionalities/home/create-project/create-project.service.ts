import {ComponentRef, Injectable} from "@angular/core";
import {StepDef} from "../../../model/step-def.model";
import {Observable, Subject} from "rxjs";
import {AppComponent} from "../../../app.component";
import {CreateProjectComponent} from "./create-project.component";
import {Project} from "../../../model/home/project.model";

@Injectable()
export class CreateProjectService {

    private modalComponentRef: ComponentRef<CreateProjectComponent>;
    private modalComponent: CreateProjectComponent;
    private modalSubject: Subject<Project>;

    showCreteProjectModal(): Observable<Project> {

        const factory = AppComponent.componentFactoryResolver.resolveComponentFactory(CreateProjectComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        modalComponentRef.instance.createProjectService = this;

        this.modalComponentRef = modalComponentRef;
        this.modalComponent = modalComponentRef.instance;
        this.modalSubject = new Subject<Project>();

        return this.modalSubject.asObservable();
    }

    onCreateProjectAction(project: Project) {
        this.modalSubject.next(project);
    }

    clearModal() {
        this.modalSubject.complete();
        this.modalComponentRef.destroy();

        this.modalComponentRef = null;
        this.modalComponent = null;
        this.modalSubject = null;
    }
}
