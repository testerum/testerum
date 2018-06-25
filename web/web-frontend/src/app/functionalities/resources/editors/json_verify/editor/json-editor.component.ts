import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import 'brace/mode/json';
import 'brace/theme/eclipse';
import {AceEditorComponent} from "ng2-ace-editor";


@Component({
    moduleId: module.id,
    selector: 'json-editor',
    templateUrl: 'json-editor.component.html',
    styleUrls: ['json-editor.component.css']
})

export class JsonEditorComponent implements OnInit {
    @Input() jsonText: string = "";
    @Input() editMode: boolean = true;

    @Output() change: EventEmitter<string> = new EventEmitter<string>();

    @ViewChild('editor') editor: AceEditorComponent;

    hasFocus = false;

    options: any = {
        maxLines: 5,
        printMargin: true,
        highlightActiveLine: true,
        useSoftTabs: true
    };

    ngOnInit(): void {
        this.editor.getEditor().onFocus = ((event: FocusEvent) => {
            this.hasFocus = true;
            this.editor.setOptions({
                maxLines: 30
            })
        });
        this.editor.getEditor().onBlur = ((event: FocusEvent) => {
            this.hasFocus = false;

            this.editor.setOptions({
                maxLines: 5
            })
        })
    }

    onChange(code: string) {
        this.change.emit(code)
    }
}
