import {ChangeDetectorRef, ComponentFactoryResolver, Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {AppComponent} from "../../../app.component";
import {ErrorFeedbackModalComponent} from "./error-feedback-modal.component";
import {MyError} from "../../../model/exception/my-error.model";

@Injectable()
export class ErrorFeedbackModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    showInfoModal(error: MyError): Observable<boolean> {
        let modalSubject = new Subject<boolean>();

        const factory = this.componentFactoryResolver.resolveComponentFactory(ErrorFeedbackModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: ErrorFeedbackModalComponent = modalComponentRef.instance;

        modalInstance.error = error;

        modalInstance.modalComponentRef = modalComponentRef;
        modalInstance.modalSubject = modalSubject;

        modalComponentRef.changeDetectorRef.detectChanges();

        return modalSubject.asObservable();
    }
}
