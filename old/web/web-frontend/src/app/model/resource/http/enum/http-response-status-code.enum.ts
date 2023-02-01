
import {Enum} from "../../../enums/enum.interface";

export class HttpResponseStatusCode extends Enum {

    public static statusCode200 = new HttpResponseStatusCode(200, "OK");
    public static statusCode201 = new HttpResponseStatusCode(201, "Created");
    public static statusCode202 = new HttpResponseStatusCode(202, "Accepted");
    public static statusCode203 = new HttpResponseStatusCode(203, "Non-Authoritative Information");
    public static statusCode204 = new HttpResponseStatusCode(204, "No Content");
    public static statusCode205 = new HttpResponseStatusCode(205, "Reset Content");
    public static statusCode206 = new HttpResponseStatusCode(206, "Partial Content");
    public static statusCode207 = new HttpResponseStatusCode(207, "Multi-Status");
    public static statusCode208 = new HttpResponseStatusCode(208, "Already Reported");
    public static statusCode226 = new HttpResponseStatusCode(226, "IM Used");
    public static statusCode300 = new HttpResponseStatusCode(300, "Multiple Choices");
    public static statusCode301 = new HttpResponseStatusCode(301, "Moved Permanently");
    public static statusCode302 = new HttpResponseStatusCode(302, "Found");
    public static statusCode303 = new HttpResponseStatusCode(303, "See Other");
    public static statusCode304 = new HttpResponseStatusCode(304, "Not Modified");
    public static statusCode305 = new HttpResponseStatusCode(305, "Use Proxy");
    public static statusCode306 = new HttpResponseStatusCode(306, "Switch Proxy");
    public static statusCode307 = new HttpResponseStatusCode(307, "Temporary Redirect");
    public static statusCode308 = new HttpResponseStatusCode(308, "Permanent Redirect");
    public static statusCode400 = new HttpResponseStatusCode(400, "Bad Request");
    public static statusCode401 = new HttpResponseStatusCode(401, "Unauthorized");
    public static statusCode402 = new HttpResponseStatusCode(402, "Payment Required");
    public static statusCode403 = new HttpResponseStatusCode(403, "Forbidden");
    public static statusCode404 = new HttpResponseStatusCode(404, "Not Found");
    public static statusCode405 = new HttpResponseStatusCode(405, "Method Not Allowed");
    public static statusCode406 = new HttpResponseStatusCode(406, "Not Acceptable");
    public static statusCode407 = new HttpResponseStatusCode(407, "Proxy Authentication Required");
    public static statusCode408 = new HttpResponseStatusCode(408, "Request Timeout");
    public static statusCode409 = new HttpResponseStatusCode(409, "Conflict");
    public static statusCode410 = new HttpResponseStatusCode(410, "Gone");
    public static statusCode411 = new HttpResponseStatusCode(411, "Length Required");
    public static statusCode412 = new HttpResponseStatusCode(412, "Precondition Failed");
    public static statusCode413 = new HttpResponseStatusCode(413, "Payload Too Large");
    public static statusCode414 = new HttpResponseStatusCode(414, "URI Too Long");
    public static statusCode415 = new HttpResponseStatusCode(415, "Unsupported Media Type");
    public static statusCode416 = new HttpResponseStatusCode(416, "Range Not Satisfiable");
    public static statusCode417 = new HttpResponseStatusCode(417, "Expectation Failed");
    public static statusCode418 = new HttpResponseStatusCode(418, "I'm a teapot");
    public static statusCode421 = new HttpResponseStatusCode(421, "Misdirected Request");
    public static statusCode422 = new HttpResponseStatusCode(422, "Unprocessable Entity");
    public static statusCode423 = new HttpResponseStatusCode(423, "Locked");
    public static statusCode424 = new HttpResponseStatusCode(424, "Failed Dependency");
    public static statusCode425 = new HttpResponseStatusCode(425, "Upgrade Required");
    public static statusCode428 = new HttpResponseStatusCode(428, "Precondition Required");
    public static statusCode429 = new HttpResponseStatusCode(429, "Too Many Requests");
    public static statusCode431 = new HttpResponseStatusCode(431, "Request Header Fields Too Large");
    public static statusCode451 = new HttpResponseStatusCode(451, "Unavailable For Legal Reasons");
    public static statusCode500 = new HttpResponseStatusCode(500, "Internal Server Error");
    public static statusCode501 = new HttpResponseStatusCode(501, "Not Implemented");
    public static statusCode502 = new HttpResponseStatusCode(502, "Bad Gateway");
    public static statusCode503 = new HttpResponseStatusCode(503, "Service Unavailable");
    public static statusCode504 = new HttpResponseStatusCode(504, "Gateway Timeout");
    public static statusCode505 = new HttpResponseStatusCode(505, "HTTP Version Not Supported");
    public static statusCode506 = new HttpResponseStatusCode(506, "Variant Also Negotiates");
    public static statusCode507 = new HttpResponseStatusCode(507, "Insufficient Storage");
    public static statusCode508 = new HttpResponseStatusCode(508, "Loop Detected");
    public static statusCode510 = new HttpResponseStatusCode(510, "Not Extended");
    public static statusCode511 = new HttpResponseStatusCode(511, "Network Authentication Required");

