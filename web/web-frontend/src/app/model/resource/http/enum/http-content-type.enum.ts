
import {Enum} from "../../../enums/enum.interface";

export class HttpContentType extends Enum {

    public static CONTENT_TYPE_HEADER_KEY: string = "Content-Type";

    public static TEXT = new HttpContentType("Text", "text/plain", "text");
    public static JSON = new HttpContentType("JSON", "application/json", "json");
    public static JAVASCRIPT = new HttpContentType("Javascript", "application/javascript", "javascript");
    public static XML = new HttpContentType("XML", "application/xml", "xml");
    public static XML_TEXT = new HttpContentType("XML", "text/xml", "xml");
    public static HTML = new HttpContentType("HTML", "text/html", "html");

    static readonly enums: Array<HttpContentType> = [
        HttpContentType.TEXT,
        HttpContentType.JSON,
        HttpContentType.JAVASCRIPT,
        HttpContentType.XML,
        HttpContentType.XML_TEXT,
        HttpContentType.HTML
    ];

    readonly contentType: string;
    readonly editorMode: string;
    private constructor(asString: string, contentType: string, editorMode: string) {
        super(asString);
        this.contentType = contentType;
        this.editorMode = editorMode;
    }

    public static fromString(methodAsString: string): HttpContentType {
        for (let enumValue of HttpContentType.enums) {
            if(enumValue.enumAsString == methodAsString) {
                return enumValue;
            }
        }

        return null;
    }
    public static fromContentType(contentType: string): HttpContentType {
        for (let enumValue of HttpContentType.enums) {
            if(enumValue.contentType == contentType) {
                return enumValue;
            }
        }

        return null;
    }
}
