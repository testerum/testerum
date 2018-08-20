import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {JsonVerify} from "../model/json-verify.model";

@Component({
    selector: 'json-verify-editor',
    templateUrl: './json-verify-editor.component.html',
    styleUrls: ['./json-verify-editor.component.scss']
})
export class JsonVerifyEditorComponent {

    @Input() isEditMode: boolean = true;
    @Input() model: string;
    @Output() modelChange = new EventEmitter<string>();

    @Output() change = new EventEmitter<string>();

    options: any = {
        printMargin: true,
        highlightActiveLine: true,
        useSoftTabs: true
    };

    onTextChange(code: string) {
        this.change.emit(code);
        this.modelChange.emit(code);
    }
}
