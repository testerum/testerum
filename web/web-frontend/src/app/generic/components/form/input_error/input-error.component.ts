import {Component, Input, OnInit} from '@angular/core';
import {NgModel} from "@angular/forms";
import {ArrayUtil} from "../../../../utils/array.util";

@Component({
    moduleId: module.id,
    selector: 'input-error',
    templateUrl: 'input-error.component.html',
    styleUrls: ["input-error.component.scss"]
})

export class InputErrorComponent {
    @Input() model: NgModel;
    @Input() errorsKey: string[] = [];
    @Input() errorMessages: any;
    @Input() width: number;

    allErrorsKey(): string[] {
        let errorsKey = [];

        let modelErrorsKey = this.model && this.model.errors ? Object.keys(this.model.errors) : [];
        for (const modelErrorKey of modelErrorsKey) {
            errorsKey.push(modelErrorKey)
        }

        if (this.errorsKey) {
            for (const inputErrorKey of this.errorsKey) {
                errorsKey.push(inputErrorKey);
            }
        }

        return errorsKey;
    }
}
