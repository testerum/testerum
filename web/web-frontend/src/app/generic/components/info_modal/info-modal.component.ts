import {Component, OnInit, ViewChild} from '@angular/core';
import {InfoModalListener} from "./info-modal.listener";
import {ModalDirective} from "ngx-bootstrap";

@Component({
    moduleId: module.id,
    selector: 'info-modal',
    templateUrl: 'info-modal.component.html'
})
export class InfoModalComponent {

    @ViewChild("infoModal") infoModal:ModalDirective;

    listener: InfoModalListener;
    payload: any;

    title:string;
    text:string;


    public show(title:string, text:string, listener: InfoModalListener, payload: any) {
        this.title = title;
        this.text = text;
        this.listener = listener;
        this.payload = payload;

        this.infoModal.show();
    }

    close() {
        if (this.listener) {
            this.listener.infoModalListener(this.payload);
        }
        this.infoModal.hide();
    }
}
