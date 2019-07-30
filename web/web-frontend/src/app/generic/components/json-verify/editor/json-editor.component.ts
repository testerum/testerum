import {Component, EventEmitter, Input, Output} from '@angular/core';
import {editor} from "monaco-editor";

@Component({
    moduleId: module.id,
    selector: 'json-editor',
    templateUrl: 'json-editor.component.html',
    styleUrls: ['json-editor.component.scss']
})
export class JsonEditorComponent {
    @Input() jsonText: string = "";
    @Input() editMode: boolean = true;

    @Output() change: EventEmitter<string> = new EventEmitter<string>();

    editorOptions: editor.IEditorConstructionOptions = {
        language: 'json',
        readOnly: !this.editMode,
    };

    onChange(code: string) {
        this.change.emit(code)
    }
}
