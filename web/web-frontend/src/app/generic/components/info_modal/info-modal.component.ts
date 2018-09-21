import {AfterViewInit, Component, ComponentRef, OnInit, ViewChild} from '@angular/core';
import {InfoModalListener} from "./info-modal.listener";
import {ModalDirective} from "ngx-bootstrap";
import {AreYouSureModalEnum} from "../are_you_sure_modal/are-you-sure-modal.enum";
import {Subject} from "rxjs";

@Component({
    moduleId: module.id,
    selector: 'info-modal',
    templateUrl: 'info-modal.component.html'
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
