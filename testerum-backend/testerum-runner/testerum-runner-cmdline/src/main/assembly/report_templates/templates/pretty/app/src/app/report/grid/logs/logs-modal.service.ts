import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {LogsModalComponent} from "./logs-modal.component";
import {AppComponent} from "../../../app.component";

@Injectable()
export class LogsModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    showLogsModal(title:string, text:string,): Observable<void> {
        let modalSubject = new Subject<void>();

        const factory = this.componentFactoryResolver.resolveComponentFactory(LogsModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: LogsModalComponent = modalComponentRef.instance;

        modalInstance.title = title;
        modalInstance.text = text;

        modalInstance.modalComponentRef = modalComponentRef;
        modalInstance.modalSubject = modalSubject;

        return modalSubject.asObservable();
    }
}
