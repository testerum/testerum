import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {ParamStepPatternPart} from "../../../../model/text/parts/param-step-pattern-part.model";
import {ArgModalComponent} from "./arg-modal.component";
import {ArgModalEnum} from "./enum/arg-modal.enum";
import {AppComponent} from "../../../../app.component";
import {Arg} from "../../../../model/arg/arg.model";

@Injectable()
export class ArgModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    showAreYouSureModal(arg: Arg, stepParameter: ParamStepPatternPart): Observable<ArgModalEnum> {
        let modalSubject = new Subject<ArgModalEnum>();

        const factory = this.componentFactoryResolver.resolveComponentFactory(ArgModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: ArgModalComponent = modalComponentRef.instance;

        modalInstance.arg = arg;
        modalInstance.stepParameter = stepParameter;

        modalInstance.modalComponentRef = modalComponentRef;
        modalInstance.modalSubject = modalSubject;

        return modalSubject.asObservable();
    }
}
