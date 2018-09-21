import {AfterViewInit, Component, ComponentRef, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {Subject} from "rxjs";

@Component({
    moduleId: module.id,
    selector: 'info-modal',
    templateUrl: 'info-modal.component.html',
    styleUrls: ['info-modal.component.scss']
})
export class InfoModalComponent implements AfterViewInit {

    @ViewChild("infoModal") modal:ModalDirective;

    title:string;
    text:string;

    modalComponentRef: ComponentRef<InfoModalComponent>;
    modalSubject:Subject<void>;

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalSubject.complete();

            this.modalComponentRef.destroy();

            this.modalComponentRef = null;
            this.modalSubject = null;
        })
    }

    close() {
        this.modalSubject.next();
        this.modal.hide();
    }
}
