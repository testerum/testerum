import {EventEmitter} from "@angular/core";

export class ReportPieModel {
    totalTests: number = 0;
    passed: number = 0;
    failed: number = 0;
    disabled: number = 0;
    undefined: number = 0;
    skipped: number = 0;

}
