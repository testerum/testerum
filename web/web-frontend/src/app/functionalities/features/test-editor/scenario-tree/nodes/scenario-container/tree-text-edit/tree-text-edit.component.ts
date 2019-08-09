import {Component, ElementRef, forwardRef, Input, OnInit, ViewChild} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from "@angular/forms";
import {FocusDirective} from "../../../../../../../generic/directives/focus.directive";

@Component({
    selector: 'tree-text-edit',
    templateUrl: './tree-text-edit.component.html',
    styleUrls: ['./tree-text-edit.component.scss'],
    providers: [
        { provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => TreeTextEditComponent), multi: true}
    ]
})
export class TreeTextEditComponent implements ControlValueAccessor {

    text: string;
    @Input() isEditMode: boolean;
    @Input() isTextEditMode: boolean = false;

    @ViewChild('input') inputElementRef: ElementRef;

    propagateChange = (_: any) => {};

    writeValue(obj: any): void {
        this.text = obj;
    }

    registerOnChange(fn: any): void {
        this.propagateChange = fn;
    }

    registerOnTouched(fn: any): void {}
    setDisabledState(isDisabled: boolean): void {}

    onTextChange(text: string) {
        this.propagateChange(this.text);
    }

    setTextEditMode(isTextEditMode: boolean) {
        this.isTextEditMode = isTextEditMode;
    }

    focusInputElement() {
        FocusDirective.focusElement(this.inputElementRef.nativeElement)
    }
}
