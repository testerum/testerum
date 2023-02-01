import {AfterViewInit, Component, ComponentRef, OnInit, ViewChild} from '@angular/core';
import {AreYouSureModalEnum} from "./are-you-sure-modal.enum";
import {ModalDirective} from "ngx-bootstrap";
import {Subject} from "rxjs";

@Component({
    moduleId: module.id,
    selector: 'are-you-sure-modal',
    templateUrl: 'are-you-sure-modal.component.html',
    styleUrls: ['are-you-sure-modal.component.scss']
})
export class AreYouSureModalComponent implements AfterViewInit {

    @ViewChild("areYouSureModal", { static: true }) modal:ModalDirective;

    title:string;
    text:string;

    modalComponentRef: ComponentRef<AreYouSureModalComponent>;
    modalSubject:Subject<AreYouSureModalEnum>;

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalSubject.complete();

            this.modalComponentRef.destroy();

            this.modalComponentRef = null;
            this.modalSubject = null;
        })
    }

    ok() {
        this.modalSubject.next(AreYouSureModalEnum.OK);
        this.modal.hide();
    }

    cancel() {
        this.modalSubject.next(AreYouSureModalEnum.CANCEL);
        this.modal.hide();
    }

    onKeyDown(event: KeyboardEvent) {
        if (event.code == "Escape") {
            this.cancel();
        }
    }
}
