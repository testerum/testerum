import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {InputTypeEnum} from "./model/input-type.enum";
import {CompareMode} from "../../../../model/enums/compare-mode.enum";

@Component({
    selector: 'dynamic-input',
    templateUrl: './dynamic-input.component.html',
    styleUrls: ['./dynamic-input.component.scss']
})
export class DynamicInputComponent implements OnInit {

    @Input() inputType: InputTypeEnum;
    @Input() value: string;
    @Output() valueChange = new EventEmitter();
    @Output() change: EventEmitter<string> = new EventEmitter<string>();

    InputTypeEnum = InputTypeEnum;

    constructor() {
    }

    ngOnInit() {
    }

    onValueChange(event: any) {
        this.valueChange.emit(this.value);
        this.change.emit(this.value);
    }
}
