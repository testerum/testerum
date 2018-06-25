
import {Injectable} from "@angular/core";
import {StepOrderChangedListener} from "./step-order-changed-listeher.interface";
import {TestModel} from "../../../../model/test/test.model";
import {StepCall} from "../../../../model/step-call.model";

@Injectable()
export class StepListService {

    stepOrderChangedListeners:Array<StepOrderChangedListener> = [];

    testModel:TestModel;

    public addStepOrderChangedListener(orderStepChangedListener:StepOrderChangedListener):void {
        this.stepOrderChangedListeners.push(orderStepChangedListener);
    }

    public removeStepOrderChangedListener(orderStepChangedListener:StepOrderChangedListener):void {
        let index = this.stepOrderChangedListeners.indexOf(orderStepChangedListener, 0);
        if (index > -1) {
            this.stepOrderChangedListeners.splice(index, 1);
        }
    }

    public triggerStepOrderChangedEvent():void {
        for (let listener of this.stepOrderChangedListeners) {
            listener.onStepOrderChangedEvent();
        }
    }

    public moveStepUp(stepCall:StepCall):void {
        let stepIndex = this.findStepIndex(stepCall);
        if(stepIndex > 0) {
            let previewsStep = this.testModel.stepCalls[stepIndex-1];
            this.testModel.stepCalls[stepIndex-1] = this.testModel.stepCalls[stepIndex];
            this.testModel.stepCalls[stepIndex] = previewsStep;
            this.triggerStepOrderChangedEvent();
        }
    }

    public moveStepDown(stepCall:StepCall):void {
        let stepIndex = this.findStepIndex(stepCall);
        if(0 <= stepIndex && stepIndex < this.testModel.stepCalls.length-1) {
            let nextStep = this.testModel.stepCalls[stepIndex+1];
            this.testModel.stepCalls[stepIndex+1] = this.testModel.stepCalls[stepIndex];
            this.testModel.stepCalls[stepIndex] = nextStep;
            this.triggerStepOrderChangedEvent();
        }
    }

    public removeStep(stepCall:StepCall):void {
        let stepIndex = this.findStepIndex(stepCall);
        this.testModel.stepCalls.splice(stepIndex, 1);
    }

    public getPreviewsStep(stepCall:StepCall):StepCall {
        let stepIndex = this.findStepIndex(stepCall);
        if(stepIndex < 1) {
            return null;
        }

        return this.testModel.stepCalls[stepIndex-1];
    }

    private findStepIndex(stepCall: StepCall):number {
        let stepIndex: number = -1;
        this.testModel.stepCalls.forEach((item, index) => {
            if(item.id == stepCall.id) {
                stepIndex = index;
            }
        });
        return stepIndex;
    }
}