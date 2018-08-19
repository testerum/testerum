import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {JsonVerify} from "../model/json-verify.model";

@Component({
    selector: 'json-verify-editor',
    templateUrl: './json-verify-editor.component.html',
    styleUrls: ['./json-verify-editor.component.scss']
})
export class JsonVerifyEditorComponent implements OnInit {

    @Input() jsonVerify: JsonVerify;
    @Input() isEditMode: boolean = true;

    @Output() change = new EventEmitter<string>();

    options: any = {
        printMargin: true,
        highlightActiveLine: true,
        useSoftTabs: true
    };

    constructor() {
    }

    ngOnInit() {
    }

    onTextChange(code: string) {
        this.change.emit(code)
    }
}
