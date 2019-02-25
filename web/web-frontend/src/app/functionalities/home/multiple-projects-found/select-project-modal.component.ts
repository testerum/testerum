import {AfterViewInit, Component, ComponentRef, OnInit, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {Project} from "../../../model/home/project.model";
import {Observable, Subject} from "rxjs";

@Component({
  selector: 'select-project-modal',
  templateUrl: './select-project-modal.component.html',
  styleUrls: ['./select-project-modal.component.scss']
})
export class SelectProjectModalComponent implements AfterViewInit {

    @ViewChild("selectProjectModal") modal:ModalDirective;
    modalSubject:Subject<Project>;
    modalComponentRef: ComponentRef<SelectProjectModalComponent>;

    projects: Array<Project>;

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalSubject.complete();

            if (this.modalComponentRef) {
                this.modalComponentRef.destroy();
            }

            this.modalComponentRef = null;
            this.modalSubject = null;
        })
    }

    show(projects: Array<Project>): Observable<Project> {
        this.projects = projects;
        this.modalSubject = new Subject<Project>();

        return this.modalSubject.asObservable();
    }

    onProjectSelected(project: Project) {
        this.modalSubject.next(project);
        this.modal.hide();
    }
}
