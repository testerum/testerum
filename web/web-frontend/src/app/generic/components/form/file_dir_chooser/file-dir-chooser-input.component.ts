import {Component, forwardRef, Input, ViewChild} from '@angular/core';
import {ControlValueAccessor, FormControl, NG_VALIDATORS, NG_VALUE_ACCESSOR} from "@angular/forms";
import {FileDirChooserModalComponent} from "./dialog/file-dir-chooser-modal.component";
import {FileDirChooserModalService} from "./dialog/file-dir-chooser-modal.service";

@Component({
    selector: 'file-dir-chooser',
    templateUrl: 'file-dir-chooser-input.component.html',
    styleUrls: ["file-dir-chooser-input.component.scss"],
    providers: [
        { provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => FileDirChooserInputComponent), multi: true},
        { provide: NG_VALIDATORS, useExisting: forwardRef(() => FileDirChooserInputComponent), multi: true }
    ]
})
export class FileDirChooserInputComponent implements ControlValueAccessor {

    // the method set in registerOnChange to emit changes back to the form
    private propagateChange = (_: any) => { };
    validateFn:any = () => {};

    value: string;
    @Input() disabled = false;
    @ViewChild(FileDirChooserModalComponent) directoryChooserDialogComponent: FileDirChooserModalComponent;

    constructor(private directoryChooserDialogService: FileDirChooserModalService) {
    }

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
            this.directoryChooserDialogService.showDirectoryChooserDialogModal().subscribe( (selectedPath: string) => {
                this.value = selectedPath;
                this.propagateChange(selectedPath);
            });
        }
    }

    validate(c: FormControl) {
        return this.validateFn(c);
    }
}