    static readonly enums: Array<HttpResponseStatusCode> = [
        HttpResponseStatusCode.statusCode200,
        HttpResponseStatusCode.statusCode201,
        HttpResponseStatusCode.statusCode202,
        HttpResponseStatusCode.statusCode203,
        HttpResponseStatusCode.statusCode204,
        HttpResponseStatusCode.statusCode205,
        HttpResponseStatusCode.statusCode206,
        HttpResponseStatusCode.statusCode207,
        HttpResponseStatusCode.statusCode208,
        HttpResponseStatusCode.statusCode226,
        HttpResponseStatusCode.statusCode300,
        HttpResponseStatusCode.statusCode301,
        HttpResponseStatusCode.statusCode302,
        HttpResponseStatusCode.statusCode303,
        HttpResponseStatusCode.statusCode304,
        HttpResponseStatusCode.statusCode305,
        HttpResponseStatusCode.statusCode306,
        HttpResponseStatusCode.statusCode307,
        HttpResponseStatusCode.statusCode308,
        HttpResponseStatusCode.statusCode400,
        HttpResponseStatusCode.statusCode401,
        HttpResponseStatusCode.statusCode402,
        HttpResponseStatusCode.statusCode403,
        HttpResponseStatusCode.statusCode404,
        HttpResponseStatusCode.statusCode405,
        HttpResponseStatusCode.statusCode406,
        HttpResponseStatusCode.statusCode407,
        HttpResponseStatusCode.statusCode408,
        HttpResponseStatusCode.statusCode409,
        HttpResponseStatusCode.statusCode410,
        HttpResponseStatusCode.statusCode411,
        HttpResponseStatusCode.statusCode412,
        HttpResponseStatusCode.statusCode413,
        HttpResponseStatusCode.statusCode414,
        HttpResponseStatusCode.statusCode415,
        HttpResponseStatusCode.statusCode416,
        HttpResponseStatusCode.statusCode417,
        HttpResponseStatusCode.statusCode418,
        HttpResponseStatusCode.statusCode421,
        HttpResponseStatusCode.statusCode422,
        HttpResponseStatusCode.statusCode423,
        HttpResponseStatusCode.statusCode424,
        HttpResponseStatusCode.statusCode425,
        HttpResponseStatusCode.statusCode428,
        HttpResponseStatusCode.statusCode429,
        HttpResponseStatusCode.statusCode431,
        HttpResponseStatusCode.statusCode451,
        HttpResponseStatusCode.statusCode500,
        HttpResponseStatusCode.statusCode501,
        HttpResponseStatusCode.statusCode502,
        HttpResponseStatusCode.statusCode503,
        HttpResponseStatusCode.statusCode504,
        HttpResponseStatusCode.statusCode505,
        HttpResponseStatusCode.statusCode506,
        HttpResponseStatusCode.statusCode507,
        HttpResponseStatusCode.statusCode508,
        HttpResponseStatusCode.statusCode510,
        HttpResponseStatusCode.statusCode511
    ];

    readonly statusCode: number;
    private constructor(statusCode: number, asString: string) {
        super(asString);
        this.statusCode = statusCode;
    }

    public static fromString(methodAsString: string): HttpResponseStatusCode {
        for (let enumValue of HttpResponseStatusCode.enums) {
            if(enumValue.enumAsString == methodAsString) {
                return enumValue;
            }
        }
        return null;
    }

    public static fromStatusCodeNumber(statusCode: number): HttpResponseStatusCode {
        for (let enumValue of HttpResponseStatusCode.enums) {
            if(enumValue.statusCode == statusCode) {
                return enumValue;
            }
        }
        return null;
    }
}
