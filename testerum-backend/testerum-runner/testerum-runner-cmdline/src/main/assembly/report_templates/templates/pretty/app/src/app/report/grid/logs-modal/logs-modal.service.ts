import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {LogsModalComponent} from "./logs-modal.component";
import {AppComponent} from "../../../app.component";
import {ReportLog} from "../../../../../../../../common/testerum-model/report-model/model/report/report-log";

@Injectable()
export class LogsModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    showLogsModal(logs: Array<ReportLog>): Observable<void> {
        let modalSubject = new Subject<void>();

        const factory = this.componentFactoryResolver.resolveComponentFactory(LogsModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: LogsModalComponent = modalComponentRef.instance;

        modalInstance.logs = logs; //TODO Ionut: print nice the error logs

        modalInstance.modalComponentRef = modalComponentRef;
        modalInstance.modalSubject = modalSubject;

        modalComponentRef.changeDetectorRef.detectChanges();

        return modalSubject.asObservable();
    }
}
