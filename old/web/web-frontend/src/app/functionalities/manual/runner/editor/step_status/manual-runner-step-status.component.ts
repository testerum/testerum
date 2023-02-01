import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ManualTestStepStatus} from "../../../plans/model/enums/manual-test-step-status.enum";

@Component({
    selector: 'manual-runner-step-status',
    templateUrl: 'manual-runner-step-status.component.html',
    styleUrls: ['manual-runner-step-status.component.scss']
})
export class ManualRunnerStepStatusComponent implements OnInit {

    ManualTestStepStatus = ManualTestStepStatus;

    @Input() stepStatus: ManualTestStepStatus;
    @Input() readonly: boolean;
    @Output() stepStatusChange = new EventEmitter<ManualTestStepStatus>();

    ngOnInit(): void {
    }

    changeStatus() {
        if (this.readonly) {
            return
        }

        if(this.stepStatus == ManualTestStepStatus.PASSED) {
            this.setStepStatus(ManualTestStepStatus.FAILED);
        } else {
            this.setStepStatus(ManualTestStepStatus.PASSED);
        }
    }
    setStepStatus(stepStatus: ManualTestStepStatus) {
        this.stepStatus = stepStatus;
        this.stepStatusChange.emit(this.stepStatus)
    }
}
