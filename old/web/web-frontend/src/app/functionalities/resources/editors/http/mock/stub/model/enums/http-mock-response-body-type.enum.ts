import {Enum} from "../../../../../../../../model/enums/enum.interface";

export class HttpMockResponseBodyType extends Enum {

    public static CONTENT_TYPE_HEADER_KEY: string = "Content-Type";

    public static OTHER = new HttpMockResponseBodyType("Other", "OTHER", null, "text");
    public static HTML = new HttpMockResponseBodyType("HTML", "HTML", "text/html", "html");
    public static JAVASCRIPT = new HttpMockResponseBodyType("Javascript", "JAVASCRIPT", "application/javascript", "javascript");
    public static JSON = new HttpMockResponseBodyType("JSON", "JSON", "application/json", "json");
    public static TEXT = new HttpMockResponseBodyType("Text", "TEXT", "text/plain", "text");
    public static XML = new HttpMockResponseBodyType("XML", "XML", "application/xml", "xml");

    static readonly enums: Array<HttpMockResponseBodyType> = [
        HttpMockResponseBodyType.OTHER,
        HttpMockResponseBodyType.HTML,
        HttpMockResponseBodyType.JAVASCRIPT,
        HttpMockResponseBodyType.JSON,
        HttpMockResponseBodyType.TEXT,
        HttpMockResponseBodyType.XML
    ];

    readonly serialized: string;
    readonly contentType: string;
    readonly editorMode: string;
    private constructor(asString: string, serialized: string, contentType: string, editorMode: string) {
        super(asString);
        this.serialized = serialized;
        this.contentType = contentType;
        this.editorMode = editorMode;
    }

    public static fromString(methodAsString: string): HttpMockResponseBodyType {
        if(!methodAsString) {
            return null;
        }

        for (let enumValue of HttpMockResponseBodyType.enums) {
            if(enumValue.enumAsString.toUpperCase() == methodAsString.toUpperCase()) {
                return enumValue;
            }
        }

        return null;
    }

    public static fromContentType(contentType: string): HttpMockResponseBodyType {
        for (let enumValue of HttpMockResponseBodyType.enums) {
            if(enumValue.contentType == contentType) {
                return enumValue;
            }
        }

        return null;
    }
    public static fromSerialized(serialized: string): HttpMockResponseBodyType {
        for (let enumValue of HttpMockResponseBodyType.enums) {
            if(enumValue.serialized == serialized) {
                return enumValue;
            }
        }

        return null;
    }
}
