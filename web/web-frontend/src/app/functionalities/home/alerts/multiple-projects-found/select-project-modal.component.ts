import {AfterViewInit, Component, ComponentRef, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {Project} from "../../../../model/home/project.model";
import {Subject} from "rxjs";

@Component({
  selector: 'server-not-available',
  templateUrl: './select-project-modal.component.html',
  styleUrls: ['./select-project-modal.component.scss']
})
export class SelectProjectModalComponent implements AfterViewInit {

    @ViewChild("selectProjectModal") modal:ModalDirective;
    modalComponentRef: ComponentRef<SelectProjectModalComponent>;
    modalSubject:Subject<Project>;

    projects: Array<Project>;

    constructor() {
    }

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalComponentRef.destroy();
            this.modalSubject.complete();

            this.modalComponentRef = null;
            this.modalSubject = null;
        });
    }

    onProjectSelected(project: Project) {
        this.modalSubject.next(project);
        this.modal.hide();
    }
}
