import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component, ElementRef,
    EventEmitter,
    Input, OnChanges,
    OnInit,
    Output, SimpleChanges,
    ViewChild
} from '@angular/core';
import {JsonCompareModeEnum} from "../model/json-compare-mode.enum";
import {filter, take} from "rxjs/operators";
import {JsonTokens} from "../../monaco-editor/json-tokens/json-tokens.class";
import {editor, Range} from 'monaco-editor';
import {Token} from "../../monaco-editor/json-tokens/model/token.model";
import {JsonTokenType} from "../../monaco-editor/json-tokens/model/json-token.type";
import {MonacoEditorLoaderService} from "../../monaco-editor/services/monaco-editor-loader.service";
import {MonacoEditorComponent} from "../../monaco-editor/components/monaco-editor/monaco-editor.component";

@Component({
    selector: 'json-verify-editor',
    templateUrl: './json-verify-editor.component.html',
    styleUrls: ['./json-verify-editor.component.scss'],
    changeDetection: ChangeDetectionStrategy.Default
})
export class JsonVerifyEditorComponent implements OnInit, OnChanges {
    readonly COMPARE_MODE_NODE_KEY = "=compareMode";

    @Input() isEditMode: boolean = true;
    @Input() model: string;
    @Output() modelChange = new EventEmitter<string>();
    @Output() textChange = new EventEmitter<string>();

    @ViewChild("monacoEditorComponent", { static: true }) monacoEditorComponent: MonacoEditorComponent;

    editorOptions: editor.IStandaloneEditorConstructionOptions = {
        language: 'json',
        readOnly: !this.isEditMode
    };
    private monaco: any;

    private jsonTokens: JsonTokens;

    constructor(private cd: ChangeDetectorRef,
                private monacoLoader: MonacoEditorLoaderService) {}

