import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {JsonCompareModeEnum} from "../model/json-compare-mode.enum";
// import * as ace from 'brace/index';
import * as ace from 'brace';
import 'brace/mode/javascript';
import 'brace/mode/json';
import 'brace/theme/eclipse';
import {AceEditorComponent} from "ng2-ace-editor";

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

    @ViewChild("editor") aceEditor: AceEditorComponent;

    options: any = {
        printMargin: true,
        highlightActiveLine: true,
        useSoftTabs: true
    };

    onTextChange(code: string) {
        this.change.emit(code);
        this.modelChange.emit(code);
    }

    setCompareModeEvent(selectedJsonCompareMode: JsonCompareModeEnum) {
        let aceEditor = this.aceEditor.getEditor();
        let aceDocument = aceEditor.env.document;
        let cursor = aceDocument.selection.getCursor();
        let tokenIterator = new (ace.acequire('ace/token_iterator').TokenIterator)(aceDocument, cursor.row, cursor.column);
        while (tokenIterator.getCurrentToken() != null && tokenIterator.getCurrentToken().type != 'paren.lparen') {
            tokenIterator.stepBackward();
        }
        if(tokenIterator.getCurrentToken().value == "{") {
            aceEditor.navigateTo(tokenIterator.getCurrentTokenRow(), tokenIterator.getCurrentTokenColumn() + 1);

            if(this.currentTokenContainsCompareMode(tokenIterator)) {
                this.removeCompareMode(aceEditor, tokenIterator)
            }
            aceEditor.insert('"=compareMode": "'+ selectedJsonCompareMode.enumAsString +'",');
        }
        if(tokenIterator.getCurrentToken().value == "[") {
            aceEditor.navigateTo(tokenIterator.getCurrentTokenRow(), tokenIterator.getCurrentTokenColumn() + 1);

            if(this.currentTokenContainsCompareMode(tokenIterator)) {
                this.removeCompareMode(aceEditor, tokenIterator)
            }
            aceEditor.insert('"=compareMode: '+ selectedJsonCompareMode.enumAsString +'",');
        }
    }

    private currentTokenContainsCompareMode(tokenIterator: any): boolean {
        let stepForward = tokenIterator.stepForward();
        if(stepForward.value.startsWith('"=compareMode')) {
            return true;
        }
        return false;
    }

    private removeCompareMode(aceEditor: ace.Editor, tokenIterator: any) {
        let currentToken = tokenIterator.getCurrentToken();
        while(currentToken != null &&
                (currentToken.value != "," &&
                 currentToken.type != 'paren.rparen')) {
            console.log("iterator", currentToken);

            tokenIterator.stepForward();
            currentToken = tokenIterator.getCurrentToken();
        }
        console.log("last", currentToken);
        aceEditor.selection.selectTo(tokenIterator.getCurrentTokenRow(), tokenIterator.getCurrentTokenColumn() + 1);
    }
}
