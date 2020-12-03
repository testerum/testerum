import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {StepUsageDialogComponent} from "./step-usage-dialog.component";
import {StepsService} from "../../../../../service/steps.service";
import {CheckComposedStepDefUsageResponse} from "../../../../../model/step/operation/CheckComposedStepDefUsageResponse";
import {StepUsageDialogModeEnum} from "./model/step-usage-dialog-mode.enum";
import {ComposedStepDef} from "../../../../../model/composed-step-def.model";
import {Observable, Subject} from "rxjs";
import {StepUsageDialogResponseEnum} from "./model/step-usage-dialog-response.enum";
import {AppComponent} from "../../../../../app.component";
import {Path} from "../../../../../model/infrastructure/path/path.model";

@Injectable()
export class StepUsageDialogService {

    instance: StepUsageDialogComponent;

    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private stepsService: StepsService) {
    }

    show(mode: StepUsageDialogModeEnum,
         pathsForAffectedTests: Array<Path>,
         pathsForDirectAffectedSteps: Array<Path>,
         pathsForTransitiveAffectedSteps: Array<Path>): Observable<StepUsageDialogResponseEnum> {

        let modalSubject = new Subject<StepUsageDialogResponseEnum>();

        this.privateShow(
            mode,
            pathsForAffectedTests,
            pathsForDirectAffectedSteps,
            pathsForTransitiveAffectedSteps,
            modalSubject
        )
        return modalSubject.asObservable();
    }

    showUsingStepDef(composedStepDef: ComposedStepDef): Observable<StepUsageDialogResponseEnum> {
        let modalSubject = new Subject<StepUsageDialogResponseEnum>();

        this.stepsService.usageCheck(composedStepDef).subscribe((stepUsage: CheckComposedStepDefUsageResponse) => {

            this.privateShow(
                StepUsageDialogModeEnum.INFO_STEP,
                stepUsage.pathsForAffectedTests,
                stepUsage.pathsForDirectParentSteps,
                stepUsage.pathsForTransitiveParentSteps,
                modalSubject
            )
        });

        return modalSubject.asObservable();
    }

    private privateShow(mode: StepUsageDialogModeEnum,
                        pathsForAffectedTests: Array<Path>,
                        pathsForDirectAffectedSteps: Array<Path>,
                        pathsForTransitiveAffectedSteps: Array<Path>,
                        modalSubject: Subject<StepUsageDialogResponseEnum>) {

        if (this.instance && this.instance.modal.isShown) {
            return;
        }

        const factory = this.componentFactoryResolver.resolveComponentFactory(StepUsageDialogComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        this.instance = modalComponentRef.instance;

        this.instance.init(
            mode,
            pathsForAffectedTests,
            pathsForDirectAffectedSteps,
            pathsForTransitiveAffectedSteps
        );
        this.instance.modalComponentRef = modalComponentRef;
        this.instance.modalSubject = modalSubject;
    }
}
