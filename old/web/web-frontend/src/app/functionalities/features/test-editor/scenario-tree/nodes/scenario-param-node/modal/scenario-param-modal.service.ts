import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {ScenarioParamModalComponent} from "./scenario-param-modal.component";
import {ScenarioParam} from "../../../../../../../model/test/scenario/param/scenario-param.model";
import {AppComponent} from "../../../../../../../app.component";
import {Scenario} from "../../../../../../../model/test/scenario/scenario.model";
import {ScenarioParamChangeModel} from "./model/scenario-param-change.model";

@Injectable()
export class ScenarioParamModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    showEditScenarioParamModal(scenarioParam: ScenarioParam, allScenarios: Scenario[], currentScenario: Scenario): Observable<ScenarioParamChangeModel> {
        let modalSubject = new Subject<ScenarioParamChangeModel>();

        const factory = this.componentFactoryResolver.resolveComponentFactory(ScenarioParamModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: ScenarioParamModalComponent = modalComponentRef.instance;

        if (scenarioParam) {
            modalInstance.oldParam = scenarioParam;
            modalInstance.newParam = scenarioParam.clone();
        } else {
            modalInstance.newParam = new ScenarioParam();
        }
        modalInstance.allScenarios = allScenarios;
        modalInstance.currentScenario = currentScenario;

        modalInstance.modalComponentRef = modalComponentRef;
        modalInstance.modalSubject = modalSubject;

        return modalSubject.asObservable();
    }
}
