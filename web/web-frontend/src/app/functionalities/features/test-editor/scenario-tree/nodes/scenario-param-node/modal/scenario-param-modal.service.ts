import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {ScenarioParamModalComponent} from "./scenario-param-modal.component";
import {ScenarioParam} from "../../../../../../../model/test/scenario/param/scenario-param.model";
import {AppComponent} from "../../../../../../../app.component";
import {Scenario} from "../../../../../../../model/test/scenario/scenario.model";

@Injectable()
export class ScenarioParamModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    showEditScenarioParamModal(scenarioParam: ScenarioParam, allScenarios: Scenario[]): Observable<ScenarioParam|null> {
        let modalSubject = new Subject<ScenarioParam|null>();

        const factory = this.componentFactoryResolver.resolveComponentFactory(ScenarioParamModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: ScenarioParamModalComponent = modalComponentRef.instance;

        if (scenarioParam) {
            modalInstance.oldScenarioParam = scenarioParam;
            modalInstance.newScenarioParam = scenarioParam.clone();
        } else {
            modalInstance.newScenarioParam = new ScenarioParam();
        }
        modalInstance.allScenarios = allScenarios;

        modalInstance.modalComponentRef = modalComponentRef;
        modalInstance.modalSubject = modalSubject;

        return modalSubject.asObservable();
    }
}
