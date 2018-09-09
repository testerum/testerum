// import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
// import {OldManualTestStepStatus} from "../../../model/enums/manual-test-step-status.enum";
//
// @Component({
//     selector: 'step-status',
//     templateUrl: 'step-status.component.html',
//     styleUrls: ['step-status.component.scss']
// })
//
// export class StepStatusComponent implements OnInit {
//
//     ManualTestStepStatus = OldManualTestStepStatus;
//
//     @Input() stepStatus: OldManualTestStepStatus;
//     @Input() readonly: boolean;
//     @Output() stepStatusChange = new EventEmitter<OldManualTestStepStatus>();
//
//     ngOnInit(): void {
//     }
//
//     changeStatus() {
//         if (this.readonly) {
//             return
//         }
//
//         if(this.stepStatus == OldManualTestStepStatus.PASSED) {
//             this.setStepStatus(OldManualTestStepStatus.FAILED);
//         } else {
//             this.setStepStatus(OldManualTestStepStatus.PASSED);
//         }
//     }
//     setStepStatus(stepStatus: OldManualTestStepStatus) {
//         this.stepStatus = stepStatus;
//         this.stepStatusChange.emit(this.stepStatus)
//     }
// }
