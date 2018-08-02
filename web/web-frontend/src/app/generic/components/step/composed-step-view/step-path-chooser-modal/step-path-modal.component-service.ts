import {EventEmitter, Injectable} from "@angular/core";
import {StepPathContainerModel} from "./model/step-path-container.model";

@Injectable()
export class StepPathModalComponentService {
    selectedStepPathContainer: StepPathContainerModel;
    selectedNodeEmitter: EventEmitter<StepPathContainerModel> = new EventEmitter<StepPathContainerModel>();

    constructor() {
        this.selectedNodeEmitter.subscribe(selectedNode => {
            this.selectedStepPathContainer = selectedNode;
        })
    }

    triggerNodeSelected(selectedNode: StepPathContainerModel) {
        this.selectedNodeEmitter.next(selectedNode);
    }
}
