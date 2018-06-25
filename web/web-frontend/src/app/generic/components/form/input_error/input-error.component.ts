import {Component, ElementRef, Input, OnInit} from '@angular/core';
import {NgModel} from "@angular/forms";

@Component({
    moduleId: module.id,
    selector: 'input-error',
    templateUrl: 'input-error.component.html',
    styleUrls: ["input-error.component.css"]
})

export class InputErrorComponent implements OnInit {
    @Input() model: NgModel;
    @Input() errorMessages: any;

    constructor() {
    }

    ngOnInit() {
    }

    errorKeys() {
        return this.model.errors ? Object.keys(this.model.errors) : [];
    }
}
