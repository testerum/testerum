import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {editor} from "monaco-editor";

@Component({
    moduleId: module.id,
    selector: 'rdbms-sql-editor-component',
    templateUrl: 'rdbms-sql-editor.component.html',
    styleUrls: ['rdbms-sql-editor.component.scss']
})

export class RdbmsSqlEditorComponent implements OnChanges {
    @Input() sqlText: string = "";
    @Input() editMode: boolean = false;

    @Output() change: EventEmitter<string> = new EventEmitter<string>();

    editorOptions: editor.IEditorConstructionOptions = {
        language: 'sql',
        readOnly: !this.editMode
    };

    onChange(code: string) {
        this.change.emit(code)
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['editMode'] != null) {
            this.editorOptions = Object.assign(
                {},
                this.editorOptions,
                {
                    readOnly: !this.editMode
                }
            );
        }
    }
}
