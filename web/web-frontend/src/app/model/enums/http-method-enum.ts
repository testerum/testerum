
import {Enum} from "./enum.interface";

export class HttpMethod extends Enum {

    public static GET = new HttpMethod("GET");
    public static POST = new HttpMethod("POST");
    public static PUT = new HttpMethod("PUT");
    public static DELETE = new HttpMethod("DELETE");
    public static HEAD = new HttpMethod("HEAD");
    public static OPTIONS = new HttpMethod("OPTIONS");
    public static CONNECT = new HttpMethod("CONNECT");
    public static TRACE = new HttpMethod("TRACE");
    public static PATCH = new HttpMethod("PATCH");

    static readonly enums: Array<HttpMethod> = [
        HttpMethod.GET,
        HttpMethod.POST,
        HttpMethod.PUT,
        HttpMethod.DELETE,
        HttpMethod.HEAD,
        HttpMethod.OPTIONS,
        HttpMethod.CONNECT,
        HttpMethod.TRACE,
        HttpMethod.PATCH
    ];

    private constructor(httpMethodAsString:string) {
        super(httpMethodAsString);
    }

    public static fromString(methodAsString: string) {
        for (let enumValue of HttpMethod.enums) {
            if(enumValue.enumAsString == methodAsString) {
                return enumValue;
            }
        }

        throw Error("Enum ["+methodAsString+"] is not defined")
    }
}
