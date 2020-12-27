import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {InputTypeEnum} from "./model/input-type.enum";
import {SelectItem} from "primeng/api";
import {StringUtils} from "../../../../utils/string-utils.util";

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
    }

    onNumberChange() {
        if (StringUtils.isEmpty(this.value)) {
            this.value = this.defaultValue;
        }
        this.onValueChange();
    }

    getValueAsBoolean(): boolean {
        return this.value === "true"
    }

    onBooleanValueChange($event: any) {
        this.value = "" + $event.checked;
        this.onValueChange();
    }
}
