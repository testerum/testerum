import {Token} from "./model/token.model";
import { editor } from 'monaco-editor';
import {MonacoJsonTokenType} from "./model/monaco-json-token.type";
import {JsonTokenType} from "./model/json-token.type";
import {ArrayUtil} from "../../../../utils/array.util";

export class JsonTokens {

    tokens: Token[] = [];
    tokensAsList: Token[] = [];

    constructor(jsTokens: any[][], editor: editor.IStandaloneCodeEditor) {
        let tokensAsList: Token[] = [];
        for (let i = 0; i < jsTokens.length; i++) {
            let line = i + 1;
            let lineContent = editor.getModel().getLineContent(line);

            for (let j = 0; j < jsTokens[i].length; j++) {
                const jsToken = jsTokens[i][j];
                let column: number = jsToken.offset;

                let newWord = this.getTokenWord(lineContent, jsToken, j, jsTokens[i]);

                let tokenType = this.getTokenType(newWord, jsToken.type);

                tokensAsList.push(new Token(line, column+1, newWord, tokenType, jsToken.language));
            }
        }

        this.tokensAsList = ArrayUtil.copyArrayOfObjects(tokensAsList);
        this.setTokensAsTree(tokensAsList, this.tokens, null);
    }

    private setTokensAsTree(tokensAsList: Token[], listToAddToken: Token[], parent: Token) {
        if(tokensAsList.length == 0) return;

        let currentToken = tokensAsList[0];
        tokensAsList.shift();

        currentToken.parent = parent;

        if (listToAddToken.length > 0) { currentToken.previousToken = listToAddToken[listToAddToken.length - 1]; }
        else if (parent && parent.parent) { currentToken.previousToken = parent.parent.children[parent.parent.children.length - 1] }
        else currentToken.previousToken = this.tokens[this.tokens.length - 1];

        if (currentToken.previousToken != null) {
            currentToken.previousToken.nextToken = currentToken;
        }

        listToAddToken.push(currentToken);

        if (currentToken.type == JsonTokenType.OBJECT_OPEN || currentToken.type == JsonTokenType.ARRAY_OPEN) {
            parent = currentToken;
            listToAddToken = parent.children
        }
        if (currentToken.type == JsonTokenType.OBJECT_CLOSE || currentToken.type == JsonTokenType.ARRAY_CLOSE) {
            parent = parent.parent;
            listToAddToken = parent ? parent.children : this.tokens;
        }

        this.setTokensAsTree(tokensAsList, listToAddToken, parent)
    }

    private getTokenType(word: string, type: string) {
        switch(type) {
            case MonacoJsonTokenType.EMPTY: { return MonacoJsonTokenType.EMPTY;}
            case MonacoJsonTokenType.COLUMN: { return MonacoJsonTokenType.COLUMN;}
            case MonacoJsonTokenType.COMMA: { return MonacoJsonTokenType.COMMA;}
            case MonacoJsonTokenType.KEY: { return MonacoJsonTokenType.KEY;}
            case MonacoJsonTokenType.NUMBER: { return MonacoJsonTokenType.NUMBER;}
            case MonacoJsonTokenType.STRING: { return MonacoJsonTokenType.STRING;}
        }

        if (type == MonacoJsonTokenType.OBJECT) {
            if(word == "{") return JsonTokenType.OBJECT_OPEN;
            if(word == "}") return JsonTokenType.OBJECT_CLOSE;
        }
        if (type == MonacoJsonTokenType.ARRAY) {
            if(word == "[") return JsonTokenType.ARRAY_OPEN;
            if(word == "]") return JsonTokenType.ARRAY_CLOSE;
        }
        return type;
    }

    private getTokenWord(lineText: string, jsToken: any, tokenIndex: number, lineTokens: any[]) {
        let isLastIndex = tokenIndex == lineTokens.length - 1;
        if (isLastIndex) {
            return lineText.substring(jsToken.offset, lineText.length)
        } else {
            return lineText.substring(jsToken.offset, lineTokens[tokenIndex+1].offset)
        }
    }
}
