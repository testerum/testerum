import {Component, Input, OnInit} from '@angular/core';
import {NgModel} from "@angular/forms";

@Component({
    moduleId: module.id,
    selector: 'input-error',
    templateUrl: 'input-error.component.html',
    styleUrls: ["input-error.component.scss"]
})

export class InputErrorComponent implements OnInit {
    @Input() model: NgModel;
    @Input() errorMessages: any;
    @Input() width: number;

    constructor() {
    }

    ngOnInit() {
    }

    errorKeys() {
        return this.model.errors ? Object.keys(this.model.errors) : [];
    }
}
