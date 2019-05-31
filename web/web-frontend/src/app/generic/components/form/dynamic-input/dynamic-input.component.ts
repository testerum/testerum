import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {InputTypeEnum} from "./model/input-type.enum";
import {SelectItem} from "primeng/api";

@Component({
    selector: 'dynamic-input',
    templateUrl: './dynamic-input.component.html',
    styleUrls: ['./dynamic-input.component.scss']
})
export class DynamicInputComponent implements OnInit{

    @Input() inputType: InputTypeEnum;
    @Input() defaultValue: string;
    @Input() value: string;
    @Input() possibleValues: string[];
    @Output() valueChange = new EventEmitter();
    @Output() change: EventEmitter<string> = new EventEmitter<string>();

    possibleValuesAsSelectItems: SelectItem[] = [];
    InputTypeEnum = InputTypeEnum;

    ngOnInit(): void {
        if (this.possibleValues) {
            for (const possibleValue of this.possibleValues) {
                this.possibleValuesAsSelectItems.push(
                    {label: possibleValue, value: possibleValue}
                )
            }
        }
    }

    onValueChange() {
        this.valueChange.emit(this.value);
        this.change.emit(this.value);
    }

    getValueAsBoolean(): boolean {
        return this.value === "true"
    }

    onBooleanValueChange($event: any) {
        this.value = "" + $event.checked;
        this.onValueChange();
    }
}
