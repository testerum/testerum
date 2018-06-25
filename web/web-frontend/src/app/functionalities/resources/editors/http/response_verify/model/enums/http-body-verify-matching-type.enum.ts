

import {Enum} from "../../../../../../../model/enums/enum.interface";

export class HttpBodyVerifyMatchingType extends Enum {

    public static JSON_VERIFY = new HttpBodyVerifyMatchingType("JSON_VERIFY", "JSON Verify");
    public static EXACT_MATCH = new HttpBodyVerifyMatchingType("EXACT_MATCH", "Exact Match");
    public static CONTAINS    = new HttpBodyVerifyMatchingType("CONTAINS", "Contains");
    public static REGEX_MATCH = new HttpBodyVerifyMatchingType("REGEX_MATCH", "Match Regex");
    public static IS_EMPTY    = new HttpBodyVerifyMatchingType("IS_EMPTY", "Is Empty");

    static readonly enums: Array<HttpBodyVerifyMatchingType> = [
        HttpBodyVerifyMatchingType.JSON_VERIFY,
        HttpBodyVerifyMatchingType.EXACT_MATCH,
        HttpBodyVerifyMatchingType.CONTAINS,
        HttpBodyVerifyMatchingType.REGEX_MATCH ,
        HttpBodyVerifyMatchingType.IS_EMPTY,
    ];

    public readonly asSerialized: string;
    private constructor(asSerialized: string, asString: string) {
        super(asString);
        this.asSerialized = asSerialized;
    }

    public static fromString(methodAsString: string): HttpBodyVerifyMatchingType {
        for (let enumValue of HttpBodyVerifyMatchingType.enums) {
            if(enumValue.enumAsString.toUpperCase() == methodAsString.toUpperCase()) {
                return enumValue;
            }
        }

        return null;
    }

    public static fromSerialization(asSerialized: string): HttpBodyVerifyMatchingType {
        for (let enumValue of HttpBodyVerifyMatchingType.enums) {
            if(enumValue.asSerialized.toUpperCase() == asSerialized.toUpperCase()) {
                return enumValue;
            }
        }

        return null;
    }
}
