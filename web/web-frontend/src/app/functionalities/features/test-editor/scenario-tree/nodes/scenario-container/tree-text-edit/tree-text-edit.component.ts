import {
    Component,
    ElementRef, EventEmitter,
    forwardRef,
    Input,
    OnChanges,
    OnInit,
    Output,
    SimpleChanges,
    ViewChild
} from '@angular/core';
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
export class TreeTextEditComponent implements ControlValueAccessor, OnChanges {

    text: string;
    @Input() isEditMode: boolean;
    @Input() isTextEditMode: boolean = false;
    @Output() onTextEditModeChanged = new EventEmitter<boolean>();

    @ViewChild('input') inputElementRef: ElementRef;

    private propagateChange: Function;

    writeValue(obj: any): void {
        this.text = obj;
    }

    registerOnChange(fn: any): void {
        this.propagateChange = fn;
    }

    registerOnTouched(fn: any): void {}
    setDisabledState(isDisabled: boolean): void {}

    ngOnChanges(changes: SimpleChanges): void {
        console.log("changes", changes);
    }


    onTextChange(text: string) {
        this.text = text;
        this.propagateChange(this.text);
    }

    setTextEditMode(isTextEditMode: boolean) {
        this.isTextEditMode = isTextEditMode;
        this.onTextEditModeChanged.emit(this.isTextEditMode);
    }

    focusInputElement() {
        FocusDirective.focusElement(this.inputElementRef.nativeElement)
    }

    selectAllTextInInputElement() {
        let input = this.inputElementRef;
        setTimeout(function () { input.nativeElement.select(); }, 50);
    }
}
