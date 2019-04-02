import {AfterViewInit, Component, ComponentRef, OnInit, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {Subject} from "rxjs";
import {UrlService} from "../../../service/url.service";

@Component({
    moduleId: module.id,
    selector: 'project-reload-modal',
    templateUrl: 'project-reload-modal.component.html',
    styleUrls: ['project-reload-modal.component.scss']
})
export class ProjectReloadModalComponent implements AfterViewInit {

    @ViewChild("projectReloadModal") modal:ModalDirective;

    modalComponentRef: ComponentRef<ProjectReloadModalComponent>;
    modalSubject:Subject<void>;


    constructor(private urlService: UrlService) {
    }

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalSubject.complete();

            this.modalComponentRef.destroy();

            this.modalComponentRef = null;
            this.modalSubject = null;
        })
    }

    reload() {
        this.modalSubject.next();
        this.modal.hide();
        this.urlService.refreshPage();
    }

    continue() {
        this.modalSubject.next();
        this.modal.hide();
    }

    onKeyDown(event: KeyboardEvent) {
        if (event.code == "Escape") {
            this.continue();
        }
    }
}
