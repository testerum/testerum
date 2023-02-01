import {Enum} from "../../../../../../../../model/enums/enum.interface";

export class HttpMockRequestHeadersCompareMode extends Enum {

    public static EXACT_MATCH = new HttpMockRequestHeadersCompareMode("EXACT_MATCH", "EXACT MATCH");
    public static CONTAINS = new HttpMockRequestHeadersCompareMode("CONTAINS", "CONTAINS");
    public static REGEX_MATCH = new HttpMockRequestHeadersCompareMode("REGEX_MATCH", "MATCH REGEX");
    public static ABSENT = new HttpMockRequestHeadersCompareMode("ABSENT", "ABSENT");
    public static DOES_NOT_MATCH = new HttpMockRequestHeadersCompareMode("DOES_NOT_MATCH", "DOES NOT MATCH");

    public static readonly enums: Array<HttpMockRequestHeadersCompareMode> = [
        HttpMockRequestHeadersCompareMode.EXACT_MATCH,
        HttpMockRequestHeadersCompareMode.CONTAINS,
        HttpMockRequestHeadersCompareMode.REGEX_MATCH,
        HttpMockRequestHeadersCompareMode.ABSENT,
        HttpMockRequestHeadersCompareMode.DOES_NOT_MATCH,
    ];

    public readonly asSerialized: string;
    private constructor(asSerialized: string, asString: string) {
        super(asString);
        this.asSerialized = asSerialized;
    }

    public static fromString(methodAsString: string): HttpMockRequestHeadersCompareMode {
        for (let enumValue of HttpMockRequestHeadersCompareMode.enums) {
            if (enumValue.enumAsString == methodAsString) {
                return enumValue;
            }
        }

        return null;
    }

    public static fromSerialization(asSerialized: string): HttpMockRequestHeadersCompareMode {
        for (let enumValue of HttpMockRequestHeadersCompareMode.enums) {
            if (enumValue.asSerialized == asSerialized) {
                return enumValue;
            }
        }

        return null;
    }
}
