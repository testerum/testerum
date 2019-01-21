import {AfterViewInit, ChangeDetectorRef, Component, ComponentRef, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {Subject} from "rxjs";
import {ReportLog} from "../../../../../../../../common/testerum-model/report-model/model/report/report-log";

@Component({
    selector: 'logs-modal',
    templateUrl: 'logs-modal.component.html',
    styleUrls: ['logs-modal.component.scss']
})
export class LogsModalComponent implements AfterViewInit {

    @ViewChild("infoModal") modal:ModalDirective;

    logs: Array<ReportLog>;

    shouldWrapLogs: boolean = false;

    modalComponentRef: ComponentRef<LogsModalComponent>;
    modalSubject:Subject<void>;


    constructor(private cd: ChangeDetectorRef) {
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

    refresh() {
        if (!this.cd['destroyed']) { //without this the folowing error will appear: "ERROR Error: ViewDestroyedError: Attempt to use a destroyed view: detectChanges"
            this.cd.detectChanges();
        }
    }

    close() {
        this.modalSubject.next();
        this.modal.hide();
    }

    onToggleWrap() {
        this.shouldWrapLogs = !this.shouldWrapLogs;
        this.refresh();
    }
}
