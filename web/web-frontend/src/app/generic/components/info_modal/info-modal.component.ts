import {AfterViewInit, Component, ComponentRef, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {Observable, Subject} from "rxjs";

@Component({
    moduleId: module.id,
    selector: 'info-modal',
    templateUrl: 'info-modal.component.html',
    styleUrls: ['info-modal.component.scss']
})
export class InfoModalComponent implements AfterViewInit {

    @ViewChild("infoModal", { static: true }) modal:ModalDirective;

    title:string;
    text:string;
    suggestions: Array<string> = [];

    modalComponentRef: ComponentRef<InfoModalComponent>;
    modalSubject:Subject<void>;

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalSubject.complete();

            if (this.modalComponentRef) {
                this.modalComponentRef.destroy();
            }

            this.modalComponentRef = null;
            this.modalSubject = null;
        })
    }

    show(title:string, text:string, suggestions: Array<string> = []): Observable<void> {
        this.modalSubject = new Subject<void>();

        this.title = title;
        this.text = text;
        this.suggestions = suggestions;

        return this.modalSubject.asObservable();
    }

    close() {
        this.modalSubject.next();
        this.modal.hide();
    }

    onKeyDown(event: KeyboardEvent) {
        if (event.code == "Escape") {
            this.close();
        }
    }
}
