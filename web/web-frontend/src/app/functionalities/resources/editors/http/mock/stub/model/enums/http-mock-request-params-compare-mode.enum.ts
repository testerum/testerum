import {Enum} from "../../../../../../../../model/enums/enum.interface";

export class HttpMockRequestParamsCompareMode extends Enum {

    public static EXACT_MATCH = new HttpMockRequestParamsCompareMode("EXACT_MATCH", "EXACT MATCH");
    public static CONTAINS = new HttpMockRequestParamsCompareMode("CONTAINS", "CONTAINS");
    public static REGEX_MATCH = new HttpMockRequestParamsCompareMode("REGEX_MATCH", "MATCH REGEX");
    public static ABSENT = new HttpMockRequestParamsCompareMode("ABSENT", "ABSENT");
    public static DOES_NOT_MATCH = new HttpMockRequestParamsCompareMode("DOES_NOT_MATCH", "DOES NOT MATCH");

    public static readonly enums: Array<HttpMockRequestParamsCompareMode> = [
        HttpMockRequestParamsCompareMode.EXACT_MATCH,
        HttpMockRequestParamsCompareMode.CONTAINS,
        HttpMockRequestParamsCompareMode.REGEX_MATCH,
        HttpMockRequestParamsCompareMode.ABSENT,
        HttpMockRequestParamsCompareMode.DOES_NOT_MATCH,
    ];

    public readonly asSerialized: string;
    private constructor(asSerialized: string, asString: string) {
        super(asString);
        this.asSerialized = asSerialized;
    }

    public static fromString(methodAsString: string): HttpMockRequestParamsCompareMode {
        for (let enumValue of HttpMockRequestParamsCompareMode.enums) {
            if (enumValue.enumAsString == methodAsString) {
                return enumValue;
            }
        }

        return null;
    }

    public static fromSerialization(asSerialized: string): HttpMockRequestParamsCompareMode {
        for (let enumValue of HttpMockRequestParamsCompareMode.enums) {
            if (enumValue.asSerialized == asSerialized) {
                return enumValue;
            }
        }

        return null;
    }
}
