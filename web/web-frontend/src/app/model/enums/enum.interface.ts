
export abstract class Enum {

    readonly enumAsString: string;
    constructor(enumAsString: string) {
        this.enumAsString = enumAsString;
    }

    toString(): string {
        return this.enumAsString
    }
}
