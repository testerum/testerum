import {Component, forwardRef, Input, ViewChild} from '@angular/core';
import {ControlValueAccessor, FormControl, NG_VALIDATORS, NG_VALUE_ACCESSOR} from "@angular/forms";
import {FileChooserModalComponent} from "./dialog/file-chooser-modal.component";
import {FileChooserModalService} from "./dialog/file-chooser-modal.service";

@Component({
    selector: 'file-chooser',
    templateUrl: 'file-chooser-input.component.html',
    styleUrls: ["file-chooser-input.component.scss"],
    providers: [
        { provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => FileChooserInputComponent), multi: true},
        { provide: NG_VALIDATORS, useExisting: forwardRef(() => FileChooserInputComponent), multi: true }
    ]
})
export class FileChooserInputComponent implements ControlValueAccessor {

    // the method set in registerOnChange to emit changes back to the form
    private propagateChange = (_: any) => { };
    validateFn:any = () => {};

    value: string;
    @Input() disabled: boolean = false;
    @Input() showFiles: boolean = false;
    @ViewChild(FileChooserModalComponent) fileChooserModalComponent: FileChooserModalComponent;

    constructor(private fileChooserModalService: FileChooserModalService) {
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
            this.fileChooserModalService.showDirectoryChooserDialogModal(this.showFiles).subscribe( (selectedPath: string) => {
                this.value = selectedPath;
                this.propagateChange(selectedPath);
            });
        }
    }

    validate(c: FormControl) {
        return this.validateFn(c);
    }
}
