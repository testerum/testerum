
import {Component, OnInit, Input, EventEmitter, Output, OnDestroy} from '@angular/core';
import {StepListService} from "../step-list.service";
import {TestModel} from "../../../../../model/test/test.model";
import {StepOrderChangedListener} from "../step-order-changed-listeher.interface";
import {StringUtils} from "../../../../../utils/string-utils.util";
import {StepPhaseEnum} from "../../../../../model/enums/step-phase.enum";
import {StepCall} from "../../../../../model/step-call.model";
import {AfterPageSaveListener} from "../../../../../event-bus/listeners/after-page-save.listener";
import {ApplicationEventBus} from "../../../../../event-bus/application.eventbus";

@Component({
    moduleId: module.id,
    selector: 'step',
    templateUrl: 'step.component.html',
    styleUrls:["step.component.css"]
})
export class StepComponent implements OnInit, OnDestroy, StepOrderChangedListener, AfterPageSaveListener {

    @Input() stepCall:StepCall;
    @Input() isEditMode: boolean;

    showParameters: boolean = true;
    isFirstStep:boolean = false;
    isLastStep:boolean = false;
    phaseText:string;

    constructor(private stepListService:StepListService,
                private applicationEventBus:ApplicationEventBus) {
    }

    ngOnInit(): void {
        this.applicationEventBus.addAfterPageSaveListener(this);
        this.initPropertiesThatDependsOnStepOrder();
        this.stepListService.addStepOrderChangedListener(this);
        if(!this.isEditMode) this.showParameters = false
    }

    ngOnDestroy(): void {
        this.applicationEventBus.removeAfterPageSaveListener(this);
    }

    private initPropertiesThatDependsOnStepOrder() {
        let testModel: TestModel = this.stepListService.testModel;

        if (testModel.stepCalls.length != 0) {
            this.initIfIsTheFirstNode(testModel);
            this.initIfIsTheLastNode(testModel);
        }

        let previewsStep:StepCall = this.stepListService.getPreviewsStep(this.stepCall);
        if(previewsStep && previewsStep.stepDef.phase === this.stepCall.stepDef.phase) {
            this.phaseText = "And";
        } else {
            this.phaseText = StringUtils.toCamelCase(StepPhaseEnum[this.stepCall.stepDef.phase]);
        }
    }

    private initIfIsTheLastNode(testModel: TestModel) {
        let lastModelStep = testModel.stepCalls[testModel.stepCalls.length - 1];

        this.isLastStep = lastModelStep.id == this.stepCall.id;
    }

    private initIfIsTheFirstNode(testModel: TestModel) {
        let firstModelStep = testModel.stepCalls[0];
        this.isFirstStep = firstModelStep.id == this.stepCall.id;
    }

    toggleParameters() {
        this.showParameters = !this.showParameters;
    }

    onStepOrderChangedEvent(): void {
        this.initPropertiesThatDependsOnStepOrder()
    }

    afterPageSave() {
        this.showParameters = false;
    }

    moveStepUp(): void {
        this.stepListService.moveStepUp(this.stepCall);
    }

    moveStepDown(): void {
        this.stepListService.moveStepDown(this.stepCall);
    }

    removeStep(): void {
        this.stepListService.removeStep(this.stepCall);
    }
}
