import {Component, forwardRef, Input, ViewChild} from '@angular/core';
import {ControlValueAccessor, FormControl, NG_VALIDATORS, NG_VALUE_ACCESSOR} from "@angular/forms";
import {DirectoryChooserDialogComponent} from "./dialog/directory-chooser-dialog.component";

@Component({
    selector: 'file-dir-chooser',
    templateUrl: 'file-dir-chooser.component.html',
    styleUrls: ["file-dir-chooser.component.scss"],
    providers: [
        { provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => FileDirChooserComponent), multi: true},
        { provide: NG_VALIDATORS, useExisting: forwardRef(() => FileDirChooserComponent), multi: true }
    ]
})

export class FileDirChooserComponent implements ControlValueAccessor {

    // the method set in registerOnChange to emit changes back to the form
    private propagateChange = (_: any) => { };
    validateFn:any = () => {};

    value: string;
    @Input() disabled = false;
    @ViewChild(DirectoryChooserDialogComponent) directoryChooserDialogComponent: DirectoryChooserDialogComponent;

    // this is the initial value set to the component
    public writeValue(obj: any) {
        if(obj) {
            this.value = obj;
        } else {
            this.value = ""
        }
    }

    // registers 'fn' that will be fired when changes are made
    // this is how we emit the changes back to the form
    public registerOnChange(fn: any) {
        this.propagateChange = fn;
    }

    // not used, used for touch input
    public registerOnTouched() { }

    // change events from the textarea
    onChangeEvent(event) {
        this.value = event.target.value;

        this.propagateChange(this.value);
    }

    public showDirectoryChooserModal() {
        if (!this.disabled) {
            this.directoryChooserDialogComponent.show();
        }
    }

    onPathSelected($event:any) {
        this.value = $event;
        this.propagateChange(this.value);
    }

    validate(c: FormControl) {
        return this.validateFn(c);
    }
}
