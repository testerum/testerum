import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {AppComponent} from "../../../app.component";
import {InfoModalComponent} from "./info-modal.component";

@Injectable()
export class InfoModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    showInfoModal(title:string, text:string,): Observable<void> {
        let modalSubject = new Subject<void>();

        const factory = this.componentFactoryResolver.resolveComponentFactory(InfoModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: InfoModalComponent = modalComponentRef.instance;

        modalInstance.title = title;
        modalInstance.text = text;

        modalInstance.modalComponentRef = modalComponentRef;
        modalInstance.modalSubject = modalSubject;

        return modalSubject.asObservable();
    }
}
