import {AfterViewInit, Component, ComponentRef, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {interval, Subscription} from "rxjs";
import {map} from "rxjs/operators";

@Component({
  selector: 'server-not-available',
  templateUrl: './select-project-modal.component.html',
  styleUrls: ['./select-project-modal.component.scss']
})
export class SelectProjectModalComponent implements AfterViewInit {

    @ViewChild("selectProjectModal") modal:ModalDirective;

    modalComponentRef: ComponentRef<SelectProjectModalComponent>;

    constructor() {
    }

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalComponentRef.destroy();

            this.modalComponentRef = null;
        });

    }
}
