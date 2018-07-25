import {Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {AceEditorComponent} from "ng2-ace-editor";
import 'brace/mode/sql';
import 'brace/theme/crimson_editor';

@Component({
    moduleId: module.id,
    selector: 'rdbms-sql-editor-component',
    templateUrl: 'rdbms-sql-editor.component.html',
    styleUrls: ['rdbms-sql-editor.component.scss']
})

export class RdbmsSqlEditorComponent {
    @Input() sqlText: string = "";
    @Input() editMode: boolean = false;

    @Output() change: EventEmitter<string> = new EventEmitter<string>();

    @ViewChild('editor') editor: AceEditorComponent;

    options: any = {
        maxLines: 1000,
        printMargin: true,
        highlightActiveLine: true,
        useSoftTabs: true

    };

    onChange(code: string) {
        this.change.emit(code)
    }
}
