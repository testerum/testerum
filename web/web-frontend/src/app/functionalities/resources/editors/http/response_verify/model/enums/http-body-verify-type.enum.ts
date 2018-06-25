import {Enum} from "../../../../../../../model/enums/enum.interface";

export class HttpBodyVerifyType extends Enum {

    public static TEXT = new HttpBodyVerifyType("TEXT", "Text", "text");
    public static JSON = new HttpBodyVerifyType("JSON", "JSON", "json");
    public static XML = new HttpBodyVerifyType("XML", "XML", "xml");
    public static HTML = new HttpBodyVerifyType("HTML", "HTML", "html");

    static readonly enums: Array<HttpBodyVerifyType> = [
        HttpBodyVerifyType.TEXT,
        HttpBodyVerifyType.JSON,
        HttpBodyVerifyType.XML,
        HttpBodyVerifyType.HTML,
    ];

    editorMode: string;
    public readonly asSerialized: string;
    private constructor(asSerialized: string, asString: string, editorMode: string) {
        super(asString);
        this.editorMode = editorMode;
        this.asSerialized = asSerialized;
    }

    public static fromString(methodAsString: string): HttpBodyVerifyType {
        for (let enumValue of HttpBodyVerifyType.enums) {
            if(enumValue.enumAsString.toUpperCase() == methodAsString.toUpperCase()) {
                return enumValue;
            }
        }

        return null;
    }

    public static fromSerialization(asSerialized: string): HttpBodyVerifyType {
        for (let enumValue of HttpBodyVerifyType.enums) {
            if(enumValue.asSerialized.toUpperCase() == asSerialized.toUpperCase()) {
                return enumValue;
            }
        }

        return null;
    }
}
