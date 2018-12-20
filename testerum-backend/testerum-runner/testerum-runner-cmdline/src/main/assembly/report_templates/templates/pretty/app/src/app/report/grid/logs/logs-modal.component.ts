import {AfterViewInit, Component, ComponentRef, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {Subject} from "rxjs";

@Component({
    selector: 'logs-modal',
    templateUrl: 'logs-modal.component.html',
    styleUrls: ['logs-modal.component.scss']
})
export class LogsModalComponent implements AfterViewInit {

    @ViewChild("infoModal") modal:ModalDirective;

    title:string;
    text:string;

    modalComponentRef: ComponentRef<LogsModalComponent>;
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
