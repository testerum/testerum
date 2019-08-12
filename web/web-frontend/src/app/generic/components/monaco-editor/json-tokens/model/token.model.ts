
export class Token {

    constructor(line: number, column: number, word: string, type: string, language: string, parent: Token = null, children: Token[] = []) {
        this.line = line;
        this.column = column;
        this.word = word;
        this.type = type;
        this.language = language;
        this.parent = parent;
        this.children = children;
    }

    line: number;
    column: number;
    word: string;
    type: string;
    language: string;
    parent: Token | null;
    children: Token[];
    previousToken: Token;
    nextToken: Token;
}
