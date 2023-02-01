
import {Enum} from "../../../enums/enum.interface";
import {HttpMethod} from "../../../enums/http-method-enum";

export class HttpRequestBodyType extends Enum {
    public static RAW = new HttpRequestBodyType("raw", null);
    public static X_WWW_FORM_URLENCODED = new HttpRequestBodyType("x-www-form-urlencoded", "application/x-www-form-urlencoded");
    public static FORM_DATA = new HttpRequestBodyType("form-data", null);
    public static BINARY = new HttpRequestBodyType("binary", null);

    static readonly enums: Array<HttpRequestBodyType> = [
        HttpRequestBodyType.RAW,
        HttpRequestBodyType.X_WWW_FORM_URLENCODED,
        HttpRequestBodyType.FORM_DATA,
        HttpRequestBodyType.BINARY,
    ];

    public readonly contentTypeHeaderValue: string;
    private constructor(httpMethodAsString: string, contentTypeHeaderValue: string) {
        super(httpMethodAsString);
        this.contentTypeHeaderValue = contentTypeHeaderValue;
    }

    public static fromString(methodAsString: string) {
        for (let enumValue of HttpRequestBodyType.enums) {
            let methodAsStringLowerCase = methodAsString.toLowerCase();
            if(enumValue.enumAsString.toLowerCase() == methodAsStringLowerCase) {
                return enumValue;
            }
        }

        throw Error("Enum ["+methodAsString+"] is not defined")
    }
}
