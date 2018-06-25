import {Enum} from "../../../../../../../../model/enums/enum.interface";

export class HttpMockRequestBodyVerifyType extends Enum {

    public static TEXT = new HttpMockRequestBodyVerifyType("TEXT", "Text", "text");
    public static JSON = new HttpMockRequestBodyVerifyType("JSON", "JSON", "json");
    public static XML = new HttpMockRequestBodyVerifyType("XML", "XML", "xml");
    public static HTML = new HttpMockRequestBodyVerifyType("HTML", "HTML", "html");

    static readonly enums: Array<HttpMockRequestBodyVerifyType> = [
        HttpMockRequestBodyVerifyType.TEXT,
        HttpMockRequestBodyVerifyType.JSON,
        HttpMockRequestBodyVerifyType.XML,
        HttpMockRequestBodyVerifyType.HTML,
    ];

    editorMode: string;
    public readonly asSerialized: string;
    private constructor(asSerialized: string, asString: string, editorMode: string) {
        super(asString);
        this.editorMode = editorMode;
        this.asSerialized = asSerialized;
    }

    public static fromString(methodAsString: string): HttpMockRequestBodyVerifyType {
        for (let enumValue of HttpMockRequestBodyVerifyType.enums) {
            if(enumValue.enumAsString.toUpperCase() == methodAsString.toUpperCase()) {
                return enumValue;
            }
        }

        return null;
    }

    public static fromSerialization(asSerialized: string): HttpMockRequestBodyVerifyType {
        for (let enumValue of HttpMockRequestBodyVerifyType.enums) {
            if(enumValue.asSerialized.toUpperCase() == asSerialized.toUpperCase()) {
                return enumValue;
            }
        }

        return null;
    }
}
