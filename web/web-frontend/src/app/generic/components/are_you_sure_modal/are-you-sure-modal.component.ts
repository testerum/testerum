import {Component, OnInit, ViewChild} from '@angular/core';
import {AreYouSureModalEnum} from "./are-you-sure-modal.enum";
import {AreYouSureModalListener} from "./are-you-sure-modal.listener";
import {ModalDirective} from "ngx-bootstrap";

@Component({
    moduleId: module.id,
    selector: 'are-you-sure-modal',
    templateUrl: 'are-you-sure-modal.component.html'
})
export class AreYouSureModalComponent {

    @ViewChild("areYouSureModal") areYouSureModal:ModalDirective;

    listener: AreYouSureModalListener;

    title:string;
    text:string;

    show(title:string, text:string, listener: AreYouSureModalListener) {
        this.title = title;
        this.text = text;
        this.listener = listener;

        this.areYouSureModal.show();
    }

    ok() {
        this.listener(AreYouSureModalEnum.OK);
        this.areYouSureModal.hide();
    }

    cancel() {
        this.listener(AreYouSureModalEnum.CANCEL);
        this.areYouSureModal.hide();
    }
}
