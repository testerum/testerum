import {EventEmitter, Injectable} from "@angular/core";
import {ArgModalComponent} from "./arg-modal/arg-modal.component";

@Injectable()
export class StepCallTreeService {

    isEditMode: boolean;
    editModeEventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();
    stepCallOrderChangeEventEmitter: EventEmitter<void> = new EventEmitter<void>();

    argModal: ArgModalComponent;

    setEditMode(editMode: boolean) {
        this.editModeEventEmitter.emit(editMode);
        this.isEditMode = editMode
    }

    triggerStepCallOrderChangeEvent(): void {
        this.stepCallOrderChangeEventEmitter.emit();
    }
}
