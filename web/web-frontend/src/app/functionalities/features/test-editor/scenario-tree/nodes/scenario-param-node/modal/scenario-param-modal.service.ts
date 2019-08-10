import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {ScenarioParamModalComponent} from "./scenario-param-modal.component";
import {ScenarioParam} from "../../../../../../../model/test/scenario/param/scenario-param.model";
import {AppComponent} from "../../../../../../../app.component";

@Injectable()
export class ScenarioParamModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    showEditScenarioParamModal(scenarioParam: ScenarioParam): Observable<ScenarioParam|null> {
        let modalSubject = new Subject<ScenarioParam|null>();

        const factory = this.componentFactoryResolver.resolveComponentFactory(ScenarioParamModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: ScenarioParamModalComponent = modalComponentRef.instance;

        modalInstance.oldScenarioParam = scenarioParam;
        modalInstance.newScenarioParam = scenarioParam.clone();

        modalInstance.modalComponentRef = modalComponentRef;
        modalInstance.modalSubject = modalSubject;

        return modalSubject.asObservable();
    }
}
