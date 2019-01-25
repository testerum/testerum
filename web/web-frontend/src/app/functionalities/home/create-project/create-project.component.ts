import {AfterViewInit, Component, ViewChild, ViewEncapsulation} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {CreateProjectService} from "./create-project.service";
import {Project} from "../../../model/home/project.model";

@Component({
    moduleId: module.id,
    selector: 'create-project',
    templateUrl: 'create-project.component.html',
    styleUrls: ['create-project.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class CreateProjectComponent implements AfterViewInit {

    name: string;
    path: string;

    @ViewChild("modal") modal:ModalDirective;

    createProjectService: CreateProjectService;
    constructor() {
    }

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.createProjectService.clearModal()
        })
    }

    onCancelAction() {
        this.modal.hide()
    }

    onCreateProjectAction() {
        this.createProjectService.onCreateProjectAction(new Project(this.name, this.path));
        this.modal.hide()
    }
}
