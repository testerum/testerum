import {EventEmitter} from "@angular/core";
import {ExecutionStatusEnum} from "../../../../../model/test/event/enums/execution-status.enum";

export class ExecutionPieModel {
    private _totalTests: number = 0;
    private _waitingToExecute: number = 0;
    private _passed: number = 0;
    private _failed: number = 0;
    private _disabled: number = 0;
    private _error: number = 0;
    private _undefined: number = 0;
    private _skipped: number = 0;

    changeEventEmitter = new EventEmitter<void>();

    reset() {
        this._totalTests = 0;
        this._waitingToExecute = 0;
        this._passed = 0;
        this._error = 0;
        this._failed = 0;
        this._undefined = 0;
        this._skipped = 0;
        this.changeEventEmitter.emit();
    }

    setWaitingToExecute(count: number) {
        this._waitingToExecute = count;
        this.changeEventEmitter.emit();
    }
    incrementPassed() {
        this._passed ++;
        this._waitingToExecute --;
        this.changeEventEmitter.emit();
    }
    incrementFailed() {
        this._failed ++;
        this._waitingToExecute --;
        this.changeEventEmitter.emit();
    }
    incrementDisabled() {
        this._disabled ++;
        this._waitingToExecute --;
        this.changeEventEmitter.emit();
    }
    incrementError() {
        this._error ++;
        this._waitingToExecute --;
        this.changeEventEmitter.emit();
    }
    incrementUndefined() {
        this._undefined ++;
        this._waitingToExecute --;
        this.changeEventEmitter.emit();
    }
    incrementSkipped() {
        this._skipped ++;
        this._waitingToExecute --;
        this.changeEventEmitter.emit();
    }

    set totalTests(value: number) {
        this._totalTests = value;
        this.changeEventEmitter.emit();
    }

    set waitingToExecute(value: number) {
        this._waitingToExecute = value;
        this.changeEventEmitter.emit();
    }

    set passed(value: number) {
        this._passed = value;
        this.changeEventEmitter.emit();
    }

    set failed(value: number) {
        this._failed = value;
        this.changeEventEmitter.emit();
    }

    set error(value: number) {
        this._error = value;
        this.changeEventEmitter.emit();
    }

    set undefined(value: number) {
        this._undefined = value;
        this.changeEventEmitter.emit();
    }

    set skipped(value: number) {
        this._skipped = value;
        this.changeEventEmitter.emit();
    }

    get totalTests(): number {
        return this._totalTests;
    }

    get waitingToExecute(): number {
        return this._waitingToExecute;
    }

    get passed(): number {
        return this._passed;
    }

    get failed(): number {
        return this._failed;
    }

    get disabled(): number {
        return this._disabled;
    }

    get error(): number {
        return this._error;
    }

    get undefined(): number {
        return this._undefined;
    }
    get skipped(): number {
        return this._undefined;
    }

    incrementBasedOnState(status: ExecutionStatusEnum) {
        switch (status) {
            case ExecutionStatusEnum.WAITING: this._waitingToExecute++; break;
            case ExecutionStatusEnum.PASSED: this._passed++; break;
            case ExecutionStatusEnum.FAILED: this._failed++; break;
            case ExecutionStatusEnum.DISABLED: this._disabled++; break;
            case ExecutionStatusEnum.ERROR: this._error++; break;
            case ExecutionStatusEnum.UNDEFINED: this._undefined++; break;
            case ExecutionStatusEnum.SKIPPED: this._skipped++; break;
        }
        this.changeEventEmitter.emit();
    }
}
