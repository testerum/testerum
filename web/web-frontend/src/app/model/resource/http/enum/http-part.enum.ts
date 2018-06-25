import {Enum} from "../../../enums/enum.interface";

export class HttpPart extends Enum {
    public static URL = new HttpPart("url");
    public static HEADERS = new HttpPart("headers");
    public static BODY = new HttpPart("body");
    public static RESPONSE = new HttpPart("response");

    static readonly enums: Array<HttpPart> = [
        HttpPart.URL,
        HttpPart.HEADERS,
        HttpPart.BODY
    ];

    private constructor(asString: string) {
        super(asString);
    }

    public static fromString(methodAsString: string) {
        for (let enumValue of HttpPart.enums) {
            if(enumValue.enumAsString == methodAsString) {
                return enumValue;
            }
        }

        throw Error("Enum ["+methodAsString+"] is not defined")
    }
}
