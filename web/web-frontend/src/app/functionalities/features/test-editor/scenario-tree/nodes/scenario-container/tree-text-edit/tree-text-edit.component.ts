import {
    Component,
    ElementRef, EventEmitter,
    forwardRef,
    Input,
    OnChanges, OnDestroy,
    OnInit,
    Output,
    SimpleChanges,
    ViewChild
} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from "@angular/forms";
import {FocusDirective} from "../../../../../../../generic/directives/focus.directive";
import {ScenarioTreeComponentService} from "../../../scenario-tree.component-service";
import {TestsRunnerService} from "../../../../../tests-runner/tests-runner.service";

@Component({
    selector: 'tree-text-edit',
    templateUrl: './tree-text-edit.component.html',
    styleUrls: ['./tree-text-edit.component.scss'],
    providers: [
        { provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => TreeTextEditComponent), multi: true}
    ]
})
export class TreeTextEditComponent implements ControlValueAccessor, OnInit, OnDestroy {

    text: string;
    @Input() isTextEditMode: boolean = false;
    @Output() onTextEditModeChanged = new EventEmitter<boolean>();

    @ViewChild('input', { static: false }) inputElementRef: ElementRef;

    private propagateChange: Function;

    constructor(private scenarioTreeComponentService: ScenarioTreeComponentService) {
    }

    ngOnInit(): void {
        this.scenarioTreeComponentService.editModeEventEmitter.subscribe( (isEditMode: boolean) => {
            if (isEditMode == false) {
                this.setTextEditMode(false);
            }
        });
    }

    ngOnDestroy(): void {
    }

    writeValue(obj: any): void {
        this.text = obj;
    }

    registerOnChange(fn: any): void {
        this.propagateChange = fn;
    }

    registerOnTouched(fn: any): void {}
    setDisabledState(isDisabled: boolean): void {}

    onTextChange(text: string) {
        this.text = text;
        this.propagateChange(this.text);
    }

    setTextEditMode(isTextEditMode: boolean) {
        if (this.isTextEditMode != isTextEditMode) {
            this.isTextEditMode = isTextEditMode;
            this.onTextEditModeChanged.emit(this.isTextEditMode);
        }
    }

    focusInputElement() {
        FocusDirective.focusElement(this.inputElementRef.nativeElement)
    }

    selectAllTextInInputElement() {
        let input = this.inputElementRef;
        setTimeout(function () { input.nativeElement.select(); }, 50);
    }
}
