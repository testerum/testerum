import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {Project} from "../../../model/home/project.model";
import {SelectProjectModalComponent} from "./select-project-modal.component";
import {AppComponent} from "../../../app.component";

@Injectable()
export class SelectProjectModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    showModal(projects: Array<Project>): Observable<Project> {
        let modalSubject = new Subject<Project>();

        const factory = this.componentFactoryResolver.resolveComponentFactory(SelectProjectModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: SelectProjectModalComponent = modalComponentRef.instance;

        modalInstance.projects = projects;

        modalInstance.modalComponentRef = modalComponentRef;
        modalInstance.modalSubject = modalSubject;

        return modalSubject.asObservable();
    }
}
