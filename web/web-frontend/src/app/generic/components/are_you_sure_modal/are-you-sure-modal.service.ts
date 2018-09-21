import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {AppComponent} from "../../../app.component";
import {AreYouSureModalEnum} from "./are-you-sure-modal.enum";
import {AreYouSureModalComponent} from "./are-you-sure-modal.component";

@Injectable()
export class AreYouSureModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    showAreYouSureModal(title:string, text:string): Observable<AreYouSureModalEnum> {
        let modalSubject = new Subject<AreYouSureModalEnum>();

        const factory = this.componentFactoryResolver.resolveComponentFactory(AreYouSureModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: AreYouSureModalComponent = modalComponentRef.instance;

        modalInstance.title = title;
        modalInstance.text = text;

        modalInstance.modalComponentRef = modalComponentRef;
        modalInstance.modalSubject = modalSubject;

        return modalSubject.asObservable();
    }
}
