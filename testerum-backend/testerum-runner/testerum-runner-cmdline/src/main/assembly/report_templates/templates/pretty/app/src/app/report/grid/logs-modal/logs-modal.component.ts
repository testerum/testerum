import {AfterViewInit, Component, ComponentRef, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {Subject} from "rxjs";
import {ReportLog} from "../../../../../../../../common/testerum-model/model/report/report-log";
import {ExceptionDetail} from "../../../../../../../../common/testerum-model/model/exception/exception-detail";

@Component({
    selector: 'logs-modal',
    templateUrl: 'logs-modal.component.html',
    styleUrls: ['logs-modal.component.scss']
})
export class LogsModalComponent implements AfterViewInit {

    @ViewChild("infoModal") modal:ModalDirective;

    logs: Array<ReportLog>;
    exceptionDetail: ExceptionDetail;

    shouldWrapLogs: boolean = false;

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

    onToggleWrap() {
        this.shouldWrapLogs = !this.shouldWrapLogs;
    }
}
