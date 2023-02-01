import {AfterViewInit, Component, ViewChild, ViewEncapsulation} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {CreateProjectService} from "./create-project.service";
import {CreateProjectRequest} from "../../../model/home/create-project-request.model";

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

    @ViewChild("modal", { static: true }) modal:ModalDirective;

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
        this.createProjectService.onCreateProjectAction(
            CreateProjectRequest.create(this.path, this.name)
        );
        this.modal.hide()
    }
}
