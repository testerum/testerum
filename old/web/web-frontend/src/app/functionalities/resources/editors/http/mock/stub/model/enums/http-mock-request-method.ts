

import {Enum} from "../../../../../../../../model/enums/enum.interface";

export class HttpMockRequestMethod extends Enum {

    public static GET = new HttpMockRequestMethod("GET", false);
    public static POST = new HttpMockRequestMethod("POST", true);
    public static PUT = new HttpMockRequestMethod("PUT", true);
    public static DELETE = new HttpMockRequestMethod("DELETE", false);
    public static HEAD = new HttpMockRequestMethod("HEAD", false);
    public static OPTIONS = new HttpMockRequestMethod("OPTIONS", false);
    public static CONNECT = new HttpMockRequestMethod("CONNECT", true);
    public static TRACE = new HttpMockRequestMethod("TRACE", false);
    public static PATCH = new HttpMockRequestMethod("PATCH", true);

    static readonly enums: Array<HttpMockRequestMethod> = [
        HttpMockRequestMethod.GET,
        HttpMockRequestMethod.POST,
        HttpMockRequestMethod.PUT,
        HttpMockRequestMethod.DELETE,
        HttpMockRequestMethod.HEAD,
        HttpMockRequestMethod.OPTIONS,
        HttpMockRequestMethod.CONNECT,
        HttpMockRequestMethod.TRACE,
        HttpMockRequestMethod.PATCH
    ];

    public readonly hasBody: boolean = true;
    private constructor(HttpMockRequestMethodAsString:string, hasBody: boolean) {
        super(HttpMockRequestMethodAsString);
        this.hasBody = hasBody;
    }

    public static fromString(methodAsString: string) {
        for (let enumValue of HttpMockRequestMethod.enums) {
            if(enumValue.enumAsString == methodAsString) {
                return enumValue;
            }
        }

        throw Error("Enum ["+methodAsString+"] is not defined")
    }
}
