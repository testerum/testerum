

import {Enum} from "../../../../../../../../model/enums/enum.interface";

export class HttpMockRequestBodyMatchingType extends Enum {

    public static JSON_VERIFY = new HttpMockRequestBodyMatchingType("JSON_VERIFY", "JSON Verify");
    public static EXACT_MATCH = new HttpMockRequestBodyMatchingType("EXACT_MATCH", "Exact Match");
    public static CONTAINS    = new HttpMockRequestBodyMatchingType("CONTAINS", "Contains");
    public static REGEX_MATCH = new HttpMockRequestBodyMatchingType("REGEX_MATCH", "Match Regex");
    public static IS_EMPTY    = new HttpMockRequestBodyMatchingType("IS_EMPTY", "Is Empty");

    static readonly enums: Array<HttpMockRequestBodyMatchingType> = [
        HttpMockRequestBodyMatchingType.JSON_VERIFY,
        HttpMockRequestBodyMatchingType.EXACT_MATCH,
        HttpMockRequestBodyMatchingType.CONTAINS,
        HttpMockRequestBodyMatchingType.REGEX_MATCH ,
        HttpMockRequestBodyMatchingType.IS_EMPTY,
    ];

    public readonly asSerialized: string;
    private constructor(asSerialized: string, asString: string) {
        super(asString);
        this.asSerialized = asSerialized;
    }

    public static fromString(methodAsString: string): HttpMockRequestBodyMatchingType {
        for (let enumValue of HttpMockRequestBodyMatchingType.enums) {
            if(enumValue.enumAsString.toUpperCase() == methodAsString.toUpperCase()) {
                return enumValue;
            }
        }

        return null;
    }

    public static fromSerialization(asSerialized: string): HttpMockRequestBodyMatchingType {
        for (let enumValue of HttpMockRequestBodyMatchingType.enums) {
            if(enumValue.asSerialized.toUpperCase() == asSerialized.toUpperCase()) {
                return enumValue;
            }
        }

        return null;
    }
}
