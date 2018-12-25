import {AfterViewInit, Component, ComponentRef, OnInit, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {About} from "./model/about.model";

@Component({
  selector: 'about.component',
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.scss']
})
export class AboutComponent implements OnInit, AfterViewInit {

    model: About;

    @ViewChild("aboutModal") modal:ModalDirective;
    modalComponentRef: ComponentRef<AboutComponent>;

    constructor() { }

    ngOnInit(): void {
        let about = new About(); //Must be replaced later
        about.name = "marius.gradinaru";
        about.license = "";
        about.version = "";
        about.expirationDate = new Date();
        this.model = about;
    }

    ngAfterViewInit(): void {

        this.modal.show();
        this.modal.onHidden.subscribe(event => {

            this.modalComponentRef.destroy();

            this.modalComponentRef = null;
        })
    }

    cancel() {
        this.modal.hide();
    }

    ok() {
        this.modal.hide();
    }

}
