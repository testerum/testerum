import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {AppComponent} from "../../../app.component";
import {ProjectReloadModalComponent} from "./project-reload-modal.component";
import {ContextService} from "../../../service/context.service";

@Injectable()
export class ProjectReloadModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private contextService: ContextService) {
    }

    showProjectReloadModal(reloadedProjectPath: string): Observable<void> {
        if (!this.contextService.isProjectSelected()) return null;
        if (this.contextService.getCurrentProject().path != reloadedProjectPath) return null;

        let modalSubject = new Subject<void>();

        const factory = this.componentFactoryResolver.resolveComponentFactory(ProjectReloadModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: ProjectReloadModalComponent = modalComponentRef.instance;

        modalInstance.modalComponentRef = modalComponentRef;
        modalInstance.modalSubject = modalSubject;

        return modalSubject.asObservable();
    }
}
