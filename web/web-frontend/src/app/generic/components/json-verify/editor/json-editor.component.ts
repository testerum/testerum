import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
import {editor} from "monaco-editor";
import {MonacoEditorComponent} from "../../monaco-editor/components/monaco-editor/monaco-editor.component";
import {JsonUtil} from "../../../../utils/json.util";

@Component({
    moduleId: module.id,
    selector: 'json-editor',
    templateUrl: 'json-editor.component.html',
    styleUrls: ['json-editor.component.scss']
})
export class JsonEditorComponent implements OnInit, OnChanges {
    @Input() jsonText: string = "";
    @Input() editMode: boolean = true;

    @Output() valueChange: EventEmitter<string> = new EventEmitter<string>();

    @ViewChild("monacoEditorComponent", { static: true }) monacoEditorComponent: MonacoEditorComponent;

    isValidJson: boolean = true;

    editorOptions: editor.IStandaloneEditorConstructionOptions = {
        language: 'json',
        readOnly: !this.editMode,
    };

    ngOnInit(): void {
        this.isValidJson = JsonUtil.isJson(this.jsonText);
    }

    onChange(jsonAsString: string) {
        this.isValidJson = JsonUtil.isJson(jsonAsString);
        this.valueChange.emit(jsonAsString)
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

    formatJson() {
        let editor = this.monacoEditorComponent.editor;
        setTimeout(function() {
            editor.getAction('editor.action.formatDocument').run();
        }, 300);
    }

    getJsonResourceExplanation(): string {
        return "This editor allows you to define a JSON resource.\n" +
            "You can use Testerum Expressions `{{expression}}` wherever is needed, for e.g.:\n" +
            "`" +
            "{\n" +
            "  \"firstName\": \"{{dataGenerator().name().firstName()}}\",\n" +
            "  \"lastName\": \"{{dataGenerator().name().lastName()}}\",\n" +
            "  \"appointmentDate\": \"{{currentDate().plusDays(2)}}\",\n" +
            "}\n" +
            "`\n" +
            "For more functions or how to use Testerum Expressions and Variables please check our documentation."
    }
}