    ngOnInit(): void {
        this.editorOptions.readOnly = !this.isEditMode;

        this.monacoLoader.isMonacoLoaded.pipe(
            filter(isLoaded => isLoaded),
            take(1)
        ).subscribe(() => {
            this.monaco = ((window as any).monaco);
        });
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['isEditMode'] != null) {
            this.editorOptions = Object.assign(
                {},
                this.editorOptions,
                {
                    readOnly: !this.isEditMode
                }
            );
        }
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
    }

    onTextChange(value: string) {
        this.textChange.emit(value);
        this.modelChange.emit(value);
    }

    setCompareModeEvent(selectedJsonCompareMode: JsonCompareModeEnum) {
        let monacoEditor = this.monacoEditorComponent.editor;
        let line = monacoEditor.getPosition().lineNumber;
        let column = monacoEditor.getPosition().column;
        let tokens = this.monaco.editor.tokenize(this.model, "json");
        this.jsonTokens = new JsonTokens(tokens, monacoEditor);

        let tokenUnderCaret: Token = this.getTokenByPosition(line, column, this.jsonTokens);

        let compareTokenKey = this.getCompareTokenKey(tokenUnderCaret);

        if (compareTokenKey) {
            this.changeCompareTokenValue(compareTokenKey, selectedJsonCompareMode, monacoEditor);
        } else {
            this.addCompareMode(tokenUnderCaret, selectedJsonCompareMode, monacoEditor);
        }
    }

    private getTokenByPosition(line: number, column: number, jsonTokens: JsonTokens) {
        let biggerToken: Token;
        let biggerTokenIndex = null;
        for (let i = 0; i < jsonTokens.tokensAsList.length; i++) {
            let jsonToken = jsonTokens.tokensAsList[i];
            if (jsonToken.line > line ||
                (jsonToken.line == line && jsonToken.column > column)) {
                biggerToken = jsonToken;
                biggerTokenIndex = i;
                break;
            }
        }
        let token = biggerTokenIndex ? jsonTokens.tokensAsList[biggerTokenIndex -1] : biggerToken;
        if (token.column + token.word.length == column) {
            return biggerToken;
        }
        return token;
    }

    private getCompareTokenKey(tokenUnderCaret: Token): Token|null {
        let compareTokenKey: Token | null;
        if (tokenUnderCaret.parent == null) {
            let rootNode = this.getRootNode();
            if(rootNode == null) return null;

            compareTokenKey = this.findCompareTokenKey(rootNode.children);
        } else {
            compareTokenKey = this.findCompareTokenKey(tokenUnderCaret.parent.children);
        }
        return compareTokenKey;
    }

    private findCompareTokenKey(objectTokens: Token[]): Token | null {
        for (const token of objectTokens) {
            if (token.word.startsWith('"'+this.COMPARE_MODE_NODE_KEY)) {
                return token;
            }
        }
        return null;
    }

    private getCompareNodeEndToken(compareTokenKey: Token): Token {
        let columnToken: Token = this.getNextNonEmptyToken(compareTokenKey);
        if(columnToken.type != JsonTokenType.COLUMN) return columnToken;

        let valueToken: Token = this.getNextNonEmptyToken(columnToken);
        if(valueToken.type != JsonTokenType.STRING) return valueToken;

        return valueToken;
    }

    private getNextNonEmptyToken(previewToken: Token): Token {
        let nextToken = previewToken.nextToken;
        while (nextToken != null && nextToken.type == JsonTokenType.EMPTY) {
            nextToken = nextToken.nextToken
        }

        return nextToken;
    }

    private addCompareMode(tokenUnderCaret: Token, selectedJsonCompareMode: JsonCompareModeEnum, editor: editor.IStandaloneCodeEditor) {

        if (tokenUnderCaret.parent == null) {
            this.insertCompareTokenInRootObject(this.jsonTokens.tokens, selectedJsonCompareMode, editor);
        } else {
            this.insertCompareTokenInObject(tokenUnderCaret.parent, selectedJsonCompareMode, editor)
        }
    }

    private insertCompareTokenInRootObject(objectTokens: Token[], selectedJsonCompareMode: JsonCompareModeEnum, monacoEditor: editor.IStandaloneCodeEditor) {
        let rootNode = this.getRootNode();

        if(rootNode == null) return;

        let textToInsert = this.getTextToInsert(rootNode.type, selectedJsonCompareMode)+",";

        monacoEditor.executeEdits("", [{
            forceMoveMarkers:true,
            range: new Range(rootNode.line, rootNode.column+1, rootNode.line, rootNode.column+1),
            text: textToInsert
        }]);
    }

    private getRootNode() {
        let rootNode: Token = null;
        for (const token of this.jsonTokens.tokens) {
            if (token.type == JsonTokenType.OBJECT_OPEN || token.type == JsonTokenType.ARRAY_OPEN) {
                rootNode = token;
                break;
            }
        }
        return rootNode;
    }

    private getTextToInsert(tokenType: string, selectedJsonCompareMode: JsonCompareModeEnum) {
        if (tokenType == JsonTokenType.OBJECT_OPEN) {
            return '"=compareMode": "'+selectedJsonCompareMode.toString()+'"'
        }
        if (tokenType == JsonTokenType.ARRAY_OPEN) {
            return '"=compareMode: '+selectedJsonCompareMode.toString()+'"'
        }
        throw new Error("unexpected json token");
    }

    private changeCompareTokenValue(compareTokenKey: Token, selectedJsonCompareMode: JsonCompareModeEnum, monacoEditor: editor.IStandaloneCodeEditor) {
        let parentTokenType = null;
        if (compareTokenKey.parent) {
            parentTokenType = compareTokenKey.parent.type;
        } else {
            parentTokenType = this.getRootNode().type
        }

        let textToInsert = this.getTextToInsert(parentTokenType, selectedJsonCompareMode);

        if (parentTokenType == JsonTokenType.ARRAY_OPEN) {
            let compareTokenEndColumn = compareTokenKey.column + compareTokenKey.word.length;

            monacoEditor.executeEdits("", [{
                    forceMoveMarkers:true,
                    range: new Range(compareTokenKey.line, compareTokenKey.column, compareTokenKey.line, compareTokenEndColumn),
                    text: textToInsert
            }]);
        }

        if (parentTokenType == JsonTokenType.OBJECT_OPEN) {
            let compareTokenEndToken = this.getCompareNodeEndToken(compareTokenKey);
            let compareTokenEndColumn = compareTokenEndToken.column + compareTokenEndToken.word.length;

            monacoEditor.executeEdits("", [{
                forceMoveMarkers:true,
                range: new Range(compareTokenKey.line, compareTokenKey.column, compareTokenKey.line, compareTokenEndColumn),
                text: textToInsert
            }]);
        }
    }

    private insertCompareTokenInObject(objectToken: Token, selectedJsonCompareMode: JsonCompareModeEnum, monacoEditor: editor.IStandaloneCodeEditor) {
        let parentTokenType = objectToken.type;
        let textToInsert = this.getTextToInsert(parentTokenType, selectedJsonCompareMode) + ",";

        monacoEditor.executeEdits("", [{
            forceMoveMarkers:true,
            range: new Range(objectToken.line, objectToken.column+1, objectToken.line, objectToken.column+1),
            text: textToInsert
        }]);
    }

    formatJson() {
        let editor = this.monacoEditorComponent.editor;
        setTimeout(function() {
            editor.getAction('editor.action.formatDocument').run();
        }, 300);

    }
}
