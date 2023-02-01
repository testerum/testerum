import {Enum} from "../../../../../../../model/enums/enum.interface";

export class HttpResponseVerifyHeadersCompareMode extends Enum {

    public static EXACT_MATCH = new HttpResponseVerifyHeadersCompareMode("EXACT_MATCH", "EXACT MATCH");
    public static CONTAINS = new HttpResponseVerifyHeadersCompareMode("CONTAINS", "CONTAINS");
    public static REGEX_MATCH = new HttpResponseVerifyHeadersCompareMode("REGEX_MATCH", "MATCH REGEX");

    public static readonly enums: Array<HttpResponseVerifyHeadersCompareMode> = [
        HttpResponseVerifyHeadersCompareMode.EXACT_MATCH,
        HttpResponseVerifyHeadersCompareMode.CONTAINS,
        HttpResponseVerifyHeadersCompareMode.REGEX_MATCH,
    ];

    public readonly asSerialized: string;
    private constructor(asSerialized: string, asString: string) {
        super(asString);
        this.asSerialized = asSerialized;
    }

    public static fromString(methodAsString: string): HttpResponseVerifyHeadersCompareMode {
        for (let enumValue of HttpResponseVerifyHeadersCompareMode.enums) {
            if (enumValue.enumAsString == methodAsString) {
                return enumValue;
            }
        }

        return null;
    }

    public static fromSerialization(asSerialized: string): HttpResponseVerifyHeadersCompareMode {
        for (let enumValue of HttpResponseVerifyHeadersCompareMode.enums) {
            if (enumValue.asSerialized == asSerialized) {
                return enumValue;
            }
        }

        return null;
    }
}
